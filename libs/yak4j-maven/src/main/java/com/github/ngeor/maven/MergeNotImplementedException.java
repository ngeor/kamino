package com.github.ngeor.maven;

import com.github.ngeor.yak4jdom.ElementWrapper;

public class MergeNotImplementedException extends RuntimeException {
    public MergeNotImplementedException(ElementWrapper element) {
        super(String.format("Merging %s is not implemented", element.path()));
    }
}
