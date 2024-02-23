package com.github.ngeor.changelog;

import com.github.ngeor.versions.SemVer;
import java.util.Map;

public record FormatOptions(String unreleasedTitle, SemVer futureVersion, Map<String, String> subGroupNames) {}
