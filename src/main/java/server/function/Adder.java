package server.function;

@FunctionalInterface
public interface Adder<T> {
    void add(T t);
}