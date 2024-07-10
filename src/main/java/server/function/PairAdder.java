package server.function;

@FunctionalInterface
public interface PairAdder<K, V> {
    void add(K k, V v);
}