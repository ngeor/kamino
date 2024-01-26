package com.github.ngeor.yak4j;

/**
 * A runtime exception regarding XML (de)serialization issues.
 */
@SuppressWarnings("WeakerAccess")
public class XmlRuntimeException extends RuntimeException {
    public XmlRuntimeException(Throwable cause) {
        super(cause);
    }
}
