package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;

public record LoadResult(MavenCoordinates coordinates, DocumentWrapper document) {}
