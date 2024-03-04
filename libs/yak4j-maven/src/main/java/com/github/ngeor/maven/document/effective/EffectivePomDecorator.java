package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.parent.CanLoadParentDecorator;
import com.github.ngeor.yak4jdom.DocumentWrapper;

public class EffectivePomDecorator<E extends EffectivePom> extends CanLoadParentDecorator<E> implements EffectivePom {
    public EffectivePomDecorator(E decorated) {
        super(decorated);
    }

    @Override
    public DocumentWrapper effectivePom() {
        return getDecorated().effectivePom();
    }
}
