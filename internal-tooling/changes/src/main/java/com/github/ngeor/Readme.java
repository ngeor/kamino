package com.github.ngeor;

import java.util.List;

public record Readme(String header, List<Section> sections) {
    public record Section(String title, String body) {}
}
