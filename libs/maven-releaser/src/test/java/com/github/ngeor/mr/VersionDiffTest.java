package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.versions.SemVer;
import org.junit.jupiter.api.Test;

class VersionDiffTest {
    @Test
    void nullOldVersion() {
        assertThatThrownBy(() -> new VersionDiff(null, new SemVer(1, 0, 0))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullNewVersion() {
        assertThatThrownBy(() -> new VersionDiff(new MavenCoordinates("com.acme", "foo", "1.0"), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void missingArtifactId() {
        assertThatThrownBy(() -> new VersionDiff(new MavenCoordinates("com.acme", null, "1.0"), new SemVer(1, 1, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getters() {
        MavenCoordinates coordinates = new MavenCoordinates("com.acme", "foo", "1.0");
        SemVer newVersion = new SemVer(1, 1, 0);
        VersionDiff versionDiff = new VersionDiff(coordinates, newVersion);
        assertThat(versionDiff.oldVersion()).isEqualTo(coordinates);
        assertThat(versionDiff.newVersion()).isEqualTo(newVersion);
    }

    @Test
    void toSnapshot() {
        MavenCoordinates coordinates = new MavenCoordinates("com.acme", "foo", "1.0");
        SemVer newVersion = new SemVer(1, 1, 0);
        VersionDiff versionDiff = new VersionDiff(coordinates, newVersion).toSnapshot();
        assertThat(versionDiff.oldVersion()).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.1.0"));
        assertThat(versionDiff.newVersion()).isEqualTo(new SemVer(1, 2, 0, "SNAPSHOT"));
    }
}
