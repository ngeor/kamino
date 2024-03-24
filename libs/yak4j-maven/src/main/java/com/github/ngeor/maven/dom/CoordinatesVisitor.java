package com.github.ngeor.maven.dom;

import com.github.ngeor.maven.find.State;
import com.github.ngeor.maven.find.States;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Objects;
import java.util.function.BiConsumer;

public class CoordinatesVisitor {
    private final BiConsumer<ElementWrapper, MavenCoordinates> consumer;
    private final boolean onlyAllFields;

    public CoordinatesVisitor(boolean onlyAllFields, BiConsumer<ElementWrapper, MavenCoordinates> consumer) {
        this.onlyAllFields = onlyAllFields;
        this.consumer = Objects.requireNonNull(consumer);
    }

    public CoordinatesVisitor(BiConsumer<ElementWrapper, MavenCoordinates> consumer) {
        this(true, consumer);
    }

    public void visit(DocumentWrapper document) {
        visit(document.getDocumentElement());
    }

    private void visit(ElementWrapper element) {
        var state = States.mavenCoordinates()
                .compose(
                        new State<ElementWrapper, ElementWrapper>() {
                            @Override
                            public boolean isFound() {
                                return false;
                            }

                            @Override
                            public boolean hasValue() {
                                return false;
                            }

                            @Override
                            public ElementWrapper getValue() {
                                return null;
                            }

                            @Override
                            public State<ElementWrapper, ElementWrapper> visit(ElementWrapper element) {
                                CoordinatesVisitor.this.visit(element);
                                return this;
                            }
                        },
                        (l, r) -> l);

        MavenCoordinates coordinates = state.consume(element.getChildElementsAsIterator());

        if ((onlyAllFields && !coordinates.hasMissingFields()) || (!onlyAllFields && !coordinates.isEmpty())) {
            consumer.accept(element, coordinates);
        }
    }
}
