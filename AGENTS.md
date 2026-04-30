# AGENTS.md

## Project Overview

이 프로젝트는 OpenRewrite를 활용해 JDK 및 Spring 생태계 버전 업그레이드에 필요한 Recipe를 작성하고 테스트하기 위한 저장소다.

대상 버전 변경은 다음과 같다.

- JDK: `1.8` -> `17`
- Spring Framework: `5.2.8.RELEASE` -> `6.2.18`
- Spring Boot: `2.3.3.RELEASE` -> `3.5.14`
- Spring Batch: `4.2.4.RELEASE` -> `5.2.5`
- Spring Cloud: `Hoxton.SR7` -> `2025.0.2`

---

## Rule Priority

코드 생성 및 수정 시 다음 규칙을 반드시 우선 적용한다.

1. 이 파일 (AGENTS.md)
2. `docs/*.md` (존재하는 경우)
3. 기존 프로젝트 코드 및 구조
4. 일반적인 예제나 인터넷 자료

---

## Maven Rules

- dependency version은 직접 선언하지 말고 다음 우선순위를 따른다:
  1. Spring Boot BOM (`spring-boot-dependencies`)
  2. root `dependencyManagement`
  3. 불가피한 경우에만 명시적 version 사용 (이유 필요)

- 멀티 모듈 구조 규칙:
  - root 프로젝트는 `packaging = pom`
  - 모든 모듈은 root POM을 parent로 가진다
  - 신규 모듈은 반드시 root `<modules>`에 추가
  - 모듈 간 의존성은 project coordinates 사용
  - 다른 모듈은 불필요하게 수정하지 않는다

---

## Spring Rules

- field injection 금지 → constructor injection 사용
- `final` 필드 우선 사용
- controller에는 비즈니스 로직을 넣지 않는다
- 설정은 `@ConfigurationProperties` 사용
- 불필요한 Bean / Annotation 추가 금지
- 새로운 dependency 추가 시 반드시 필요성 설명

---

## OpenRewrite Rules

- 업그레이드 작업은 OpenRewrite Recipe 기반으로 수행한다
- 수동 코드 수정은 최소화한다
- 변경은 반드시 대상 버전 스펙(JDK17 / Spring Boot 3.x)에 맞춰야 한다

---

## Execution Rules (Codex)

- workspace root를 기준으로 작업한다
- 단순 조회 작업에 대해 권한 상승 금지
- 실패한 명령은 1회만 재시도
- 동일한 실패 반복 금지
- 불확실한 경우 추측하지 말고 질문한다

---

## Validation

코드 변경 후 반드시 수행:

```bash
mvn -q -DskipTests compile
```