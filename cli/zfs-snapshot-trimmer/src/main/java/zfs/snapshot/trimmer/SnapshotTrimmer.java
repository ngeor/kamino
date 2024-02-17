package zfs.snapshot.trimmer;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ngeor on 8/10/16.
 *
 * @author ngeor
 * @version $Id: $Id
 */
class SnapshotTrimmer {
    private final Clock clock;

    /**
     * <p>Constructor for SnapshotTrimmer.</p>
     */
    SnapshotTrimmer() {
        this(Clock.systemDefaultZone());
    }

    /**
     * <p>Constructor for SnapshotTrimmer.</p>
     *
     * @param clock a {@link java.time.Clock} object.
     */
    SnapshotTrimmer(final Clock clock) {
        this.clock = clock;
    }

    /**
     * <p>trim.</p>
     *
     * @param snapshotLines a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    List<SnapshotLine> trim(final List<SnapshotLine> snapshotLines) {
        Map<String, List<SnapshotLine>> perFileSystem =
                snapshotLines.stream().collect(Collectors.groupingBy(SnapshotLine::getFileSystem));

        return perFileSystem.values().stream()
                .map(this::trimOneFileSystem)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Trims one file system.
     *
     * @param snapshotLines The snapshot lines.
     * @return The filtered snapshot lines.
     */
    private List<SnapshotLine> trimOneFileSystem(final List<SnapshotLine> snapshotLines) {
        Map<Integer, List<SnapshotLine>> perYear =
                snapshotLines.stream().collect(Collectors.groupingBy(SnapshotLine::getYear));
        return perYear.values().stream()
                .map(this::trimOneYear)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Keeps only one year.
     *
     * @param snapshotLines The snapshot lines.
     * @return The filtered snapshot lines.
     */
    private List<SnapshotLine> trimOneYear(final List<SnapshotLine> snapshotLines) {
        List<SnapshotLine> result;
        if (snapshotLines.isEmpty()) {
            result = snapshotLines;
        } else if (snapshotLines.get(0).getYear() == LocalDateTime.now(clock).getYear()) {
            result = trimCurrentYear(snapshotLines);
        } else {
            result = trimPastYear(snapshotLines);
        }

        return result;
    }

    /**
     * Keeps the oldest based on year.
     *
     * @param snapshotLines The snapshot lines.
     * @return The filtered snapshot lines.
     */
    private List<SnapshotLine> trimPastYear(final List<SnapshotLine> snapshotLines) {
        return preserveOldest(snapshotLines);
    }

    /**
     * Keeps the oldest of the given lines.
     *
     * @param snapshotLines The snapshot lines.
     * @return The filtered snapshot lines.
     */
    private List<SnapshotLine> preserveOldest(final List<SnapshotLine> snapshotLines) {
        List<SnapshotLine> sorted = snapshotLines.stream()
                .sorted(Comparator.comparing(SnapshotLine::getSnapshotName))
                .collect(Collectors.toList());

        sorted.remove(0);
        return sorted;
    }

    /**
     * Keeps only the current year from the given lines.
     *
     * @param snapshotLines The snapshot lines.
     * @return The filtered snapshot lines.
     */
    private List<SnapshotLine> trimCurrentYear(final List<SnapshotLine> snapshotLines) {
        Map<Integer, List<SnapshotLine>> perMonth =
                snapshotLines.stream().collect(Collectors.groupingBy(SnapshotLine::getMonth));

        return perMonth.values().stream()
                .map(this::trimOneMonth)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Keeps only the oldest month from the given snapshot lines.
     *
     * @param snapshotLines The snapshot lines.
     * @return The filtered snapshot lines.
     */
    private List<SnapshotLine> trimOneMonth(final List<SnapshotLine> snapshotLines) {
        List<SnapshotLine> result;
        if (snapshotLines.isEmpty()) {
            result = snapshotLines;
        } else if (snapshotLines.get(0).getMonth() == LocalDateTime.now(clock).getMonthValue()) {
            result = Collections.emptyList(); // do not delete anything of the current month
        } else {
            result = preserveOldest(snapshotLines);
        }

        return result;
    }
}
