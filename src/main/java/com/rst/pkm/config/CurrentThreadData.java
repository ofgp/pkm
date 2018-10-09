package com.rst.pkm.config;

/**
 * @author hujia
 */
public class CurrentThreadData {
    @lombok.Data
    private static class Data {
        private String serviceId;
        private String signature;
    }

    private static ThreadLocal<Data> THREAD_LOCAL = ThreadLocal.withInitial(() -> new Data());

    public static String serviceId() {
        return THREAD_LOCAL.get().serviceId;
    }

    public static void setServiceId(String serviceId) {
        THREAD_LOCAL.get().serviceId = serviceId;
    }

    public static String signature() {
        return THREAD_LOCAL.get().signature;
    }

    public static void setSignature(String signature) {
        THREAD_LOCAL.get().signature = signature;
    }
}
