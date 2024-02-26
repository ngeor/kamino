package com.github.ngeor.process;

import java.util.List;

public record ProcessBuilderWithArgs(ProcessBuilder processBuilder, List<String> args) {
    public String commandLine() {
        return String.join(" ", args);
    }
}
