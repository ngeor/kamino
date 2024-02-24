package com.github.ngeor.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

public final class MavenModuleNg {
    private final File pomFile;
    private final String moduleName;

    private record EffectivePomAndParentPoms(MavenDocument doc, List<ParentPom> parentPoms) {}

    private final LazyInitializer<EffectivePomAndParentPoms> lazyEffectivePomAndParentPoms = new LazyInitializer<>() {
        @Override
        protected EffectivePomAndParentPoms initialize() {
            List<ParentPom> parentPoms = new ArrayList<>();
            MavenDocument effectivePom = new MavenDocument(pomFile).effectivePom(parentPoms);
            return new EffectivePomAndParentPoms(effectivePom, Collections.unmodifiableList(parentPoms));
        }
    };

    private final LazyInitializer<MavenCoordinates> lazyCoordinates = new LazyInitializer<>() {
        @Override
        protected MavenCoordinates initialize() throws ConcurrentException {
            return effectivePom().coordinates();
        }
    };

    private MavenModuleNg(File pomFile, String moduleName) {
        this.pomFile = Objects.requireNonNull(pomFile);
        this.moduleName = Objects.requireNonNull(moduleName);
    }

    public static MavenModuleNg root(File rootPomFile) {
        return new MavenModuleNg(rootPomFile, "");
    }

    public static MavenModuleNg child(File rootPomFile, String moduleName) {
        return new MavenModuleNg(rootPomFile.toPath().resolve(moduleName).toFile(), moduleName);
    }

    public String getModuleName() {
        return moduleName;
    }

    public MavenDocument effectivePom() throws ConcurrentException {
        return lazyEffectivePomAndParentPoms.get().doc();
    }

    public List<ParentPom> parentPoms() throws ConcurrentException {
        return lazyEffectivePomAndParentPoms.get().parentPoms();
    }

    public MavenCoordinates coordinates() throws ConcurrentException {
        return lazyCoordinates.get();
    }

    public Stream<MavenModuleNg> children() throws ConcurrentException {
        return effectivePom().modules().map(name -> MavenModuleNg.child(pomFile, name));
    }
}
