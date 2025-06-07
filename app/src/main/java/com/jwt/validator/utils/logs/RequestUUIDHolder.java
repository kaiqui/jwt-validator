package com.jwt.validator.utils.logs;

import java.util.UUID;

public class RequestUUIDHolder {
    private static final ThreadLocal<String> uuidHolder = new ThreadLocal<>();

    public static void setUuid(String uuid) {
        if (uuidHolder.get() == null) {
            uuidHolder.set(uuid);
        }
    }

    public static String getUuid() {
        String uuid = uuidHolder.get();
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            uuidHolder.set(uuid);
        }
        return uuid;
    }

    public static void clear() {
        uuidHolder.remove();
    }
}
