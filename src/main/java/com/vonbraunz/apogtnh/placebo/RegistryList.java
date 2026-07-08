package com.vonbraunz.apogtnh.placebo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple ordered container. Replaces Placebo's RegistryList concept without any
 * IForgeRegistry integration (which doesn't exist in 1.7.10).
 */
public class RegistryList<T> {

    private final List<T> items = new ArrayList<T>();

    public void add(T item) {
        if (item != null && !items.contains(item)) items.add(item);
    }

    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    public int size() { return items.size(); }

    public T get(int i) { return items.get(i); }

    public boolean isEmpty() { return items.isEmpty(); }
}
