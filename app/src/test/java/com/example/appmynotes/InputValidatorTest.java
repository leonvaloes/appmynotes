package com.example.appmynotes;

import com.example.appmynotes.util.InputValidator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputValidatorTest {
    @Test public void blankValuesAreRejected() {
        assertTrue(InputValidator.isBlank(null));
        assertTrue(InputValidator.isBlank("   "));
        assertFalse(InputValidator.isBlank("Anotação"));
    }

    @Test public void colorMustUseRgbHexFormat() {
        assertTrue(InputValidator.isHexColor("#D32F2F"));
        assertTrue(InputValidator.isHexColor("#abcdef"));
        assertFalse(InputValidator.isHexColor("D32F2F"));
        assertFalse(InputValidator.isHexColor("#FFF"));
        assertFalse(InputValidator.isHexColor("#GG0000"));
    }
}
