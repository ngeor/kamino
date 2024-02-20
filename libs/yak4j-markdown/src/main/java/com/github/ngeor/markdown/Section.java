package com.github.ngeor.markdown;

import java.util.ArrayList;
import java.util.List;

public record Section(int level, String title, List<Item> contents) implements Item {
    public Section(int level, String title) {
        this(level, title, new ArrayList<>());
    }

    public Section(int level, String title, Item... items) {
        this(level, title, new ArrayList<>(List.of(items)));
    }
}
