package com.github.ngeor.mr;

import com.github.ngeor.maven.dom.CoordinatesVisitor;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang3.Validate;

@SuppressWarnings("java:S6548") // "enum singleton pattern detected"
public enum EnsureNoSnapshotVersions implements Consumer<DocumentWrapper> {
    INSTANCE;

    @Override
    public void accept(DocumentWrapper effectivePom) {
        Objects.requireNonNull(effectivePom);
        new CoordinatesVisitor(this::ensureNoSnapshotVersions).visit(effectivePom);
    }

    private void ensureNoSnapshotVersions(ElementWrapper element, MavenCoordinates coordinates) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(coordinates);
        Validate.isTrue(!coordinates.hasMissingFields());
        Validate.isTrue(
                !coordinates.isSnapshot(),
                "Snapshot version %s is not allowed (%s)",
                coordinates.version(),
                element.path());
    }
}
