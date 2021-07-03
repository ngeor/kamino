import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

String getPomVersion(File file) {
    def documentBuilderFactory = DocumentBuilderFactory.newInstance()
    def documentBuilder = documentBuilderFactory.newDocumentBuilder()
    def document = documentBuilder.parse(file)
    def childNodes = document.documentElement.childNodes
    def version = ""
    for (i in 0..childNodes.length) {
        def childNode = childNodes.item(i)
        if (childNode.nodeType == Node.ELEMENT_NODE && childNode.nodeName == "version") {
            version = childNode.textContent.trim()
            break
        }
    }
    return version
}

List<String> readLines(File file) {
    def lines = []
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
        for (String line in bufferedReader.lines()) {
            lines.add(line)
        }
    }
    return lines
}

void updateArchetypeResourcesPom(File pomFile, String checkstyleRulesVersion) {
    def lines = readLines(pomFile)
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pomFile))) {
        def state = "looking-for-artifact"
        for (String line in lines) {
            if (state == "looking-for-artifact") {
                if (line.trim() == "<artifactId>checkstyle-rules</artifactId>") {
                    state = "looking-for-version"
                }
            } else if (state == "looking-for-version") {
                if (line.trim().startsWith("<version>")) {
                    state = "all-done"
                    def parts = line.split("<")
                    line = parts[0] + "<version>" + checkstyleRulesVersion + "</version>"
                }
            }
            bufferedWriter.write(line)
            bufferedWriter.newLine()
        }
    }
}

void gitAdd(File file) {
    def processBuilder = new ProcessBuilder("git", "add", file.toString())
    def process = processBuilder.start()
    process.waitFor()
}

void updateArchetypeResourcesPom() {
    def checkstyleRulesPomFile = new File("libs/checkstyle-rules/pom.xml")
    assert checkstyleRulesPomFile.exists()

    def archetypeResourcePomFile = new File("maven-archetypes/archetype-quickstart-jdk8/src/main/resources/archetype-resources/pom.xml")
    assert archetypeResourcePomFile.exists()

    if (archetypeResourcePomFile.lastModified() < checkstyleRulesPomFile.lastModified()) {
        def checkstyleRulesVersion = getPomVersion(checkstyleRulesPomFile)
        updateArchetypeResourcesPom(archetypeResourcePomFile, checkstyleRulesVersion)
        gitAdd(archetypeResourcePomFile)
    }
}

void updateReadme() {
    def pomFile = new File("maven-archetypes/archetype-quickstart-jdk8/pom.xml")
    assert pomFile.exists()

    def readmeFile = new File("maven-archetypes/archetype-quickstart-jdk8/README.md")
    assert readmeFile.exists()

    if (readmeFile.lastModified() < pomFile.lastModified()) {
        def pomVersion = getPomVersion(pomFile)
        updateReadme(readmeFile, pomVersion)
        gitAdd(readmeFile)
    }
}

void updateReadme(File readmeFile, String pomVersion) {
    def lines = readLines(readmeFile)
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(readmeFile))) {
        for (String line in lines) {
            if (line.contains("-DarchetypeVersion") || line.contains("latest version")) {
                // -DarchetypeVersion=2.0.0 \
                // Tip : double check `2.0.0` is the latest version, in case this README is outdated
                def matcher = line =~ ~/\d+\.\d+\.\d+(-SNAPSHOT)?/
                line = matcher.replaceAll(pomVersion)
            }
            bufferedWriter.write(line)
            bufferedWriter.newLine()
        }
    }
}

updateArchetypeResourcesPom()
updateReadme()
