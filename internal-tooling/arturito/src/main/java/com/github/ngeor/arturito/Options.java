package com.github.ngeor.arturito;

public record Options(GpgOptions gpg, NexusOptions nexus, String path, boolean debug) {}
