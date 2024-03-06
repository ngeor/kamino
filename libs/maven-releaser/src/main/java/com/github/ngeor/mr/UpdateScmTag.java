package com.github.ngeor.mr;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.function.Consumer;

public record UpdateScmTag(String tag) implements Consumer<DocumentWrapper> {
    @Override
    public void accept(DocumentWrapper effectivePom) {
        effectivePom
                .getDocumentElement()
                .findChildElements("scm")
                .flatMap(e -> e.findChildElements("tag"))
                .forEach(e -> e.setTextContent(tag));
    }
}
