package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.ParentPom;

@FunctionalInterface
public interface Resolver {
    Input resolve(Input child, ParentPom parentPom);
}
