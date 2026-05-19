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

코드 생성 및 수정 시 다음 우선순위를 따른다.

1. AGENTS.md
2. `docs/*.md`
3. 기존 프로젝트 코드 및 구조
4. 일반 예제 / 외부 자료

---

## Task Execution Pattern (IMPORTANT)

모든 작업은 TODO 기반으로 수행한다.

- 작업 시작 전 TODO를 생성하거나 기존 TODO를 읽는다
- TODO는 3~7개 수준으로 유지한다
- 한 번에 하나의 TODO만 수행한다
- 수행 후 TODO 상태를 갱신한다
- 실패 시 TODO를 수정하고 원인을 반영한다
- 완료 기준은 Validation 성공이다

---

## Maven Rules

- dependency version 우선순위:
  1. Spring Boot BOM
  2. root `dependencyManagement`
  3. 필요한 경우만 명시 (이유 포함)

- 멀티 모듈 규칙:
  - root는 `packaging = pom`
  - 모든 모듈은 root parent 사용
  - 신규 모듈은 `<modules>`에 추가
  - 모듈 간 dependency는 project coordinates 사용
  - 불필요한 모듈 수정 금지

---

## OpenRewrite Rules

- 변경은 Recipe 기반으로 수행
- 수동 코드 수정 최소화
- 대상 버전(JDK17 / Spring Boot 3.x)에 맞춰 작성

---

## Execution Rules (Codex)

- workspace root 기준으로 작업
- 단순 조회에 대해 권한 상승 금지
- 실패한 명령은 1회만 재시도
- 동일 실패 반복 금지
- 불확실하면 추측하지 말고 질문

---

## Validation

모든 변경 후 반드시 실행:

```bash
mvn -q -DskipTests compile
````

필요 시:

```bash
mvn test
```

* 실패 시 우회하지 않고 원인 수정