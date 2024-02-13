package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 */
public final class App {
    App() {}

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        new App().run();
    }

    void run() throws IOException, InterruptedException {
        File rootDirectory = new File(".");

        String path = "libs/java";
        String tagPrefix = path + "/v";
        String version = null; // "4.2.1";
        String sinceCommit = version != null ? tagPrefix + version + "..HEAD" : null;

        Git git = new Git(rootDirectory);

        FormattedRelease formattedRelease = format(
                Release.create(git.revList(sinceCommit, path))
                        .filter(c -> !c.summary().contains("maven-release-plugin"))
                        .makeSubGroups(new Release.SubGroupOptions("chore", List.of("feat", "fix"))),
                new FormatOptions(
                        tagPrefix,
                        "Unreleased",
                        Map.of("feat", "Features", "fix", "Fixes", "chore", "Miscellaneous Tasks")));

        File changeLog =
                rootDirectory.toPath().resolve(path).resolve("CHANGELOG.md").toFile();
        Readme readme = changeLog.isFile() ? ReadmeReader.read(changeLog) : new Readme("# Changelog", List.of());
        readme = merge(readme, formattedRelease);
        ReadmeWriter.write(readme, changeLog);
        print(formattedRelease, new PrintWriter(System.out, true));
    }

    FormattedRelease format(Release release, FormatOptions options) {
        return new FormattedRelease(
                release.groups().stream().map(g -> format(g, options)).toList());
    }

    private FormattedRelease.Group format(Release.Group group, FormatOptions options) {
        String title = group.tag().tag();
        if (title != null) {
            if (title.startsWith(options.tagPrefix())) {
                title = title.substring(options.tagPrefix().length());
            }
            title = "[" + title + "] - " + group.tag().authorDate();
        } else {
            title = options.defaultTag();
        }

        return new FormattedRelease.Group(
                title, group.subGroups().stream().map(g -> format(g, options)).toList());
    }

    private FormattedRelease.SubGroup format(Release.SubGroup subGroup, FormatOptions options) {
        String title = options.subGroupNames().getOrDefault(subGroup.name(), subGroup.name());
        return new FormattedRelease.SubGroup(
                title,
                subGroup.commits().stream()
                        .map(Commit::summary)
                        .map(s -> Arrays.asList(s.split(":", 2))
                                .reversed()
                                .getFirst()
                                .trim())
                        .toList());
    }

    void print(FormattedRelease formattedRelease, PrintWriter writer) {
        for (var it = formattedRelease.groups().iterator(); it.hasNext(); ) {
            var formattedGroup = it.next();
            writer.printf("## %s%n%n", formattedGroup.title());
            for (var itChild = formattedGroup.subGroups().iterator(); itChild.hasNext(); ) {
                var childGroup = itChild.next();
                writer.printf("### %s%n%n", childGroup.title());
                for (String item : childGroup.items()) {
                    writer.printf("* %s%n", item);
                }
                if (itChild.hasNext()) {
                    writer.println();
                }
            }
            if (it.hasNext()) {
                writer.println();
            }
        }
    }

    Readme merge(Readme readme, FormattedRelease formattedRelease) {
        List<Readme.Section> sections = new ArrayList<>();
        Set<String> seenTitles = new HashSet<>();
        for (FormattedRelease.Group formattedGroup : formattedRelease.groups()) {
            StringBuilder body = new StringBuilder();
            for (var itChild = formattedGroup.subGroups().iterator(); itChild.hasNext(); ) {
                var childGroup = itChild.next();
                body.append(String.format("### %s%n%n", childGroup.title()));
                for (String item : childGroup.items()) {
                    body.append(String.format("* %s%n", item));
                }
                if (itChild.hasNext()) {
                    body.append(System.lineSeparator());
                }
            }
            sections.add(new Readme.Section(formattedGroup.title(), body.toString()));
            seenTitles.add(formattedGroup.title());
        }
        sections.addAll(readme.sections().stream()
                .filter(s -> !seenTitles.contains(s.title()))
                .toList());

        return new Readme(readme.header(), sections);
    }
}
