package com.jwt.validator.service.prime.impl;

import com.jwt.validator.utils.logs.LogManager;
import com.jwt.validator.service.prime.PrimeService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PrimeServiceImpl implements PrimeService {

    private static final Map<Integer, Boolean> primeCache = new ConcurrentHashMap<>();
    private static final LogManager<PrimeServiceImpl> log = new LogManager<>(PrimeServiceImpl.class);

    @Override
    public boolean isPrime(int number) {

        log.debug("Starting prime number validation");

        try {
            if (number <= 1) {
                log.warn("Number is less than or equal to 1 - not prime", number);
                return false;
            }

            if (primeCache.containsKey(number)) {
                boolean cachedResult = primeCache.get(number);
                log.debug("Cache hit for number", number, String.valueOf(number));
                return cachedResult;
            }
            boolean result = calculateIsPrime(number);
            log.debug("Cache miss for number", number, String.valueOf(result));

            primeCache.put(number, result);
            log.debug("Cached result for number", number, String.valueOf(result));

            if (result) {
                log.info("Number is prime", number);
            } else {
                log.debug("Number is not prime", number, String.valueOf(result));
            }

            return result;
        } catch (Exception e) {
            log.error("Error calculating if number is prime", number, e);
            throw e;
        } finally {
            log.debug("Completed prime number validation");
        }
    }

    private boolean calculateIsPrime(int number) {
        log.debug("Starting prime calculation for number", number);

        if (number == 2) {
            log.debug("Number is 2 - prime");
            return true;
        }

        if (number % 2 == 0) {
            log.debug("Number is even and greater than 2 - not prime", String.valueOf(number));
            return false;
        }

        int sqrt = (int) Math.sqrt(number);
        log.debug("Checking divisors up to sqrt ", number, String.valueOf(sqrt));

        for (int i = 3; i <= sqrt; i += 2) {
            if (number % i == 0) {
                log.debug("Found divisor", i, String.valueOf(number));
                return false;
            }
        }

        log.debug("No divisors found for number", number);
        return true;
    }
}