# docs/spring-boot-rules.md

## Application code

- Use constructor injection.
- Prefer immutable dependencies with `final`.
- Keep controllers thin.
- Put business logic in service classes.
- Put persistence logic in repository classes.
- Do not place domain logic in controllers.

## Configuration

- Use `application.yml` for environment-neutral defaults.
- Use profiles only for environment-specific values.
- Bind structured config with `@ConfigurationProperties`.