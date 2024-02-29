package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.ElementNames;
import com.github.ngeor.maven.resolve.input.Input;
import com.github.ngeor.maven.resolve.input.InputDecorator;
import com.github.ngeor.maven.resolve.input.InputFactory;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

final class SanityCheckedInput extends InputDecorator {
    private SanityCheckedInput(Input decorated) {
        super(decorated);
    }

    @Override
    public DocumentWrapper document() {
        DocumentWrapper result = super.document();
        Objects.requireNonNull(result);
        ElementWrapper element = Objects.requireNonNull(result.getDocumentElement());
        Validate.isTrue(
                ElementNames.PROJECT.equals(element.getNodeName()),
                "Unexpected root element '%s' (expected '%s')",
                element.getNodeName(),
                ElementNames.PROJECT);
        return result;
    }

    public static InputFactory decorateFactory(InputFactory factory) {
        return pomFile -> new SanityCheckedInput(factory.load(pomFile));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SanityCheckedInput other && Objects.equals(getDecorated(), other.getDecorated());
    }

    @Override
    public int hashCode() {
        return getDecorated().hashCode();
    }
}
