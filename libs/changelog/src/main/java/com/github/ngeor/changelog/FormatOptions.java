package com.github.ngeor.changelog;

import java.util.Map;

public record FormatOptions(String unreleasedTitle, Map<String, String> subGroupNames, String compareUrlTemplate) {}
