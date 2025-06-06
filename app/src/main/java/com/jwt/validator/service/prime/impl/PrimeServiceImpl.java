package com.jwt.validator.service.prime.impl;

import com.jwt.validator.service.prime.PrimeService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PrimeServiceImpl implements PrimeService {

    private static final Map<Integer, Boolean> primeCache = new ConcurrentHashMap<>();

    @Override
    public boolean isPrime(int number) {
        MDC.put("operation", "prime-check");
        MDC.put("number", String.valueOf(number));

        log.debug("Starting prime number validation");

        try {
            if (number <= 1) {
                log.warn("Number {} is less than or equal to 1 - not prime", number);
                return false;
            }

            if (primeCache.containsKey(number)) {
                boolean cachedResult = primeCache.get(number);
                log.debug("Cache hit for number {}: {}", number, cachedResult);
                return cachedResult;
            }

            log.debug("Cache miss for number {}, calculating...", number);
            boolean result = calculateIsPrime(number);

            primeCache.put(number, result);
            log.debug("Cached result for number {}: {}", number, result);

            if (result) {
                log.info("Number {} is prime", number);
            } else {
                log.debug("Number {} is not prime", number);
            }

            return result;
        } catch (Exception e) {
            log.error("Error calculating if number {} is prime", number, e);
            throw e;
        } finally {
            MDC.clear();
            log.debug("Completed prime number validation");
        }
    }

    private boolean calculateIsPrime(int number) {
        log.debug("Starting prime calculation for number {}", number);

        if (number == 2) {
            log.debug("Number is 2 - prime");
            return true;
        }

        if (number % 2 == 0) {
            log.debug("Number {} is even and greater than 2 - not prime", number);
            return false;
        }

        int sqrt = (int) Math.sqrt(number);
        log.debug("Checking divisors up to sqrt({}) = {}", number, sqrt);

        for (int i = 3; i <= sqrt; i += 2) {
            if (number % i == 0) {
                log.debug("Found divisor {} for number {} - not prime", i, number);
                return false;
            }
        }

        log.debug("No divisors found for number {} - prime", number);
        return true;
    }
}