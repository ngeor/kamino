package com.github.ngeor.maven.resolve;

import com.github.ngeor.yak4jdom.DocumentWrapper;

public sealed interface Input permits FileInput, StringInput {
    DocumentWrapper loadDocument();
}
