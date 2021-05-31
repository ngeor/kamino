package com.github.ngeor.yak4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for text files.
 */
class TextUtil {
    /**
     * Filters the given stream.
     */
    List<String[]> filter(InputStream inputStream, Pattern pattern) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return filter(bufferedReader, pattern);
        }
    }

    /**
     * Filters the given file.
     */
    List<String[]> filter(File file, Pattern pattern) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return filter(bufferedReader, pattern);
        }
    }

    /**
     * Filters the given reader.
     */
    List<String[]> filter(BufferedReader bufferedReader, Pattern pattern) throws IOException {
        String line = bufferedReader.readLine();
        List<String[]> result = new ArrayList<>();

        while (line != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                result.add(new String[]{line, matcher.group(1)});
            }

            line = bufferedReader.readLine();
        }

        return result;
    }
}
