package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.yak4jdom.DocumentWrapper;

public interface EffectivePom extends CanLoadParent {
    DocumentWrapper effectivePom();
}
