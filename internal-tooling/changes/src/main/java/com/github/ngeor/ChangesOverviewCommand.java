package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ChangesOverviewCommand {
    private final File rootDirectory = new File(".");
    private final Git git = new Git(rootDirectory);

    public void run() {
        System.out.println("Release status");
        System.out.println("Module\tLatest version\tDate\tNumber of unreleased commits");
        Maven maven = new Maven(rootDirectory.toPath().resolve("pom.xml").toFile());
        DocumentWrapper document = maven.effectivePom(new ArrayList<>());
        document.getDocumentElement()
                .findChildElements("modules")
                .flatMap(e -> e.findChildElements("module"))
                .map(ElementWrapper::getTextContent)
                .filter(this::isEligible)
                .map(this::buildOverview)
                .forEach(System.out::println);
    }

    private boolean isEligible(String module) {
        return module != null
                && (module.startsWith("archetypes/") || module.startsWith("libs/") || module.startsWith("plugins/"));
    }

    private String buildOverview(String module) {
        try {
            return module + "\t"
                    + recentTagWithDate(module)
                            .map(versionWithDate -> buildExtraInfo(module, versionWithDate[0], versionWithDate[1]))
                            .orElse("N/A");
        } catch (IOException | InterruptedException | ProcessFailedException e) {
            return module + "\t" + e.getMessage();
        }
    }

    private Optional<String[]> recentTagWithDate(String module)
            throws IOException, InterruptedException, ProcessFailedException {
        // TODO make function for the tag prefix and ensure no duplicate trailing slash
        return git.getMostRecentTagWithDate(module + "/v");
    }

    private String buildExtraInfo(String module, String version, String date) {
        String count;
        try {
            count = String.valueOf(git.revList(module + "/v" + version, module)
                    .filter(new CommitFilter())
                    .count());
        } catch (Exception ex) {
            count = ex.getMessage();
        }
        return version + "\t" + date + "\t" + count;
    }
}
