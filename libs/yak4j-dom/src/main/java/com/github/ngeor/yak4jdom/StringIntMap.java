package com.github.ngeor.yak4jdom;

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

class StringIntMap {
    private final String[] items;
    private final int[] orders;
    private int actualLength;

    public StringIntMap(String... items) {
        Objects.requireNonNull(items);
        this.items = new String[items.length];
        this.orders = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            insertionSort(items[i], i);
        }
        this.actualLength = items.length;
    }

    private void insertionSort(String item, int originalIndex) {
        Validate.notBlank(item);
        Validate.isTrue(originalIndex >= 0);
        int i = 0;
        while (i < items.length && items[i] != null) {
            int cmp = item.compareTo(items[i]);
            if (cmp == 0) {
                throw new IllegalArgumentException("Duplicate item: " + item);
            } else if (cmp < 0) {
                // shift existing items right
                Validate.validState(items[items.length - 1] == null, "Insufficient space to insert %s", item);
                for (int j = items.length - 1; j > i; j--) {
                    items[j] = items[j - 1];
                    orders[j] = orders[j - 1];
                }
                break;
            } else {
                // continue iteration
                i++;
            }
        }

        Validate.validState(i < items.length, "Insufficient space to insert %s", item);
        items[i] = item;
        orders[i] = originalIndex;
    }

    public void remove(String key) {
        int idx = Arrays.binarySearch(items, key);
        if (idx >= 0) {
            this.orders[idx] = -1;
            this.actualLength--;
        }
    }

    public int get(String key) {
        int idx = Arrays.binarySearch(items, key);
        if (idx >= 0) {
            return this.orders[idx];
        } else {
            return idx;
        }
    }

    public boolean isEmpty() {
        return actualLength <= 0;
    }
}
