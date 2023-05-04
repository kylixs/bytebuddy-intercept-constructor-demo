package com.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnhanceHelper {

    private static List<String> interceptors = new ArrayList<>();
    private static List<Map.Entry<String, Throwable>> errors = new ArrayList<>();

    public static void onError(String message, Throwable error) {
        errors.add(new MapEntry(message, error));
    }

    public static void addInterceptor(String interceptor) {
        interceptors.add(interceptor);
    }

    public static List<String> getInterceptors() {
        return interceptors;
    }

    public static List<Map.Entry<String, Throwable>> getErrors() {
        return errors;
    }

    public static void clear() {
        errors.clear();
        interceptors.clear();
    }

    private static class MapEntry<T, P> implements Map.Entry<T, P> {
        private T key;
        private P value;

        public MapEntry(T key, P value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public T getKey() {
            return key;
        }

        @Override
        public P getValue() {
            return value;
        }

        @Override
        public P setValue(P value) {
            this.value = value;
            return value;
        }
    }
}
