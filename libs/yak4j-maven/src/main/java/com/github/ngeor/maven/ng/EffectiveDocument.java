package com.github.ngeor.maven.ng;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.function.Supplier;

public abstract sealed class EffectiveDocument extends BaseDocument {

    public EffectiveDocument(PomDocumentFactory owner, Supplier<DocumentWrapper> documentLoader) {
        super(owner, documentLoader);
    }

    public static final class Root extends EffectiveDocument {
        public Root(PomDocument pomDocument) {
            super(pomDocument.getOwner(), pomDocument::loadDocument);
        }
    }

    public static final class Child extends EffectiveDocument {
        private final PomDocument child;

        public Child(EffectiveDocument parent, PomDocument child) {
            super(child.getOwner(), () -> child.getOwner().merge(parent, child));
            this.child = child;
        }

        @Override
        public MavenCoordinates coordinates() {
            // TODO fix this
            // important to not load our document for this,
            // otherwise we can't use the coordinates as a cache key
            return child.coordinates();
        }
    }
}
