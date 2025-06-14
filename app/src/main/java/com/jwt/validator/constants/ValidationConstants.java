package com.jwt.validator.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ValidationConstants {
    public static final int MAX_NAME_LENGTH = 256;
    public static final Set<String> ALLOWED_ROLES = 
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Admin", "Member", "External")));
    
    private ValidationConstants() {}
}