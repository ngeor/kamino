package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.ParentPom;
import java.io.IOException;

@FunctionalInterface
public interface Resolver {
    Input resolve(Input child, ParentPom parentPom) throws IOException;
}
