package com.github.ngeor.mr;

import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.Tag;
import com.github.ngeor.maven.document.PomDocument;
import com.github.ngeor.maven.document.PomDocumentFactory;
import com.github.ngeor.maven.dom.CoordinatesVisitor;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class ResolveReactorSnapshots implements Consumer<DocumentWrapper> {
    private final File monorepoRoot;
    private final Git git;
    private final MavenCoordinates moduleCoordinates;
    private Map<String, String> moduleVersions;
    private PomDocument aggregator;

    public ResolveReactorSnapshots(File monorepoRoot, Git git, MavenCoordinates moduleCoordinates) {
        this.monorepoRoot = Objects.requireNonNull(monorepoRoot);
        this.git = Objects.requireNonNull(git);
        this.moduleCoordinates = Objects.requireNonNull(moduleCoordinates);
    }

    @Override
    public void accept(DocumentWrapper effectivePom) {
        this.aggregator = new PomDocumentFactory().create(monorepoRoot.toPath().resolve("pom.xml"));
        this.moduleVersions = new HashMap<>();
        new CoordinatesVisitor(this::resolve).visit(effectivePom);
    }

    private void resolve(ElementWrapper element, MavenCoordinates coordinates) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(coordinates);
        Validate.isTrue(!coordinates.hasMissingFields());
        if (coordinates.groupIdArtifactId().equals(moduleCoordinates.groupIdArtifactId())
                || !coordinates.isSnapshot()) {
            return;
        }

        String module = aggregator.moduleByCoordinates(coordinates).orElse(null);
        if (module == null) {
            return;
        }

        String mostRecentVersion = moduleVersions.computeIfAbsent(module, this::getMostRecentVersion);
        if (StringUtils.isBlank(mostRecentVersion)) {
            return;
        }

        element.findChildElements(ElementNames.VERSION).forEach(e -> e.setTextContent(mostRecentVersion));
    }

    private String getMostRecentVersion(String module) {
        TagPrefix tagPrefix = TagPrefix.forPath(module);
        try {
            return git.getMostRecentTag(tagPrefix.tagPrefix())
                    .map(Tag::name)
                    .map(tagPrefix::stripTagPrefixIfPresent)
                    .orElse("");
        } catch (ProcessFailedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
