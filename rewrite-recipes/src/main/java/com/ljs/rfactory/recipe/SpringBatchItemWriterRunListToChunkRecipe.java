package com.ljs.rfactory.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaSourceFile;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.Statement;
import org.openrewrite.java.tree.TypeTree;
import org.openrewrite.java.tree.TypeUtils;

public class SpringBatchItemWriterRunListToChunkRecipe extends Recipe {

    private static final String CHUNK_FQN = "org.springframework.batch.item.Chunk";
    private static final String ITEM_WRITER_FQN = "org.springframework.batch.item.ItemWriter";
    private static final Set<String> LIST_ONLY_METHODS = new HashSet<>(Arrays.asList(
            "stream",
            "parallelStream",
            "spliterator",
            "listIterator",
            "subList",
            "get",
            "contains",
            "containsAll",
            "indexOf",
            "lastIndexOf",
            "toArray"
    ));

    @Override
    public String getDisplayName() {
        return "Migrate Spring Batch writer run(List) to run(Chunk)";
    }

    @Override
    public String getDescription() {
        return "Updates BaseWriter-style run(List) methods used by Spring Batch ItemWriter implementations to run(Chunk) and rewrites List-only usages.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
                J.MethodDeclaration m = super.visitMethodDeclaration(method, ctx);
                if (!isCandidateRunMethod(m, getCursor())) {
                    return m;
                }

                J.VariableDeclarations parameter = m.getParameters().get(0) instanceof J.VariableDeclarations
                        ? (J.VariableDeclarations) m.getParameters().get(0)
                        : null;
                if (parameter == null || parameter.getVariables().isEmpty()) {
                    return m;
                }

                TypeTree typeExpression = parameter.getTypeExpression();
                if (typeExpression == null) {
                    return m;
                }

                maybeAddImport(CHUNK_FQN);
                maybeRemoveImport("java.util.List");

                String updatedParameterSource = toChunkParameter(parameter, getCursor());
                J.VariableDeclarations updatedParameter = (J.VariableDeclarations) JavaTemplate.builder(updatedParameterSource)
                        .imports(CHUNK_FQN)
                        .build()
                        .apply(new Cursor(getCursor(), parameter), parameter.getCoordinates().replace());

                List<Statement> updatedParameters = new ArrayList<>(m.getParameters());
                updatedParameters.set(0, updatedParameter);
                m = m.withParameters(updatedParameters);

                if (m.getBody() != null) {
                    String parameterName = parameter.getVariables().get(0).getSimpleName();
                    m = (J.MethodDeclaration) new RunBodyVisitor(parameterName)
                            .visitNonNull(m, ctx, getCursor().getParentOrThrow());
                }

                return m;
            }

            private boolean isCandidateRunMethod(J.MethodDeclaration method, Cursor cursor) {
                if (!"run".equals(method.getSimpleName()) || method.getParameters().size() != 1) {
                    return false;
                }

                J.VariableDeclarations parameter = method.getParameters().get(0) instanceof J.VariableDeclarations
                        ? (J.VariableDeclarations) method.getParameters().get(0)
                        : null;
                if (parameter == null || !isListType(parameter.getTypeExpression())) {
                    return false;
                }

                J.ClassDeclaration enclosingClass = cursor.firstEnclosing(J.ClassDeclaration.class);
                return enclosingClass != null && isWriterPatternClass(enclosingClass);
            }

            private boolean isWriterPatternClass(J.ClassDeclaration classDecl) {
                if (hasWriteCallingRun(classDecl)) {
                    return true;
                }

                if (classDecl.getType() != null && TypeUtils.isAssignableTo(ITEM_WRITER_FQN, classDecl.getType())) {
                    return true;
                }

                if (classDecl.getImplements() != null) {
                    for (TypeTree anInterface : classDecl.getImplements()) {
                        JavaType type = anInterface.getType();
                        if (TypeUtils.isOfClassType(type, ITEM_WRITER_FQN) || simpleName(anInterface).endsWith("Writer")) {
                            return true;
                        }
                    }
                }

                if (classDecl.getExtends() != null && simpleName(classDecl.getExtends()).endsWith("Writer")) {
                    return true;
                }

                return classDecl.getSimpleName().endsWith("Writer");
            }

            private boolean hasWriteCallingRun(J.ClassDeclaration classDecl) {
                if (classDecl.getBody() == null) {
                    return false;
                }

                for (Statement statement : classDecl.getBody().getStatements()) {
                    if (!(statement instanceof J.MethodDeclaration)) {
                        continue;
                    }
                    J.MethodDeclaration method = (J.MethodDeclaration) statement;
                    if (!"write".equals(method.getSimpleName()) || method.getBody() == null) {
                        continue;
                    }
                    boolean[] found = new boolean[]{false};
                    new JavaIsoVisitor<boolean[]>() {
                        @Override
                        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation methodInvocation, boolean[] foundFlag) {
                            J.MethodInvocation mi = super.visitMethodInvocation(methodInvocation, foundFlag);
                            if ("run".equals(mi.getSimpleName())) {
                                foundFlag[0] = true;
                            }
                            return mi;
                        }
                    }.visit(method, found);
                    if (found[0]) {
                        return true;
                    }
                }

