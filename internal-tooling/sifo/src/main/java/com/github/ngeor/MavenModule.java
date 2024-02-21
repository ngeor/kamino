package com.github.ngeor;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.MavenDocument;
import com.github.ngeor.maven.ParentPom;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class MavenModule implements Comparable<MavenModule> {
    private final File typeDirectory;
    private final File projectDirectory;
    private final File pomFile;
    private MavenDocument effectivePom;
    private final List<ParentPom> parentPoms = new ArrayList<>();

    public MavenModule(File typeDirectory, File projectDirectory, File pomFile) {
        this.typeDirectory = typeDirectory;
        this.projectDirectory = projectDirectory;
        this.pomFile = pomFile;
    }

    public String path() {
        return typeDirectory.getName() + "/" + projectDirectory.getName();
    }

    public File typeDirectory() {
        return typeDirectory;
    }

    public File projectDirectory() {
        return projectDirectory;
    }

    public File pomFile() {
        return pomFile;
    }

    public MavenDocument effectivePom() {
        if (effectivePom == null) {
            parentPoms.clear();
            effectivePom = new MavenDocument(pomFile).effectivePom(parentPoms);
        }

        return effectivePom;
    }

    public List<ParentPom> parentPoms() {
        return Collections.unmodifiableList(parentPoms);
    }

    public MavenCoordinates coordinates() {
        return effectivePom().coordinates();
    }

    public Stream<MavenCoordinates> dependencies() {
        return effectivePom().dependencies();
    }

    public Optional<String> calculateJavaVersion() {
        return effectivePom().property("maven.compiler.source").map(v -> "1.8".equals(v) ? "8" : v);
    }

    @Override
    public String toString() {
        return String.format("%s { path=%s }", MavenModule.class.getName(), path());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MavenModule m && compareTo(m) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeDirectory.getName(), projectDirectory.getName());
    }

    @Override
    public int compareTo(MavenModule other) {
        int cmp = this.typeDirectory.getName().compareTo(other.typeDirectory.getName());
        if (cmp == 0) {
            return this.projectDirectory.getName().compareTo(other.projectDirectory.getName());
        } else {
            return cmp;
        }
    }
}
