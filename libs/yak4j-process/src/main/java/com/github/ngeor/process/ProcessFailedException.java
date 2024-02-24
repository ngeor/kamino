package com.github.ngeor.process;

import java.io.IOException;

public class ProcessFailedException extends Exception {
    public ProcessFailedException(String message) {
        super(message);
    }

    public ProcessFailedException(IOException rootCause) {
        super(rootCause);
    }

    public ProcessFailedException(InterruptedException rootCause) {
        super(rootCause);
    }
}
