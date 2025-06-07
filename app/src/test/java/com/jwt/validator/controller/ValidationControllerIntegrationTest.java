package com.jwt.validator.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidationControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String token) {
        return "http://localhost:" + port + "/api/v1/validate?token=" + token;
    }

    @Nested
    @DisplayName("Valid token cases")
    class ValidTokenCases {

        @Test
        @DisplayName("Should return true for valid token")
        void shouldReturnTrueForValidToken() {
            String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url(validToken), Boolean.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid token cases")
    class InvalidTokenCases {

        @Test
        @DisplayName("Should return 400 for malformed token")
        void shouldReturn400ForMalformedToken() {
            String invalidToken = "invalid.jwt.token";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url(invalidToken), Boolean.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isFalse();
        }

        @Test
        @DisplayName("Should return 400 for token missing claims")
        void shouldReturn400ForMissingClaims() {
            String tokenMissingClaims = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSJ9.XIcNK39XywFXfjswotre28BTfSS_eGX8HdCqQiuCtnk";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url(tokenMissingClaims), Boolean.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isFalse();
        }

        @Test
        @DisplayName("Should return 400 for Name with number")
        void shouldReturn400ForNameWithNumber() {
            String tokenNameWithNumber = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url(tokenNameWithNumber), Boolean.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isFalse();
        }

        @Test
        @DisplayName("Should return 400 for Role not allowed")
        void shouldReturn400ForRoleNotAllowed() {
            String tokenRoleInvalid = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiSW50ZXJuYWwiLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9._zx_zo7K_0WLKiflYVOqo_YNbxub59rZ4ax4gLlHoNo";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url(tokenRoleInvalid), Boolean.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isFalse();
        }

        @Test
        @DisplayName("Should return 400 for Seed not prime")
        void shouldReturn400ForSeedNotPrime() {
            String tokenSeedNotPrime = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiMTAiLCJOYW1lIjoiVG9uaW5obyBBcmF1am8ifQ.wMQO8f_RTE6AdwVfUk4fs5mEz7SPUXr1g-Oj6U_ehtQ";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url(tokenSeedNotPrime), Boolean.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isFalse();
        }

        @Test
        @DisplayName("Should return 400 for Name longer than 256 characters")
        void shouldReturn400ForNameTooLong() {
            String tokenNameTooLong = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUEifQ.xumDplNJeVCIbG5iYXABDtx8d1ns-vYSxZ8Ev1bz1gQ";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url(tokenNameTooLong), Boolean.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isFalse();
        }
    }
}