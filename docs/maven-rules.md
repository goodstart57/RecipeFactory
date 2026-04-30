# docs/maven-rules.md

## Dependency version policy

Use this priority:

1. Versions managed by `spring-boot-dependencies`
2. Versions managed by root `dependencyManagement`
3. Explicit version only when unmanaged and justified

Do not add `<version>` to dependencies already managed by Spring Boot BOM.

## Multi-module policy

- Root project packaging must be `pom`.
- Each module must have a parent reference to the root POM.
- Module dependencies must use project coordinates.
- Do not duplicate plugin versions in child modules.