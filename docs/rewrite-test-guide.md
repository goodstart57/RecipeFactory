# Rewrite Test Guide

이 문서는 `RecipeFactory`에서 OpenRewrite Recipe를 Maven 명령어로 테스트하는 기준 절차를 정리한다.

## 목적

- 업그레이드 대상 샘플 모듈에 Recipe를 적용한다
- 변경 내용을 적용 전 미리 확인한다
- 필요한 경우 실제 소스에 반영한다

현재 저장소 기준 테스트 대상 모듈:

- `sample-batch-before`

## 사전 조건

- Maven 사용 가능
- JDK 설치 완료
- 프로젝트 루트에서 명령 실행

권장 확인:

```bash
mvn -version
mvn -pl sample-batch-before compile
```

## 실행 방식

이 저장소에는 아직 `rewrite-maven-plugin`이 POM에 고정되어 있지 않다.
따라서 테스트는 다음 두 방식 중 하나로 진행한다.

1. 일회성으로 plugin 좌표를 직접 지정해 실행
2. 반복 작업이 많아지면 POM에 plugin을 추가한 뒤 `rewrite:*` goal 실행

## 1. 일회성 테스트

### 사용 가능한 Recipe 조회

```bash
mvn -pl sample-batch-before org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.discover=true
```

### 특정 Recipe dry run

예시:

```bash
mvn -pl sample-batch-before org.openrewrite.maven:rewrite-maven-plugin:dryRun \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17
```

여러 Recipe를 함께 테스트할 때는 쉼표로 연결한다.

```bash
mvn -pl sample-batch-before org.openrewrite.maven:rewrite-maven-plugin:dryRun \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0
```

### 실제 소스에 적용

```bash
mvn -pl sample-batch-before org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17
```

적용 후 반드시 컴파일로 확인한다.

```bash
mvn -pl sample-batch-before compile
```

## 2. 반복 실행용 plugin 등록 방식

반복 테스트가 많다면 `sample-batch-before/pom.xml` 또는 root POM에 `rewrite-maven-plugin`을 추가한다.

예시:

```xml
<plugin>
  <groupId>org.openrewrite.maven</groupId>
  <artifactId>rewrite-maven-plugin</artifactId>
  <version>6.16.0</version>
</plugin>
```

등록 후에는 아래처럼 짧게 실행할 수 있다.

```bash
mvn -pl sample-batch-before rewrite:discover
mvn -pl sample-batch-before rewrite:dryRun -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17
mvn -pl sample-batch-before rewrite:run -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17
```

## 권장 테스트 순서

1. `compile`로 기준선 확인
2. `rewrite:discover` 또는 `-Drewrite.discover=true`로 후보 Recipe 확인
3. `dryRun`으로 변경 예정 내용 검토
4. `run`으로 실제 반영
5. 다시 `compile` 실행
6. 필요 시 배치 기동까지 확인

예시:

```bash
mvn -pl sample-batch-before compile
mvn -pl sample-batch-before org.openrewrite.maven:rewrite-maven-plugin:dryRun -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17
mvn -pl sample-batch-before org.openrewrite.maven:rewrite-maven-plugin:run -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17
mvn -pl sample-batch-before compile
mvn -pl sample-batch-before spring-boot:run "-Dspring-boot.run.arguments=--spring.batch.job.names=sample_employeeJob"
```

## 결과 확인 포인트

- `pom.xml` dependency / plugin 변경 여부
- `javax.*` -> `jakarta.*` 전환 여부
- Spring Boot / Spring Batch API 변경 반영 여부
- 컴파일 성공 여부
- 배치 잡 실행 성공 여부

## 주의 사항

- 먼저 `dryRun`으로 변경 범위를 확인한다
- 여러 업그레이드 Recipe를 한 번에 적용하면 원인 추적이 어려워질 수 있다
- 모듈 단위(`-pl sample-batch-before`)로 좁혀서 실행하는 편이 안전하다
- 적용 후에는 반드시 Maven compile 또는 실제 배치 실행으로 검증한다
