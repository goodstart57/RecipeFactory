# Spring Batch Writer `run(List)` -> `run(Chunk)` Plan

## Summary

Spring Batch 5 마이그레이션 과정에서 `ItemWriter#write`는 `Chunk`를 받도록 바뀌었지만, `BaseWriter` 템플릿 패턴의 내부 확장 포인트인 `run(List)`는 그대로 남아 컴파일 에러가 발생할 수 있다.

이번 변경은 `ItemWriter#write(...)`가 내부에서 `run(...)`을 호출하는 writer 패턴만 대상으로 잡고, `run(List)` 시그니처와 본문 사용 패턴을 `Chunk` 기반으로 일괄 변환하는 OpenRewrite Recipe를 추가하는 것이다.

## Key Changes

- `rewrite-recipes` 모듈에 `SpringBatchItemWriterRunListToChunkRecipe`를 추가한다.
- 변환 대상은 다음 조건을 만족하는 `run(List ...)` 메서드로 제한한다.
  - 메서드명이 `run`
  - 파라미터가 1개인 `List` 계열
  - 같은 클래스가 `ItemWriter` 계열이거나 `write(...)` 안에서 `run(...)`을 호출하는 writer 패턴
- 대상 메서드의 파라미터 타입을 `Chunk`로 변경한다.
  - 예: `run(List<? extends T> items)` -> `run(Chunk<? extends T> items)`
- `run` 본문에서 `List` 전용 API를 쓰는 부분만 `chunk.getItems()`로 변환한다.
  - `items.stream()` -> `items.getItems().stream()`
  - `items.get(0)` -> `items.getItems().get(0)`
  - `contains`, `subList`, `listIterator`, `toArray` 등도 동일 원칙 적용
- `Chunk` 자체가 제공하는 API는 그대로 유지한다.
  - `isEmpty`, `size`
  - enhanced-for 기반 반복
- 필요한 import를 추가한다.
  - `org.springframework.batch.item.Chunk`
- `java.util.List` import는 현재 구현 기준으로 보수적으로 유지한다.
  - 이후 import 정리 Recipe를 별도로 두는 것이 안전하다.

## Implementation Notes

- 구현 위치:
  - `rewrite-recipes/src/main/java/com/ljs/rfactory/recipe/SpringBatchItemWriterRunListToChunkRecipe.java`
- 테스트 위치:
  - `rewrite-recipes/src/test/java/com/ljs/rfactory/recipe/SpringBatchItemWriterRunListToChunkRecipeTest.java`
- 테스트/실행 지원을 위해 `rewrite-recipes/pom.xml`에 다음을 추가한다.
  - `rewrite-test`
  - `junit-jupiter`
  - Jackson test 의존성
  - `maven-surefire-plugin`
  - `maven-compiler-plugin`의 `<proc>none</proc>`
- OpenRewrite 템플릿으로 제네릭 파라미터를 재구성하는 과정에서 테스트 타입 검증이 과도하게 엄격해지는 문제가 있어, 테스트는 `TypeValidation.none()`으로 결과 중심 검증을 사용한다.

## Test Plan

- positive
  - `BaseWriter`가 `write(Chunk)`에서 `run(items)`를 호출하는 경우
  - abstract `run(List<? extends T>)`가 `run(Chunk<? extends T>)`로 변환되는지 확인
  - override 메서드도 함께 변환되는지 확인
  - `stream`, `get(index)`가 `getItems()` 기반으로 바뀌는지 확인
- negative
  - writer 패턴과 무관한 일반 `run(List<String>)` 메서드는 변경하지 않는지 확인

## Validation

- 실행 완료:
  - `mvn -q -pl rewrite-recipes test`
  - `mvn -q -DskipTests compile`
- 두 명령 모두 성공했다.

## Assumptions

- 대상은 Spring Batch 5 업그레이드 중 `BaseWriter` 스타일 커스텀 writer 코드다.
- `run`은 프레임워크 계약 메서드가 아니라 writer 내부 확장 포인트다.
- 전역 `List` 타입 승격은 하지 않고, `run` 메서드 파라미터와 그 본문 사용만 국소적으로 바꾼다.
