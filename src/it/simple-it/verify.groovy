import org.codehaus.plexus.util.FileUtils

def outputFile = new File(basedir, "target/json/example.json")
assert outputFile.isFile()
def contents = FileUtils.fileRead(outputFile)

def expectedFile = new File(basedir, "expected-output.json")
def expectedContents = FileUtils.fileRead(expectedFile).trim()

assert Objects.equals(contents, expectedContents)
