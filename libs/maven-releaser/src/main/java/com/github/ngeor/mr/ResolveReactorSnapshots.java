package com.github.ngeor.mr;

import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.Tag;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableConsumer;

public class ResolveReactorSnapshots implements FailableConsumer<DocumentWrapper, ProcessFailedException> {
    private final File monorepoRoot;
    private final Git git;
    private final MavenCoordinates moduleCoordinates;

    public ResolveReactorSnapshots(File monorepoRoot, Git git, MavenCoordinates moduleCoordinates) {
        this.monorepoRoot = Objects.requireNonNull(monorepoRoot);
        this.git = Objects.requireNonNull(git);
        this.moduleCoordinates = Objects.requireNonNull(moduleCoordinates);
    }

    @Override
    public void accept(DocumentWrapper effectivePom) throws ProcessFailedException {
        accept(effectivePom.getDocumentElement());
    }

    private void accept(ElementWrapper element) throws ProcessFailedException {
        resolve(element);
        for (ElementWrapper child : element.getChildElementsAsIterable()) {
            accept(child);
        }
    }

    private void resolve(ElementWrapper element) throws ProcessFailedException {
        String[] values =
                element.firstElementsText(ElementNames.GROUP_ID, ElementNames.ARTIFACT_ID, ElementNames.VERSION);
        MavenCoordinates coordinates = new MavenCoordinates(values[0], values[1], values[2]);
        if (coordinates.hasMissingFields()
                || coordinates.equals(moduleCoordinates)
                || !coordinates.version().endsWith("-SNAPSHOT")) {
            return;
        }

        // find internal module's most recent tag
        Map<MavenCoordinates, String> modules = DomHelper.getModules(DocumentWrapper.parse(
                        monorepoRoot.toPath().resolve("pom.xml").toFile()))
                .collect(Collectors.toMap(
                        name -> DomHelper.coordinates(DocumentWrapper.parse(monorepoRoot
                                .toPath()
                                .resolve(name)
                                .resolve("pom.xml")
                                .toFile())),
                        Function.identity()));
        String module = modules.get(coordinates);
        if (module == null) {
            return;
        }

        TagPrefix tagPrefix = TagPrefix.forPath(module);
        String mostRecentVersion = git.getMostRecentTag(tagPrefix.tagPrefix())
                .map(Tag::name)
                .map(tagPrefix::stripTagPrefixIfPresent)
                .orElse(null);
        if (StringUtils.isBlank(mostRecentVersion)) {
            return;
        }

        element.firstElement(ElementNames.VERSION).ifPresent(e -> e.setTextContent(mostRecentVersion));
    }
}
