package com.conductor.core.util;

public class Pair<K, V> {
    private final K key;
    private final V value;

    private Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of (K key, V value) {
        return new Pair<>(key, value);
    }

    public K getKey() { return key; }
    public V getValue() { return value; }

    public K getStatus(){return key;}
    public V getMessage(){return value;}

    public K getLeft() { return key; }
    public V getRight() { return value; }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}

