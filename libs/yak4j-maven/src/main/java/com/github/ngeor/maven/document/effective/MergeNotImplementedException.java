package com.github.ngeor.maven.document.effective;

import com.github.ngeor.yak4jdom.ElementWrapper;

public final class MergeNotImplementedException extends RuntimeException {
    public MergeNotImplementedException(ElementWrapper element) {
        super(String.format("Merging %s is not implemented", element.path()));
    }
}
