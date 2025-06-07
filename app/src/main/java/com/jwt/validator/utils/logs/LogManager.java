package com.jwt.validator.utils.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogManager<T> {
    private final Logger logger;

    public LogManager(Class<T> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void info(String message, Object object) {
        logger.info("[{}] {}: {}", RequestUUIDHolder.getUuid(), message, object);
    }

    public void info(String message) {
        logger.info("[{}] {}", RequestUUIDHolder.getUuid(), message);
    }

    public void debug(String message, Object object) {
        logger.debug("[{}] {}: {}", RequestUUIDHolder.getUuid(), message, object);
    }
    public void debug(String message, Object object, String value) {
        logger.debug("[{}] {}: {} {}", RequestUUIDHolder.getUuid(), message, object, value);
    }
    public void debug(String message, Object object, String value, String value2) {
        logger.debug("[{}] {}: {} {} {}", RequestUUIDHolder.getUuid(), message, object, value, value2);
    }

    public void debug(String message) {
        logger.debug("[{}] {}", RequestUUIDHolder.getUuid(), message);
    }

    public void error(String message, Object object, Exception e) {
        logger.error("[{}] {}: {}", RequestUUIDHolder.getUuid(), message, object, e);
    }

    public void error(String message, String value) {
        logger.error("[{}] {}", RequestUUIDHolder.getUuid(), message);
    }

    public void warn(String message, Object object) {
        logger.warn("[{}] {}: {}", RequestUUIDHolder.getUuid(), message, object);
    }
    public void warn(String message, Object object, String value) {
        logger.warn("[{}] {}: {} {}", RequestUUIDHolder.getUuid(), message, object, value);
    }

    public void warn(String message) {
        logger.warn("[{}] {}", RequestUUIDHolder.getUuid(), message);
    }
}

