package io.noties.prism4j.bundler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AnnotationsInformation {

    @Nullable
    public abstract String findExtendInformation(@NotNull String source);

    @Nullable
    public abstract List<String> findAliasesInformation(@NotNull String source);

    @Nullable
    public abstract List<String> findModifyInformation(@NotNull String source);


    @NotNull
    public static AnnotationsInformation create() {
        return new Impl();
    }

    static class Impl extends AnnotationsInformation {

        @Nullable
        @Override
        public String findExtendInformation(@NotNull String source) {

            final String annotation = "@Extend";
            final int index = source.indexOf(annotation);
            if (index < 0) {
                return null;
            }

            char c;

            final StringBuilder builder = new StringBuilder();

            boolean insideString = false;

            for (int i = index + annotation.length(); i < source.length(); i++) {

                c = source.charAt(i);

                if (Character.isWhitespace(c)) {
                    continue;
                }

                if ('\"' == c) {
                    insideString = !insideString;
                    if (!insideString) {
                        break;
                    } else {
                        continue;
                    }
                }

                if (insideString) {
                    builder.append(c);
                }
            }

            if (builder.length() == 0) {
                return null;
            } else {
                return builder.toString();
            }
        }

        @Nullable
        @Override
        public List<String> findAliasesInformation(@NotNull String source) {
            return findAnnotationArrayInformation("@Aliases", source);
        }

        @Nullable
        @Override
        public List<String> findModifyInformation(@NotNull String source) {
            return findAnnotationArrayInformation("@Modify", source);
        }

        @Nullable
        private static List<String> findAnnotationArrayInformation(@NotNull String annotation, @NotNull String source) {

            final int start = source.indexOf(annotation);
            if (start < 0) {
                return null;
            }

            final int end = source.indexOf(')', start);
            if (end < 0) {
                return null;
            }

            final List<String> aliases = new ArrayList<>(3);

            final StringBuilder builder = new StringBuilder();

            char c;

            boolean insideString = false;

            for (int i = start; i < end; i++) {

                c = source.charAt(i);

                if (Character.isWhitespace(c)) {
                    continue;
                }

                if ('\"' == c) {
                    insideString = !insideString;
                    if (!insideString) {
                        aliases.add(builder.toString());
                        builder.setLength(0);
                    }
                    continue;
                }

                if (insideString) {
                    builder.append(c);
                }
            }

            if (aliases.size() == 0) {
                return null;
            } else {
                return aliases;
            }
        }
    }
}
