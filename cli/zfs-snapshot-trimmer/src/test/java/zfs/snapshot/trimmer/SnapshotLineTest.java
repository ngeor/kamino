package zfs.snapshot.trimmer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit test for SnapshotLine class.
 * Created by ngeor on 8/10/16.
 */
public class SnapshotLineTest {
    @Test
    public void shouldParseLine() {
        final String line = "tank/remote-mirror/box/vbox@20150615\t87296\t-\t183079009872\t-";

        SnapshotLine snapshotLine = new SnapshotLine(line);

        assertEquals("tank/remote-mirror/box/vbox", snapshotLine.getFileSystem());
        assertEquals("20150615", snapshotLine.getSnapshotName());
        assertEquals("tank/remote-mirror/box/vbox@20150615", snapshotLine.getFullName());
        final int expectedYear = 2015;
        assertEquals(expectedYear, snapshotLine.getYear());
        final int expectedMonth = 6;
        assertEquals(expectedMonth, snapshotLine.getMonth());
    }
}
