package com.github.ngeor.yak4jdom;

import java.util.Objects;
import java.util.function.Consumer;

public final class Preconditions {
    public static ElementPreconditions check(DocumentWrapper documentWrapper) {
        return new ElementPreconditions(documentWrapper.getDocumentElement());
    }

    public record ElementPreconditions(ElementWrapper element) {
        public ElementPreconditions isNotNull() {
            Objects.requireNonNull(element);
            return this;
        }

        public ElementPreconditions hasChild(String childElementName) {
            isNotNull();
            if (element.firstElement(childElementName).isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("Element '%s' not found under '%s'", childElementName, element.path()));
            }
            return this;
        }

        public ElementPreconditions hasChildThat(
                String childElementName, Consumer<ElementPreconditions> childPreconditionsConsumer) {
            hasChild(childElementName);
            element.firstElement(childElementName).stream()
                    .map(ElementPreconditions::new)
                    .forEach(childPreconditionsConsumer);
            return this;
        }

        public ElementPreconditions hasChildWithTextContent(String childElementName) {
            return hasChildThat(childElementName, ElementPreconditions::hasTextContent);
        }

        public ElementPreconditions hasTextContent() {
            isNotNull();
            if (element.getTextContentTrimmed().isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("Element '%s' must have text content", element.path()));
            }
            return this;
        }

        public ElementPreconditions forEachChild(
                String childElementName, Consumer<ElementPreconditions> childPreconditionsConsumer) {
            isNotNull();
            element.findChildElements(childElementName)
                    .map(ElementPreconditions::new)
                    .forEach(childPreconditionsConsumer);
            return this;
        }
    }
}
