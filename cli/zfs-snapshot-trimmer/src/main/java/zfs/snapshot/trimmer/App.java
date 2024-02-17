package zfs.snapshot.trimmer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 * @author ngeor
 * @version $Id: $Id
 */
public class App {
    private static final String ZFS_EXECUTABLE = "zfs";

    private ProcessRunner processRunner = new ProcessRunner();
    private SnapshotTrimmer snapshotTrimmer = new SnapshotTrimmer();
    private boolean dryRun;

    /**
     * <p>run.</p>
     *
     * @throws java.io.IOException            if any.
     * @throws java.lang.InterruptedException if any.
     */
    private void run() throws IOException, InterruptedException {
        List<String> inputLines = processRunner.run(ZFS_EXECUTABLE, "list", "-t", "snapshot", "-H", "-p");
        if (isDryRun()) {
            inputLines.forEach(System.out::println);
        }

        List<SnapshotLine> snapshotLines =
                inputLines.stream().map(SnapshotLine::new).collect(Collectors.toList());
        List<SnapshotLine> snapshotsToTrim = snapshotTrimmer.trim(snapshotLines);
        for (SnapshotLine snapshotToTrim : snapshotsToTrim) {
            if (isDryRun()) {
                System.out.println("would destroy " + snapshotToTrim.getFullName());
            } else {
                processRunner.run(ZFS_EXECUTABLE, "destroy", snapshotToTrim.getFullName());
            }
        }
    }

    /**
     * <p>isDryRun.</p>
     *
     * @return a boolean.
     */
    private boolean isDryRun() {
        return dryRun;
    }

    /**
     * <p>Setter for the field <code>dryRun</code>.</p>
     *
     * @param dryRun a boolean.
     */
    private void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException            if any.
     * @throws java.lang.InterruptedException if any.
     */
    public static void main(final String[] args) throws IOException, InterruptedException {
        App app = new App();
        app.setDryRun(Arrays.stream(args).anyMatch(arg -> arg.equals("--dry-run")));
        app.run();
    }
}