                return false;
            }

            private boolean isListType(TypeTree typeTree) {
                if (typeTree == null) {
                    return false;
                }
                JavaType type = typeTree.getType();
                if (TypeUtils.isOfClassType(type, "java.util.List")) {
                    return true;
                }
                String printed = typeTree.printTrimmed(getCursor());
                return printed.startsWith("List") || printed.startsWith("java.util.List");
            }

            private String toChunkParameter(J.VariableDeclarations parameter, Cursor cursor) {
                String printed = parameter.printTrimmed(cursor);
                if (printed.startsWith("java.util.List")) {
                    return printed.replaceFirst("java\\.util\\.List", "Chunk");
                }
                if (printed.startsWith("List")) {
                    return printed.replaceFirst("List", "Chunk");
                }
                return printed.replaceFirst("(?:java\\.util\\.)?List", "Chunk");
           }

            private String simpleName(TypeTree typeTree) {
                String printed = typeTree.printTrimmed(getCursor());
                int generics = printed.indexOf('<');
                String base = generics >= 0 ? printed.substring(0, generics) : printed;
                int qualifier = base.lastIndexOf('.');
                return qualifier >= 0 ? base.substring(qualifier + 1) : base;
            }
        };
    }

    private static class RunBodyVisitor extends JavaIsoVisitor<ExecutionContext> {
        private final String parameterName;
        private final JavaTemplate getItemsTemplate = JavaTemplate.builder("#{any()}.getItems()").build();

        private RunBodyVisitor(String parameterName) {
            this.parameterName = parameterName;
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation m = super.visitMethodInvocation(method, ctx);
            if (m.getSelect() != null && isChunkParameterReference(m.getSelect()) && LIST_ONLY_METHODS.contains(m.getSimpleName())) {
                return m.withSelect(asListExpression(m.getSelect()));
            }

            JavaType.Method methodType = m.getMethodType();
            if (methodType != null && !m.getArguments().isEmpty()) {
                List<Expression> arguments = new ArrayList<>(m.getArguments());
                boolean changed = false;
                for (int i = 0; i < arguments.size(); i++) {
                    Expression argument = arguments.get(i);
                    if (!isChunkParameterReference(argument)) {
                        continue;
                    }
                    if (expectsListArgument(methodType, i)) {
                        arguments.set(i, asListExpression(argument));
                        changed = true;
                    }
                }
                if (changed) {
                    m = m.withArguments(arguments);
                }
            }

            return m;
        }

        @Override
        public J.VariableDeclarations.NamedVariable visitVariable(J.VariableDeclarations.NamedVariable variable, ExecutionContext ctx) {
            J.VariableDeclarations.NamedVariable v = super.visitVariable(variable, ctx);
            if (v.getInitializer() != null && isChunkParameterReference(v.getInitializer())) {
                J.VariableDeclarations declarations = getCursor().firstEnclosing(J.VariableDeclarations.class);
                if (declarations != null && TypeUtils.isOfClassType(declarations.getType(), "java.util.List")) {
                    v = v.withInitializer(asListExpression(v.getInitializer()));
                }
            }
            return v;
        }

        @Override
        public J.Assignment visitAssignment(J.Assignment assignment, ExecutionContext ctx) {
            J.Assignment a = super.visitAssignment(assignment, ctx);
            if (isChunkParameterReference(a.getAssignment()) && TypeUtils.isOfClassType(a.getVariable().getType(), "java.util.List")) {
                return a.withAssignment(asListExpression(a.getAssignment()));
            }
            return a;
        }

        @Override
        public J.Return visitReturn(J.Return return_, ExecutionContext ctx) {
            J.Return r = super.visitReturn(return_, ctx);
            if (r.getExpression() != null && isChunkParameterReference(r.getExpression())) {
                J.MethodDeclaration enclosingMethod = getCursor().firstEnclosing(J.MethodDeclaration.class);
                if (enclosingMethod != null && enclosingMethod.getReturnTypeExpression() != null
                        && TypeUtils.isOfClassType(enclosingMethod.getReturnTypeExpression().getType(), "java.util.List")) {
                    r = r.withExpression(asListExpression(r.getExpression()));
                }
            }
            return r;
        }

        private boolean expectsListArgument(JavaType.Method methodType, int argumentIndex) {
            if (methodType.getParameterTypes().size() <= argumentIndex) {
                return false;
            }
            JavaType parameterType = methodType.getParameterTypes().get(argumentIndex);
            return TypeUtils.isOfClassType(parameterType, "java.util.List");
        }

        private boolean isChunkParameterReference(Expression expression) {
            return expression instanceof J.Identifier
                    && parameterName.equals(((J.Identifier) expression).getSimpleName());
        }

        private Expression asListExpression(Expression chunkReference) {
            return getItemsTemplate.apply(new Cursor(getCursor().getParentOrThrow(), chunkReference), chunkReference.getCoordinates().replace(), chunkReference);
        }
    }
}
