import org.codehaus.plexus.util.FileUtils

def outputFile = new File(basedir, "target/site/swagger.yml")
assert outputFile.isFile()
def contents = FileUtils.fileRead(outputFile)

def expectedFile = new File(basedir, "expected-swagger.yml")
def expectedContents = FileUtils.fileRead(expectedFile)

assert Objects.equals(contents, expectedContents)
