package com.qualityinspection.swequalityinspection.exceptions;

import java.util.List;

public class ExceptionMessages {

    public static String statusInvalidMessage(String enumName, String invalidEnum, String allowedEnumList) {
        return String.format("Invalid value '%s' for enum %s. Allowed values are: %s.",
                invalidEnum, enumName, allowedEnumList);
    }

    public static final String ChecklistResultNotFound = "Checklist result not found";
    public static final String ProductNotFound = "Product not found";
    public static final String BatchNotFound = "Batch not found";
    public static final String BatchAlreadyChecked = "Batch is already checked";
    public static final String UserNotFound = "User not found";
    public static final String ResultAlreadyReported = "Checklist already reported";

    public static String ChecklistItemNotFound(int itemId){
        return "ChecklistItem not found: " + itemId;
    }
    private ExceptionMessages() {}
}