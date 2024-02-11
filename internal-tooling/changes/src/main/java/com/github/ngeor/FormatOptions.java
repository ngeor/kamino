package com.github.ngeor;

import java.util.Map;

public record FormatOptions(String tagPrefix, String defaultTag, Map<String, String> subGroupNames) {}
