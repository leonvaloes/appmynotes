package com.example.appmynotes.util;

public final class InputValidator {
    private InputValidator() {}

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isHexColor(String value) {
        return value != null && value.matches("#[0-9A-Fa-f]{6}");
    }
}
