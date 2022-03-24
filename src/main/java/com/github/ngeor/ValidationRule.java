package com.github.ngeor;

import java.io.IOException;

public interface ValidationRule {
    void validate() throws IOException, InterruptedException;
}
