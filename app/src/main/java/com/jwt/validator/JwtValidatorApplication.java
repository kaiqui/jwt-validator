package com.jwt.validator;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "JWT Validation API", 
    version = "1.0", 
    description = "API for validating JWTs according to specific business rules"
))
public class JwtValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtValidatorApplication.class, args);
    }
}