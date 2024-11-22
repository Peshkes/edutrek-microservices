package com.telran.groupservice;

@FunctionalInterface
public interface ThreeFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
