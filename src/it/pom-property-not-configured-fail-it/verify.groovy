import java.nio.file.Files
import java.nio.charset.StandardCharsets

def logFile = new File(basedir, "build.log")
assert logFile.isFile()

def contents = new String(Files.readAllBytes(logFile.toPath()), StandardCharsets.UTF_8)
assert !contents.isEmpty()
assert contents.contains("The parameters 'propertyName'") // is missing or invalid
