package com.qualityinspection.swequalityinspection.exceptions;

import java.util.List;

public class EnumNotFoundException extends RuntimeException {

    public EnumNotFoundException(String enumName, String invalidEnum, String validEnumList) {
        super(ExceptionMessages.statusInvalidMessage(enumName, invalidEnum, validEnumList));
    }

    public EnumNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

