package com.github.ngeor;

import com.github.ngeor.maven.dom.MavenCoordinates;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CachedModuleRepository extends ModuleRepository {
    private final Map<String, MavenCoordinates> moduleToCoordinates = new HashMap<>();
    private final Map<MavenCoordinates, String> coordinatesToModule = new HashMap<>();
    private final Map<String, Set<String>> moduleToDependencies = new HashMap<>();

    @Override
    public MavenCoordinates moduleCoordinates(String name) {
        return moduleToCoordinates.computeIfAbsent(name, super::moduleCoordinates);
    }

    @Override
    public Optional<String> moduleByCoordinates(MavenCoordinates coordinates) {
        return Optional.ofNullable(coordinatesToModule.computeIfAbsent(
                coordinates, key -> super.moduleByCoordinates(key).orElse(null)));
    }

    @Override
    public Set<String> dependenciesOf(String name) {
        return moduleToDependencies.computeIfAbsent(name, super::dependenciesOf);
    }
}
