package com.example.communitycenter.model;

public enum ResourceType {
    DOCTORS("doctors"),
    VOLUNTEERS("volunteers"),
    MEDICAL_SUPPLIES_KITS("medicalSuppliesKits"),
    TRANSPORT_VEHICLES("transportVehicles"),
    BASIC_FOOD_BASKETS("basicFoodBaskets");

    private final String resourceName;

    ResourceType(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public static ResourceType fromName(String name) {
        for (ResourceType type : values()) {
            if (type.getResourceName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown resource type: " + name);
    }
}

