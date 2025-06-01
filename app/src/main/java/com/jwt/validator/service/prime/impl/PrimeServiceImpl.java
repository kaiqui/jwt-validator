package com.jwt.validator.service.prime.impl;

import org.springframework.stereotype.Service;
import com.jwt.validator.service.prime.PrimeService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PrimeServiceImpl implements PrimeService {
    
    private static final Map<Integer, Boolean> primeCache = new ConcurrentHashMap<>();
    
    @Override
    public boolean isPrime(int number) {
        if (number <= 1) return false;
        return primeCache.computeIfAbsent(number, this::calculateIsPrime);
    }
    
    private boolean calculateIsPrime(int number) {
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        
        int sqrt = (int) Math.sqrt(number);
        for (int i = 3; i <= sqrt; i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }
}