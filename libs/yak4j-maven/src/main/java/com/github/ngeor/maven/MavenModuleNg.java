package com.github.ngeor.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

public sealed class MavenModuleNg permits RootMavenModule, ChildMavenModule {
    private final File pomFile;

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

    protected MavenModuleNg(File pomFile) {
        this.pomFile = Objects.requireNonNull(pomFile);
        Validate.isTrue(pomFile.isFile());
    }

    public static RootMavenModule root(File rootPomFile) {
        return new RootMavenModule(rootPomFile);
    }

    public File getPomFile() {
        return pomFile;
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
}
