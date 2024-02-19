package com.github.ngeor;

import java.io.IOException;
import java.util.List;

public interface Git {
    String defaultBranch() throws IOException, InterruptedException;

    String currentBranch() throws IOException, InterruptedException;

    void checkoutNewBranch(String name) throws IOException, InterruptedException;

    void init() throws IOException, InterruptedException;

    void initBare() throws IOException, InterruptedException;

    void clone(String url) throws IOException, InterruptedException;

    void push() throws IOException, InterruptedException;

    void add(String path) throws IOException, InterruptedException;

    void commit(String message) throws IOException, InterruptedException;

    void tag(String msg, String tag) throws IOException, InterruptedException;

    List<String> listTags(String pattern) throws IOException, InterruptedException;

    void fetch() throws IOException, InterruptedException;

    void pull() throws IOException, InterruptedException;

    void config(String key, String value) throws IOException, InterruptedException;

    boolean hasPendingChanges() throws IOException, InterruptedException;
}
