package com.github.ngeor.argparse;

public record ArgSpec(String name, boolean required, SpecKind kind, String description) {}
