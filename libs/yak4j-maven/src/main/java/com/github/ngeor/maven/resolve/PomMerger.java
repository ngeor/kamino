package com.github.ngeor.maven.resolve;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Optional;
import java.util.Set;

// Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
public final class PomMerger {

    private interface Merge<E> {
        void mergeIntoLeft(E left, E right);
    }

    public static class DocumentMerge implements Merge<DocumentWrapper> {
        @Override
        public void mergeIntoLeft(DocumentWrapper left, DocumentWrapper right) {
            ElementWrapper leftDocumentElement = left.getDocumentElement();
            ElementWrapper rightDocumentElement = right.getDocumentElement();
            // Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
            leftDocumentElement.removeChildNodesByName("artifactId");
            leftDocumentElement.removeChildNodesByName("name");
            new ProjectMerge().mergeIntoLeft(leftDocumentElement, rightDocumentElement);
        }
    }

    private abstract static class BasicMerger implements Merge<ElementWrapper> {
        @Override
        public void mergeIntoLeft(ElementWrapper left, ElementWrapper right) {
            if (right.hasChildElements()) {
                right.getChildElements().forEach(rightChild -> visitChild(left, rightChild));
            } else {
                right.getTextContentTrimmed().ifPresent(text -> {
                    if (left.hasChildElements()) {
                        throw new IllegalStateException(String.format(
                                "Cannot merge %s as a text node because the parent pom has child elements",
                                right.path()));
                    }
                    left.setTextContent(text);
                });
            }
        }

        protected void visitChild(ElementWrapper left, ElementWrapper rightChild) {
            ElementWrapper leftChild = ensureLeftChild(left, rightChild);
            createMerger(rightChild).mergeIntoLeft(leftChild, rightChild);
        }

        protected ElementWrapper ensureLeftChild(ElementWrapper left, ElementWrapper rightChild) {
            return locateExistingChild(left, rightChild).orElseGet(() -> left.append(rightChild.getNodeName()));
        }

        protected Optional<ElementWrapper> locateExistingChild(ElementWrapper left, ElementWrapper rightChild) {
            return left.firstElement(rightChild.getNodeName());
        }

        protected abstract Merge<ElementWrapper> createMerger(ElementWrapper rightChild);
    }

