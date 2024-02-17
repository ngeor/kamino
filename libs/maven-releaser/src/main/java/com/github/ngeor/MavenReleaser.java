package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class MavenReleaser {
    private MavenReleaser() {}

    public static void prepareRelease(File monorepoRoot, String path, SemVer nextVersion, boolean dryRun, boolean push)
            throws IOException, InterruptedException, ProcessFailedException {
        String developmentVersion =
                nextVersion.bump(SemVerBump.MINOR).preRelease("SNAPSHOT").toString();
        String tag = path + "/v" + nextVersion;

        Path modulePath = monorepoRoot.toPath().resolve(path);
        Path pomPath = modulePath.resolve("pom.xml");

        // make a backup of the pom file
        Files.copy(pomPath, modulePath.resolve("pom.xml.bak1"), StandardCopyOption.REPLACE_EXISTING);

        Maven maven =
                new Maven(pomPath.toFile());

        // overwrite pom.xml with effective pom
        File output = File.createTempFile("pom", ".xml");
        output.deleteOnExit();
        maven.effectivePomViaMaven(output);
        Files.copy(output.toPath(), pomPath, StandardCopyOption.REPLACE_EXISTING);

        // remove parent element
        DocumentWrapper document = DocumentWrapper.parse(pomPath.toFile());
        document.getDocumentElement().removeChildNodesByName("parent");
        document.write(pomPath.toFile());

        maven.prepareRelease(tag, nextVersion.toString(), developmentVersion, dryRun, false);

        if (dryRun) {
            Files.copy(modulePath.resolve("pom.xml.bak1"), pomPath, StandardCopyOption.REPLACE_EXISTING);
            return;
        }

        Git git = new Git(modulePath.toFile());

        // discard the commit that switches to the snapshots
        git.resetOne(ResetMode.HARD);

        // delete the tag, we'll recreate also the release commit
        git.deleteTag(tag);

        // restore the release pom
        git.resetOne(ResetMode.MIXED);

        maven.sortPom();
        git.add("pom.xml");
        git.commit("[maven-release-plugin] prepare release " + tag);
        git.tag(tag);

        // TODO changelog

        // restore original pom
        Files.copy(modulePath.resolve("pom.xml.bak1"), pomPath, StandardCopyOption.REPLACE_EXISTING);

        // switch to the development version
        updateVersion(pomPath.toFile(), developmentVersion);

        maven.sortPom();
        git.add("pom.xml");
        git.commit("[maven-release-plugin] prepare for next development iteration");

        if (push) {
            git.push(true);
        }
    }

    public static String updateVersion(String input, String version) {
        DocumentWrapper document = DocumentWrapper.parseString(input);
        document.getDocumentElement().firstElement("version").ifPresent(e -> e.setTextContent(version));
        return document.writeToString();
    }

    public static void updateVersion(File file, String version) {
        DocumentWrapper document = DocumentWrapper.parse(file);
        document.getDocumentElement().firstElement("version").ifPresent(e -> e.setTextContent(version));
        document.write(file);
    }
}
