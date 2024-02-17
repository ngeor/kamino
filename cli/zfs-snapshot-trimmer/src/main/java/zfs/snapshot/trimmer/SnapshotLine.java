package zfs.snapshot.trimmer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ngeor on 8/10/16.
 *
 * @author ngeor
 * @version $Id: $Id
 */
public class SnapshotLine {
    private final String fullName;
    private final String fileSystem;
    private final String snapshotName;
    private final int year;
    private final int month;

    /**
     * <p>Constructor for SnapshotLine.</p>
     *
     * @param inputLine a {@link java.lang.String} object.
     */
    SnapshotLine(final String inputLine) {
        // line: tank/remote-mirror/box/box@20150615 87296 - 183079009872 -
        // separated by tabs
        fullName = inputLine.split("\t")[0];
        String[] parts = fullName.split("@");
        fileSystem = parts[0];
        snapshotName = parts[1];
        Pattern yearMonthPattern = Pattern.compile("^([0-9]{4})([0-9]{2})");
        Matcher matcher = yearMonthPattern.matcher(snapshotName);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Could not parse year-month from snapshot name");
        }

        year = Integer.parseInt(matcher.group(1));
        month = Integer.parseInt(matcher.group(2));
    }

    /**
     * <p>Getter for the field <code>fileSystem</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getFileSystem() {
        return fileSystem;
    }

    /**
     * <p>Getter for the field <code>snapshotName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getSnapshotName() {
        return snapshotName;
    }

    /**
     * Gets the year of this line.
     *
     * @return a int.
     */
    int getYear() {
        return year;
    }

    /**
     * Gets the month of this line.
     *
     * @return a int.
     */
    int getMonth() {
        return month;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks if this object equals the given object.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnapshotLine that = (SnapshotLine) o;
        return fullName.equals(that.fullName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("SnapshotLine{fileSystem='%s', snapshotName='%s'}", fileSystem, snapshotName);
    }

    /**
     * <p>Getter for the field <code>fullName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getFullName() {
        return fullName;
    }
}
