package com.cachedcloud.dynamicquests.utils;

@FunctionalInterface
public interface TriFunction<T, U, K, R> {

  R apply(T t, U u, K k);

}
