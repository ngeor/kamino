package zfs.snapshot.trimmer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for SnapshotTrimmer class.
 * Created by ngeor on 8/10/16.
 */
public class SnapshotTrimmerTest {
    private static final String TANK_MEDIA_20160902 = "tank/media@20160902";
    private static final String TANK_MEDIA_20150902 = "tank/media@20150902";
    private static final String TANK_MEDIA_20151001 = "tank/media@20151001";
    private static final String TANK_MEDIA_20151002 = "tank/media@20151002";
    private static final String TANK_MEDIA_20150901 = "tank/media@20150901";
    private static final String TANK_MEDIA_20160901 = "tank/media@20160901";
    private static final String TANK_A_20150901 = "tank/a@20150901";
    private static final String TANK_A_20150902 = "tank/a@20150902";
    private static final String TANK_B_20151001 = "tank/b@20151001";
    private static final String TANK_B_20151002 = "tank/b@20151002";
    private static final String TANK_MEDIA_20161001 = "tank/media@20161001";
    private static final String TANK_MEDIA_20161002 = "tank/media@20161002";
    private SnapshotTrimmer snapshotTrimmer;

    @BeforeEach
    public void before() {
        snapshotTrimmer =
                new SnapshotTrimmer(Clock.fixed(Instant.parse("2016-10-08T00:00:00.00Z"), ZoneId.systemDefault()));
    }

    @Test
    public void shouldAcceptEmptyInput() {
        List<SnapshotLine> result = snapshotTrimmer.trim(new ArrayList<>());

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldKeepEverythingOfTheCurrentMonth() {
        List<SnapshotLine> input =
                Arrays.asList(new SnapshotLine(TANK_MEDIA_20161001), new SnapshotLine(TANK_MEDIA_20161002));

        List<SnapshotLine> result = snapshotTrimmer.trim(input);

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldKeepFirstSnapshotOfMonthOfCurrentYear() {
        List<SnapshotLine> input =
                Arrays.asList(new SnapshotLine(TANK_MEDIA_20160901), new SnapshotLine(TANK_MEDIA_20160902));

        List<SnapshotLine> result = snapshotTrimmer.trim(input);

        assertThat(result).containsExactly(new SnapshotLine(TANK_MEDIA_20160902));
    }

    @Test
    public void shouldKeepFirstSnapshotOfFirstMonthForPreviousYears() {
        List<SnapshotLine> input = Arrays.asList(
                new SnapshotLine(TANK_MEDIA_20150901),
                new SnapshotLine(TANK_MEDIA_20150902),
                new SnapshotLine(TANK_MEDIA_20151001),
                new SnapshotLine(TANK_MEDIA_20151002));

        List<SnapshotLine> result = snapshotTrimmer.trim(input);

        assertThat(result)
                .containsExactly(
                        new SnapshotLine(TANK_MEDIA_20150902),
                        new SnapshotLine(TANK_MEDIA_20151001),
                        new SnapshotLine(TANK_MEDIA_20151002));
    }

    @Test
    public void shouldSupportMultipleFileSystems() {
        List<SnapshotLine> input = Arrays.asList(
                new SnapshotLine(TANK_A_20150901),
                new SnapshotLine(TANK_A_20150902),
                new SnapshotLine(TANK_B_20151001),
                new SnapshotLine(TANK_B_20151002));

        List<SnapshotLine> result = snapshotTrimmer.trim(input);

        assertThat(result).containsExactly(new SnapshotLine(TANK_A_20150902), new SnapshotLine(TANK_B_20151002));
    }
}
