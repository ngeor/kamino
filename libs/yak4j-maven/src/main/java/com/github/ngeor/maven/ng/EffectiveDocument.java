package com.github.ngeor.maven.ng;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;

public abstract sealed class EffectiveDocument extends BaseDocument {

    public EffectiveDocument(PomDocumentFactory owner) {
        super(owner);
    }

    public static final class Root extends EffectiveDocument {
        private final PomDocument pomDocument;

        public Root(PomDocument pomDocument) {
            super(pomDocument.getOwner());
            this.pomDocument = pomDocument;
        }

        @Override
        protected DocumentWrapper doLoadDocument() {
            return pomDocument.loadDocument();
        }
    }

    public static final class Child extends EffectiveDocument {
        private final EffectiveDocument parent;
        private final PomDocument child;

        public Child(EffectiveDocument parent, PomDocument child) {
            super(child.getOwner());
            this.parent = parent;
            this.child = child;
        }

        @Override
        protected DocumentWrapper doLoadDocument() {
            return getOwner().merge(parent, child);
        }

        @Override
        public MavenCoordinates coordinates() {
            // important to not load our document for this,
            // otherwise we can't use the coordinates as a cache key
            return child.coordinates();
        }
    }
}
