package com.jwt.validator.service.prime.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;

class PrimeServiceImplTest {

    private final PrimeServiceImpl primeService = new PrimeServiceImpl();

    @Test
    void isPrime_shouldReturnFalseForNonPrimeNumbers() {
        assertAll(
            () -> assertFalse(primeService.isPrime(0)),
            () -> assertFalse(primeService.isPrime(1)),
            () -> assertFalse(primeService.isPrime(4)),
            () -> assertFalse(primeService.isPrime(6)),
            () -> assertFalse(primeService.isPrime(9)),
            () -> assertFalse(primeService.isPrime(15)),
            () -> assertFalse(primeService.isPrime(21)),
            () -> assertFalse(primeService.isPrime(27))
        );
    }

    @Test
    void isPrime_shouldReturnTrueForPrimeNumbers() {
        assertAll(
            () -> assertTrue(primeService.isPrime(2)),
            () -> assertTrue(primeService.isPrime(3)),
            () -> assertTrue(primeService.isPrime(5)),
            () -> assertTrue(primeService.isPrime(7)),
            () -> assertTrue(primeService.isPrime(11)),
            () -> assertTrue(primeService.isPrime(13)),
            () -> assertTrue(primeService.isPrime(17)),
            () -> assertTrue(primeService.isPrime(19))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {7841, 88037, 14627, 7919, 999983})
    void isPrime_shouldHandleLargePrimeNumbers(int prime) {
        assertTrue(primeService.isPrime(prime));
    }

    @ParameterizedTest
    @ValueSource(ints = {10000, 999999, 123456, 1000000, 999984})
    void isPrime_shouldHandleLargeNonPrimeNumbers(int nonPrime) {
        assertFalse(primeService.isPrime(nonPrime));
    }

    @Test
    void isPrime_shouldCacheResults() {
        int number = 999983;
        
        boolean firstResult = primeService.isPrime(number);
        
        boolean secondResult = primeService.isPrime(number);
        
        assertTrue(firstResult);
        assertTrue(secondResult);
    }

    @Test
    void isPrime_shouldHandleLargeNumbersEfficiently() {
        int largePrime = 2147483647; 
        long startTime = System.nanoTime();
        assertTrue(primeService.isPrime(largePrime));
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration < 900_000, "Verificação eficiente (<90ms)");
    }


    @Test
    void isPrime_shouldHandleNegativeNumbers() {
        assertFalse(primeService.isPrime(-1));
        assertFalse(primeService.isPrime(-5));
        assertFalse(primeService.isPrime(-100));
    }

    @Test
    void isPrime_shouldHandleEdgeCases() {
        assertAll(
            () -> assertFalse(primeService.isPrime(Integer.MIN_VALUE)),
            () -> assertFalse(primeService.isPrime(Integer.MAX_VALUE - 1))
        );
    }
}