package com.qualityinspection.swequalityinspection.model.enums;
//enums for roles
public enum AppRole {
    admin,
    productionWorker,
    productionManager,
    qualityInspector,
    qualityManager;

    public String asAuthority() {
        return ("role_" + this.name()).toLowerCase();
    }
}
