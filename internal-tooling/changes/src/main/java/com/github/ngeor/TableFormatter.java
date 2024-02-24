package com.github.ngeor;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class TableFormatter {
    private TableFormatter() {}

    public enum Alignment {
        LEFT,
        RIGHT
    }

    public static void padColumns(List<List<String>> table, List<Alignment> columnAlignments) {
        Objects.requireNonNull(table);
        int rows = table.size();
        if (rows <= 1) {
            // no need to figure out max widths
            return;
        }

        Validate.noNullElements(table);
        int cols = table.get(0).size();
        Validate.isTrue(cols > 0, "Table must have at least one column");
        Validate.noNullElements(Objects.requireNonNull(columnAlignments));
        Validate.isTrue(cols == columnAlignments.size(), "Column alignments size mismatch");

        // calculate max width per column
        int[] maxWidths = new int[cols];
        for (int row = 0; row < rows; row++) {
            Validate.isTrue(cols == table.get(row).size(), "Every row must have the same number of columns");
            Validate.noNullElements(table.get(row));
            for (int col = 0; col < cols; col++) {
                String item = table.get(row).get(col);
                maxWidths[col] = Math.max(maxWidths[col], item.length());
            }
        }

        // pad every item
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String item = table.get(row).get(col);
                if (item.length() < maxWidths[col]) {
                    String padding = " ".repeat(maxWidths[col] - item.length());
                    String newItem =
                            switch (columnAlignments.get(col)) {
                                case LEFT -> item + padding;
                                case RIGHT -> padding + item;
                            };
                    table.get(row).set(col, newItem);
                }
            }
        }
    }

    public static void printTable(List<List<String>> table) {
        for (List<String> row : table) {
            for (int col = 0; col < row.size(); col++) {
                if (col > 0) {
                    System.out.print(' ');
                }
                System.out.print(row.get(col));
            }
            System.out.println();
        }
    }
}
