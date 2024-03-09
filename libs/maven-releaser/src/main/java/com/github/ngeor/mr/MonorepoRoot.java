package com.github.ngeor.mr;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.stream.Stream;

record MonorepoRoot(File directory) {
    public RootPomNotParsed rootPom() {
        return new RootPomNotParsed(this);
    }

    record RootPomNotParsed(MonorepoRoot monorepoRoot) {
        public RootPomParsed parse() {
            return new RootPomParsed(monorepoRoot, parseDocument());
        }

        private File rootPomFile() {
            return new File(monorepoRoot.directory(), "pom.xml");
        }

        private DocumentWrapper parseDocument() {
            return DocumentWrapper.parse(rootPomFile());
        }
    }

    record RootPomParsed(MonorepoRoot monorepoRoot, DocumentWrapper document) {
        public Stream<ModulePomNotParsed> moduleNames() {
            return DomHelper.getModules(document).map(name -> new ModulePomNotParsed(monorepoRoot, name));
        }
    }

    record ModulePomNotParsed(MonorepoRoot monorepoRoot, String moduleName) {
        public ModulePomParsed parse() {
            return new ModulePomParsed(this, parseDocument());
        }

        private File modulePomFile() {
            return monorepoRoot
                    .directory()
                    .toPath()
                    .resolve(moduleName)
                    .resolve("pom.xml")
                    .toFile();
        }

        private DocumentWrapper parseDocument() {
            return DocumentWrapper.parse(modulePomFile());
        }
    }

    record ModulePomParsed(ModulePomNotParsed modulePomNotParsed, DocumentWrapper document) {
        public String moduleName() {
            return modulePomNotParsed.moduleName();
        }

        public ModulePomParsedWithCoordinates parseCoordinates() {
            return new ModulePomParsedWithCoordinates(this, DomHelper.coordinates(document));
        }
    }

    record ModulePomParsedWithCoordinates(ModulePomParsed modulePomParsed, MavenCoordinates coordinates) {
        public String moduleName() {
            return modulePomParsed.moduleName();
        }
    }
}
