import java.nio.file.Files
import java.nio.charset.StandardCharsets

def logFile = new File(basedir, "build.log")
assert logFile.isFile()

def contents = new String(Files.readAllBytes(logFile.toPath()), StandardCharsets.UTF_8)
assert !contents.isEmpty()
assert contents.contains("Property my.property has value 0.1.0 and matches pom.xml")
