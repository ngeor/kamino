def travisFile = new File(basedir, ".travis.yml")
assert travisFile.isFile()

def appFile = new File(basedir, "src/main/java/hello/it/App.java")
assert appFile.isFile()
