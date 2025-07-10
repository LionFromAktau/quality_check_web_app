package com.qualityinspection.swequalityinspection.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.qualityinspection.swequalityinspection.model.enums.BatchStatus;
import com.qualityinspection.swequalityinspection.model.enums.CheckResultStatus;
import com.qualityinspection.swequalityinspection.model.enums.DefectStatus;

public class EnumModule extends SimpleModule {
    public EnumModule() {
        // You can register multiple enum types here
        addDeserializer(DefectStatus.class, new GenericEnumDeserializer<>(DefectStatus.class));
        addDeserializer(BatchStatus.class, new GenericEnumDeserializer<>(BatchStatus.class));
        addDeserializer(CheckResultStatus.class, new GenericEnumDeserializer<>(CheckResultStatus.class));
    }
}