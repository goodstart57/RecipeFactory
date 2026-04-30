package com.ljs.rfactory.recipe;

import java.util.Comparator;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;

public class ConfirmRecipe extends Recipe {

    private static final String TARGET_TYPE = "com.ljs.rfactory.batch.before.sample.tasklet.NoOpTasklet";
    private static final String TARGET_METHOD = "confirmRecipeApplied";

    @Override
    public String getDisplayName() {
        return "Confirm custom recipe wiring";
    }

    @Override
    public String getDescription() {
        return "Adds a marker method to NoOpTasklet so the custom recipe module can be verified.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
                J.ClassDeclaration c = super.visitClassDeclaration(classDecl, ctx);
                if (c.getBody() == null || !TypeUtils.isOfClassType(c.getType(), TARGET_TYPE)) {
                    return c;
                }

                boolean methodExists = c.getBody().getStatements().stream()
                        .filter(J.MethodDeclaration.class::isInstance)
                        .map(J.MethodDeclaration.class::cast)
                        .map(J.MethodDeclaration::getSimpleName)
                        .anyMatch(TARGET_METHOD::equals);
                if (methodExists) {
                    return c;
                }

                return JavaTemplate.apply(
                        "public String " + TARGET_METHOD + "() { return \"confirmed\"; }",
                        updateCursor(c),
                        c.getBody().getCoordinates().addMethodDeclaration(Comparator.comparing(J.MethodDeclaration::getSimpleName))
                );
            }
        };
    }
}
