package zfs.snapshot.trimmer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngeor on 8/10/16.
 *
 * @author ngeor
 * @version $Id: $Id
 */
class ProcessRunner {
    /**
     * Runs the given command and returns the output lines.
     *
     * @param command a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     * @throws java.io.IOException            if any.
     * @throws java.lang.InterruptedException if any.
     */
    List<String> run(final String... command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).start();
        InputStream stdout = process.getInputStream();
        InputStreamReader reader = new InputStreamReader(stdout);
        BufferedReader bufferedReader = new BufferedReader(reader);
        int exitValue = process.waitFor();
        String line;
        List<String> result = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            result.add(line);
        }

        return result;
    }
}
