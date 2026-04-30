# ConfirmRecipe dryRun Report

## Summary

- Date: `2026-04-30`
- Module under test: `sample-batch-before`
- Active recipe: `com.ljs.rfactory.batch.before.ConfirmRecipeTest`
- Custom Java recipe: `com.ljs.rfactory.recipe.ConfirmRecipe`
- Result: `SUCCESS`

## Executed command

```bash
mvn -pl sample-batch-before rewrite:dryRun "-Drewrite.activeRecipes=com.ljs.rfactory.batch.before.ConfirmRecipeTest"
```

## Preconditions

- `rewrite-recipes` module was installed to the local Maven repository so the rewrite plugin could resolve the custom recipe jar.
- `sample-batch-before/pom.xml` was updated with `rewrite-maven-plugin` `configLocation` so the representative YAML recipe could be discovered reliably.

## dryRun outcome

- OpenRewrite discovered and executed `com.ljs.rfactory.batch.before.ConfirmRecipeTest`.
- The recipe chain invoked `com.ljs.rfactory.recipe.ConfirmRecipe`.
- One target file would be changed:
  - `sample-batch-before/src/main/java/com/ljs/rfactory/batch/before/sample/tasklet/NoOpTasklet.java`

## Proposed change

`ConfirmRecipe` would add the following method to `NoOpTasklet`:

```java
public String confirmRecipeApplied() {
    return "confirmed";
}
```

## Generated artifact

- Patch file: `sample-batch-before/target/rewrite/rewrite.patch`

## Notes

- The first `dryRun` attempt failed because PowerShell parsed the `-Drewrite.activeRecipes=...` argument incorrectly. Quoting the full property value fixed that.
- The next attempt failed because the plugin could not resolve the local custom recipe jar. Running `mvn -pl rewrite-recipes -am -DskipTests install` fixed that.
- The next attempt failed because the YAML recipe name was not discovered from the project resources alone. Adding `configLocation` to `rewrite-maven-plugin` fixed that.
