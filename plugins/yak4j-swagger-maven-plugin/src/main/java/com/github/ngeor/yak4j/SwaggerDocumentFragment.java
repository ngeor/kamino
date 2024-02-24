package com.github.ngeor.yak4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Represents a fragment of a swagger document.
 */
public class SwaggerDocumentFragment {
    private final Map<String, Object> data;

    /**
     * Creates an instance of this class.
     *
     * @param data The data of the fragment.
     */
    public SwaggerDocumentFragment(Map<String, Object> data) {
        this.data = Objects.requireNonNull(data);
    }

    public int size() {
        return data.size();
    }

    public String getValue(String key) {
        return (String) data.get(key);
    }

    /**
     * Finds a string value at the given path.
     *
     * @param keyPath A key path separated by dots.
     * @return The value.
     */
    public String lookupValue(String keyPath) {
        String[] keys = keyPath.split("\\.");
        SwaggerDocumentFragment map = this;
        for (int i = 0; i < keys.length - 1; i++) {
            map = map.getFragment(keys[i]);
        }

        return map.getValue(keys[keys.length - 1]);
    }

    /**
     * Finds a document fragment at the given path.
     * @param keyPath A key path separated by dots.
     * @return The document fragment.
     */
    public SwaggerDocumentFragment lookupFragment(String keyPath) {
        String[] keys = keyPath.split("\\.");
        SwaggerDocumentFragment map = this;
        for (String key : keys) {
            map = map.getFragment(key);
        }

        return map;
    }

    public SwaggerDocumentFragment getFragment(String key) {
        return new SwaggerDocumentFragment((Map<String, Object>) data.get(key));
    }

    /**
     * Gets the fragment at the given key.
     * If it is not present, a new empty fragment is created.
     * @param key The key.
     * @return The document fragment.
     */
    public SwaggerDocumentFragment ensureFragment(String key) {
        Object value = data.get(key);
        final Map<String, Object> map;
        if (value == null) {
            map = new LinkedHashMap<>();
            data.put(key, map);
        } else {
            if (value instanceof Map) {
                map = (Map<String, Object>) value;
            } else {
                throw new IllegalArgumentException("Key " + key + " already exists and it is not a fragment");
            }
        }

        return new SwaggerDocumentFragment(map);
    }

    public List<String> getValues(String key) {
        return (List<String>) data.get(key);
    }

    public List<SwaggerDocumentFragment> getFragments(String key) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(key);
        return list.stream().map(SwaggerDocumentFragment::new).collect(Collectors.toList());
    }

    /**
     * Gets the object at the given key.
     *
     * @param key The key.
     * @return The value.
     */
    public Object get(String key) {
        Object value = data.get(key);
        if (value instanceof Map) {
            return getFragment(key);
        } else if (value instanceof List list) {
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                return getFragments(key);
            }

            return getValues(key);
        } else {
            return value;
        }
    }

    public String[] keys() {
        return data.keySet().toArray(String[]::new);
    }

    public void renameKey(String oldName, String newName) {
        data.put(newName, data.remove(oldName));
    }

    /**
     * Visits all nodes recursively with the given visitor.
     *
     * @param visitor The visitor.
     */
    public void visit(BiFunction<String, Object, Object> visitor) {
        visitMap(data, visitor);
    }

    private static void visitMap(Map<String, Object> map, BiFunction<String, Object, Object> visitor) {
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                visitMap((Map<String, Object>) value, visitor);
            } else if (value instanceof List list) {
                visitList(list, visitor);
            } else {
                Object newValue = visitor.apply(key, value);
                if (newValue != value) {
                    map.put(key, newValue);
                }
            }
        });
    }

    private static void visitList(List list, BiFunction<String, Object, Object> visitor) {
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            if (value instanceof Map) {
                visitMap((Map<String, Object>) value, visitor);
            } else if (value instanceof List list1) {
                visitList(list1, visitor);
            } else {
                Object newValue = visitor.apply(String.valueOf(i), value);
                if (newValue != value) {
                    list.set(i, newValue);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SwaggerDocumentFragment)) {
            return false;
        }

        SwaggerDocumentFragment that = (SwaggerDocumentFragment) obj;
        if (size() != that.size()) {
            return false;
        }

        String[] keys = keys();
        for (String key : keys) {
            Object mine = get(key);
            Object theirs = that.get(key);
            if (!Objects.equals(mine, theirs)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public String toString() {
        SwaggerWriter swaggerWriter = new SwaggerWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            swaggerWriter.write(this, byteArrayOutputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return byteArrayOutputStream.toString();
    }

    /**
     * Appends the keys and values of the given fragment.
     * If the keys already exist, an {@link IllegalArgumentException} will be thrown.
     * @param fragment The fragment to append from.
     */
    public void append(SwaggerDocumentFragment fragment) {
        if (fragment == null) {
            return;
        }

        fragment.data.forEach((key, value) -> {
            if (data.containsKey(key)) {
                throw new IllegalArgumentException("Key " + key + " already exists");
            }

            data.put(key, value);
        });
    }
}
