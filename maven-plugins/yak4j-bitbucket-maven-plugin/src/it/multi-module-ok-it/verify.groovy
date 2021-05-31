import java.nio.file.Files
import java.nio.charset.StandardCharsets

def logFile = new File(basedir, "build.log")
assert logFile.isFile()

def contents = new String(Files.readAllBytes(logFile.toPath()), StandardCharsets.UTF_8)
assert !contents.isEmpty()
assert contents.contains("Module one has parent version 0.1.0 and matches parent pom.xml version")
assert contents.contains("Module two has parent version 0.1.0 and matches parent pom.xml version")
