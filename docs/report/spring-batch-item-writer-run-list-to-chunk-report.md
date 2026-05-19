# Spring Batch Writer `run(List)` -> `run(Chunk)` Report

## Overview

- 작업 대상: `rewrite-recipes`
- 추가된 레시피: `com.ljs.rfactory.recipe.SpringBatchItemWriterRunListToChunkRecipe`
- 목적:
  - Spring Batch 5 마이그레이션 시 `ItemWriter#write(Chunk)`로 바뀐 이후에도 내부 템플릿 메서드 `run(List)`가 남아 발생하는 컴파일 오류를 일괄 변환

## Used Recipe

- Recipe class:
  - `com.ljs.rfactory.recipe.SpringBatchItemWriterRunListToChunkRecipe`
- 동작 범위:
  - `ItemWriter` 계열이거나 `write(...)` 내부에서 `run(...)`을 호출하는 writer 패턴의 `run(List ...)`
- 주요 변환:
  - `run(List<? extends T> items)` -> `run(Chunk<? extends T> items)`
  - `items.stream()` -> `items.getItems().stream()`
  - `items.get(0)` -> `items.getItems().get(0)`
  - `items.isEmpty()`, `items.size()` 등 `Chunk`가 직접 제공하는 API는 유지

## Validation

실행한 명령:

```bash
mvn -q -pl rewrite-recipes test
mvn -q -DskipTests compile
```

결과:

- `mvn -q -pl rewrite-recipes test` 성공
- `mvn -q -DskipTests compile` 성공

추가 확인:

```bash
rg -n "class BaseWriter|abstract class BaseWriter|void run\(|protected abstract void run\(|implements ItemWriter|write\(Chunk|write\(List" sample-batch-before -S
```

- `sample-batch-before`에는 이번 레시피가 직접 적용될 `BaseWriter` 패턴 소스가 없어 매치 결과가 없었다.
- 따라서 실제 변환 결과는 단위 테스트 fixture 기반 diff로 검증했다.

## Changed Files

- `rewrite-recipes/pom.xml`
- `rewrite-recipes/src/main/java/com/ljs/rfactory/recipe/SpringBatchItemWriterRunListToChunkRecipe.java`
- `rewrite-recipes/src/test/java/com/ljs/rfactory/recipe/SpringBatchItemWriterRunListToChunkRecipeTest.java`

## Build/Test Support Diff

`rewrite-recipes/pom.xml`

```diff
@@
+    <dependency>
+      <groupId>org.openrewrite</groupId>
+      <artifactId>rewrite-test</artifactId>
+      <version>${rewrite.version}</version>
+      <scope>test</scope>
+    </dependency>
+    <dependency>
+      <groupId>org.junit.jupiter</groupId>
+      <artifactId>junit-jupiter</artifactId>
+      <version>5.10.2</version>
+      <scope>test</scope>
+    </dependency>
+    <dependency>
+      <groupId>com.fasterxml.jackson.core</groupId>
+      <artifactId>jackson-databind</artifactId>
+      <version>2.17.1</version>
+      <scope>test</scope>
+    </dependency>
+    <dependency>
+      <groupId>com.fasterxml.jackson.core</groupId>
+      <artifactId>jackson-core</artifactId>
+      <version>2.17.1</version>
+      <scope>test</scope>
+    </dependency>
+    <dependency>
+      <groupId>com.fasterxml.jackson.core</groupId>
+      <artifactId>jackson-annotations</artifactId>
+      <version>2.17.1</version>
+      <scope>test</scope>
+    </dependency>
@@
+        <configuration>
+          <proc>none</proc>
+        </configuration>
+      </plugin>
+      <plugin>
+        <groupId>org.apache.maven.plugins</groupId>
+        <artifactId>maven-surefire-plugin</artifactId>
+        <version>3.2.5</version>
```

## Representative Source Diff

단위 테스트 `migratesBaseWriterPatternAndListOnlyUsages`에서 검증한 대표 변환:

```diff
 abstract class BaseWriter<T> implements ItemWriter<T> {
     @Override
     public void write(Chunk<? extends T> items) throws Exception {
         run(items);
     }
 
-    protected abstract void run(List<? extends T> items) throws Exception;
+    protected abstract void run(Chunk<? extends T> items) throws Exception;
 }
 
 class SampleWriter extends BaseWriter<String> {
     @Override
-    protected void run(List<? extends String> items) throws Exception {
+    protected void run(Chunk<? extends String> items) throws Exception {
         if (items.isEmpty()) {
             return;
         }
-        items.stream().forEach(System.out::println);
-        System.out.println(items.get(0));
+        items.getItems().stream().forEach(System.out::println);
+        System.out.println(items.getItems().get(0));
     }
 }
```

## Test Coverage

- positive
  - writer 패턴의 abstract `run(List<? extends T>)` 변환
  - override `run(List<? extends String>)` 변환
  - `stream`, `get(index)`의 `getItems()` 치환
- negative
  - 일반 `UtilityRunner#run(List<String>)`는 변경하지 않음

## Notes

- 현재 구현은 `java.util.List` import를 보수적으로 유지한다.
- 실제 샘플 모듈에 적용 가능한 writer 패턴 소스가 추가되면, 별도 `rewrite:dryRun` 보고서를 이어서 작성하는 것이 적절하다.