    private static class BasicRecursiveMerger extends BasicMerger {

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new BasicRecursiveMerger();
        }
    }

    private static class ProjectMerge extends BasicMerger {
        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if (Set.of(
                            "properties",
                            "modelVersion",
                            "groupId",
                            "artifactId",
                            "version",
                            "name",
                            "packaging",
                            "description",
                            "url",
                            "scm")
                    .contains(name)) {
                return new BasicRecursiveMerger();
            } else if ("build".equals(name)) {
                return new BuildMerge();
            } else if ("dependencies".equals(name)) {
                return new DependenciesMerge();
            } else if ("profiles".equals(name)) {
                return new ProfilesMerge();
            } else if ("reporting".equals(name)) {
                return new ReportingMerge();
            } else if ("licenses".equals(name)) {
                return new LicensesMerge();
            } else if ("developers".equals(name)) {
                return new DevelopersMerge();
            } else if ("distributionManagement".equals(name)) {
                return new DistributionManagementMerge();
            } else if ("dependencyManagement".equals(name)) {
                return new DependencyManagementMerge();
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        }
    }

    private static class BuildMerge extends BasicMerger {
        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if ("plugins".equals(name)) {
                return new PluginsMerge();
            } else if ("extensions".equals(name)) {
                return new ExtensionsMerge();
            } else if ("pluginManagement".equals(name)) {
                return new PluginManagementMerge();
            } else if ("finalName".equals(name)) {
                return new BasicRecursiveMerger();
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        }
    }

    private static class PluginsMerge extends CoordinateMerge {
        @Override
        protected String getChildElementName() {
            return "plugin";
        }

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new PluginMerge();
        }
    }

    private static class PluginMerge extends BasicRecursiveMerger {
        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if ("executions".equals(name)) {
                return new ExecutionsMerge();
            } else if ("configuration".equals(name)) {
                return new AppendToExistingMerger();
            } else {
                return super.createMerger(rightChild);
            }
        }
    }

    private static class ExecutionsMerge extends IdentityMerge {
        @Override
        protected Set<String> getChildElementNames() {
            return Set.of("execution");
        }

        @Override
        protected String getIdElementName() {
            return "id";
        }

        @Override
        protected Optional<ElementWrapper> locateExistingChild(ElementWrapper left, ElementWrapper rightChild) {
            // allow "id" to be missing, if the parent pom has no executions
            String name = rightChild.getNodeName();
            if (getChildElementNames().contains(name)) {
                String field = getIdElementName();
                String id = rightChild.childTextContentsTrimmed(field).findAny().orElse(null);
                if (id != null) {
                    return super.locateExistingChild(left, rightChild);
                }

                if (left.findChildElements(name).findAny().isEmpty()) {
                    return Optional.of(left.append(name));
                }

                throw new IllegalStateException("Found execution without id but parent has executions");
            } else {
                return super.locateExistingChild(left, rightChild);
            }
        }

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new ExecutionMerge();
        }
    }

    private static class ExecutionMerge extends BasicRecursiveMerger {
        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if ("configuration".equals(name)) {
                return new AppendToExistingMerger();
            } else if ("goals".equals(name)) {
                return new GoalsMerger();
            } else {
                return super.createMerger(rightChild);
            }
        }
    }

    private static class AppendToExistingMerger extends BasicMerger {
        @Override
        protected Optional<ElementWrapper> locateExistingChild(ElementWrapper left, ElementWrapper rightChild) {
            return Optional.empty();
        }

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new AppendToExistingMerger();
        }
    }

    private static class GoalsMerger implements Merge<ElementWrapper> {

        @Override
        public void mergeIntoLeft(ElementWrapper left, ElementWrapper right) {
            left.removeChildNodesByName("goal");
            right.findChildElements("goal").forEach(left::importNode);
        }
    }

    private static class ExtensionsMerge extends CoordinateMerge {
        @Override
        protected String getChildElementName() {
            return "extension";
        }

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new BasicRecursiveMerger();
        }
    }

    private static class DependenciesMerge extends CoordinateMerge {
        @Override
        protected String getChildElementName() {
            return "dependency";
        }

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new BasicRecursiveMerger();
        }
    }

    private static class ProfileMerge extends BasicMerger {
        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if (Set.of("properties", "id", "activation").contains(name)) {
                return new BasicRecursiveMerger();
            } else if ("build".equals(name)) {
                return new BuildMerge();
            } else if ("dependencies".equals(name)) {
                return new DependenciesMerge();
            } else if ("reporting".equals(name)) {
                return new ReportingMerge();
            } else if ("licenses".equals(name)) {
                return new LicensesMerge();
            } else if ("developers".equals(name)) {
                return new DevelopersMerge();
            } else if ("distributionManagement".equals(name)) {
                return new DistributionManagementMerge();
            } else if ("dependencyManagement".equals(name)) {
                return new DependencyManagementMerge();
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        }
    }

    private static class ReportingMerge extends BuildMerge {}

    private static class DependencyManagementMerge extends BasicMerger {
        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if ("dependencies".equals(name)) {
                return new DependenciesMerge();
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        }
    }

    private static class PluginManagementMerge extends BasicMerger {
        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if ("plugins".equals(name)) {
                return new PluginsMerge();
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        }
    }

    private abstract static class CoordinateMerge extends BasicMerger {
        @Override
        protected Optional<ElementWrapper> locateExistingChild(ElementWrapper left, ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if (getChildElementName().equals(name)) {
                String groupId = requireChildText(rightChild, "groupId");
                String artifactId = requireChildText(rightChild, "artifactId");
                return left.getChildElements()
                        .filter(x -> name.equals(x.getNodeName()))
                        .filter(x -> hasChildText(x, "groupId", groupId))
                        .filter(x -> hasChildText(x, "artifactId", artifactId))
                        .findFirst();
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        }

        protected abstract String getChildElementName();
    }

    private abstract static class IdentityMerge extends BasicMerger {
        @Override
        protected Optional<ElementWrapper> locateExistingChild(ElementWrapper left, ElementWrapper rightChild) {
            String name = rightChild.getNodeName();
            if (getChildElementNames().contains(name)) {
                String field = getIdElementName();
                String id = requireChildText(rightChild, field);
                return left.findChildElements(name)
                        .filter(x -> hasChildText(x, field, id))
                        .findFirst();
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        }

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new BasicRecursiveMerger();
        }

        protected abstract Set<String> getChildElementNames();

        protected abstract String getIdElementName();
    }

    private static final class ProfilesMerge extends IdentityMerge {
        @Override
        protected Set<String> getChildElementNames() {
            return Set.of("profile");
        }

        @Override
        protected String getIdElementName() {
            return "id";
        }

        @Override
        protected Merge<ElementWrapper> createMerger(ElementWrapper rightChild) {
            return new ProfileMerge();
        }
    }

    private static final class LicensesMerge extends IdentityMerge {
        @Override
        protected Set<String> getChildElementNames() {
            return Set.of("license");
        }

        @Override
        protected String getIdElementName() {
            return "name";
        }
    }

    private static final class DevelopersMerge extends IdentityMerge {
        @Override
        protected Set<String> getChildElementNames() {
            return Set.of("developer");
        }

        @Override
        protected String getIdElementName() {
            return "name";
        }
    }

    private static final class DistributionManagementMerge extends IdentityMerge {
        @Override
        protected Set<String> getChildElementNames() {
            return Set.of("repository", "snapshotRepository");
        }

        @Override
        protected String getIdElementName() {
            return "id";
        }
    }

    private static String requireChildText(ElementWrapper element, String childName) {
        return element.childTextContentsTrimmed(childName)
                .findAny()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Element %s should have child element %s", element.path(), childName)));
    }

    private static boolean hasChildText(ElementWrapper element, String childName, String text) {
        return element.childTextContentsTrimmed(childName).anyMatch(text::equals);
    }
}
