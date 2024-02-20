package com.github.ngeor;

import java.io.File;
import java.util.Map;

public abstract class BaseCommand {
    protected BaseCommand(File rootDirectory, Map<String, Object> args) {}

    public abstract void run() throws Exception;
}
