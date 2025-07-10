package com.qualityinspection.swequalityinspection.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.qualityinspection.swequalityinspection.exceptions.EnumNotFoundException;

import java.io.IOException;
import java.util.Arrays;

public class GenericEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {

    private final Class<T> enumType;

    public GenericEnumDeserializer(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase();

        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }

        throw new EnumNotFoundException(enumType.getSimpleName(), value, Arrays.toString(enumType.getEnumConstants()));
    }
}

