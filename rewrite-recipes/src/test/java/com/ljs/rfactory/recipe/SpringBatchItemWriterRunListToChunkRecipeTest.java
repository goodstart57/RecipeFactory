package com.ljs.rfactory.recipe;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import static org.openrewrite.java.Assertions.java;

class SpringBatchItemWriterRunListToChunkRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new SpringBatchItemWriterRunListToChunkRecipe())
                .typeValidationOptions(TypeValidation.none());
    }

    @Test
    void migratesBaseWriterPatternAndListOnlyUsages() {
        rewriteRun(
                java(
                        "package org.springframework.batch.item;\n" +
                        "\n" +
                        "import java.util.Iterator;\n" +
                        "import java.util.List;\n" +
                        "\n" +
                        "public interface ItemWriter<T> {\n" +
                        "    void write(Chunk<? extends T> items) throws Exception;\n" +
                        "}\n" +
                        "\n" +
                        "public class Chunk<T> implements Iterable<T> {\n" +
                        "    public boolean isEmpty() { return false; }\n" +
                        "    public int size() { return 0; }\n" +
                        "    public List<T> getItems() { return null; }\n" +
                        "    @Override\n" +
                        "    public Iterator<T> iterator() { return null; }\n" +
                        "}\n"
                ),
                java(
                        "package example;\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "import org.springframework.batch.item.Chunk;\n" +
                        "import org.springframework.batch.item.ItemWriter;\n" +
                        "\n" +
                        "abstract class BaseWriter<T> implements ItemWriter<T> {\n" +
                        "    @Override\n" +
                        "    public void write(Chunk<? extends T> items) throws Exception {\n" +
                        "        run(items);\n" +
                        "    }\n" +
                        "\n" +
                        "    protected abstract void run(List<? extends T> items) throws Exception;\n" +
                        "}\n" +
                        "\n" +
                        "class SampleWriter extends BaseWriter<String> {\n" +
                        "    @Override\n" +
                        "    protected void run(List<? extends String> items) throws Exception {\n" +
                        "        if (items.isEmpty()) {\n" +
                        "            return;\n" +
                        "        }\n" +
                        "        items.stream().forEach(System.out::println);\n" +
                        "        System.out.println(items.get(0));\n" +
                        "    }\n" +
                        "}\n",
                        "package example;\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "import org.springframework.batch.item.Chunk;\n" +
                        "import org.springframework.batch.item.ItemWriter;\n" +
                        "\n" +
                        "abstract class BaseWriter<T> implements ItemWriter<T> {\n" +
                        "    @Override\n" +
                        "    public void write(Chunk<? extends T> items) throws Exception {\n" +
                        "        run(items);\n" +
                        "    }\n" +
                        "\n" +
                        "    protected abstract void run(Chunk<? extends T> items) throws Exception;\n" +
                        "}\n" +
                        "\n" +
                        "class SampleWriter extends BaseWriter<String> {\n" +
                        "    @Override\n" +
                        "    protected void run(Chunk<? extends String> items) throws Exception {\n" +
                        "        if (items.isEmpty()) {\n" +
                        "            return;\n" +
                        "        }\n" +
                        "        items.getItems().stream().forEach(System.out::println);\n" +
                        "        System.out.println(items.getItems().get(0));\n" +
                        "    }\n" +
                        "}\n"
                )
        );
    }

    @Test
    void leavesNonWriterRunMethodsUntouched() {
        rewriteRun(
                java(
                        "package example;\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "\n" +
                        "class UtilityRunner {\n" +
                        "    void run(List<String> items) {\n" +
                        "        items.forEach(System.out::println);\n" +
                        "    }\n" +
                        "}\n"
                )
        );
    }
}
