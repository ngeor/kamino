package com.github.ngeor.mr;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("java:S6548") // "enum singleton pattern detected"
enum RemoveParentElements implements Consumer<DocumentWrapper> {
    INSTANCE;

    @Override
    public void accept(DocumentWrapper effectivePom) {
        Set<String> elementsToRemove = Set.of(ElementNames.MODULES, ElementNames.PARENT);
        effectivePom.getDocumentElement().removeChildNodesByName(elementsToRemove::contains);
    }
}
