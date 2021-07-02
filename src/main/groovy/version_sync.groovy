import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

String getCheckstyleRulesVersion(File file) {
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

void updateArchetypeResourcesPom(File pomFile, String checkstyleRulesVersion) {
    def lines = []
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pomFile))) {
        for (String line in bufferedReader.lines()) {
            lines.add(line)
        }
    }
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


def checkstyleRulesPomFile = new File("libs/checkstyle-rules/pom.xml")
assert checkstyleRulesPomFile.exists()

def archetypeResourcePomFile = new File("maven-archetypes/archetype-quickstart-jdk8/src/main/resources/archetype-resources/pom.xml")
assert archetypeResourcePomFile.exists()

if (archetypeResourcePomFile.lastModified() < checkstyleRulesPomFile.lastModified()) {
    def checkstyleRulesVersion = getCheckstyleRulesVersion(checkstyleRulesPomFile)
    updateArchetypeResourcesPom(archetypeResourcePomFile, checkstyleRulesVersion)
}
