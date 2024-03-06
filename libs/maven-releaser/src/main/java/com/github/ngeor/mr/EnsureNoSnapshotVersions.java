package com.github.ngeor.mr;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("java:S6548") // "enum singleton pattern detected"
public enum EnsureNoSnapshotVersions implements Consumer<DocumentWrapper> {
    INSTANCE;

    @Override
    public void accept(DocumentWrapper effectivePom) {
        Objects.requireNonNull(effectivePom);
        ensureNoSnapshotVersions(effectivePom.getDocumentElement());
    }

    private void ensureNoSnapshotVersions(ElementWrapper element) {
        Objects.requireNonNull(element);
        // recursion
        element.getChildElements().forEach(this::ensureNoSnapshotVersions);

        if (ElementNames.VERSION.equals(element.getNodeName())) {
            String text = element.getTextContentTrimmed().orElse("");
            if (text.endsWith("-SNAPSHOT")) {
                throw new IllegalArgumentException(
                        String.format("Snapshot version %s is not allowed (%s)", text, element.path()));
            }
        }
    }
}
