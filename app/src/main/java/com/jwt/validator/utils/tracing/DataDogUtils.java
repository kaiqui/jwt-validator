package com.jwt.validator.utils.tracing;

import com.jwt.validator.utils.logs.LogManager;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DataDogUtils {

    private DataDogUtils() {
    }

    private static final LogManager<DataDogUtils> log = new LogManager<>(DataDogUtils.class);

    public static void startAndLogSpan(Map<String, Object> tags) {
        final Span span = GlobalTracer.get().activeSpan();

        if (span != null) {
            log.info("Criando Span");
            tags.forEach((key, value) -> {
                if (value instanceof String && StringUtils.isNotBlank((String) value)) {
                    span.setTag(key, (String) value);
                }
            });
            log.warn("Finalizando Span");
        }
    }

    public static void addTag(Map<String, Object> tags, Object key, Object value) {
        if (key instanceof String && value instanceof String && StringUtils.isNotBlank((String) value)) {
            tags.put((String) key, value);
        }
    }
}

