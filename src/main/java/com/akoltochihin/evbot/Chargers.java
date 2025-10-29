package com.akoltochihin.evbot;

public enum Chargers {

    RODINA_CINEMA("Родина", "8d36c69b-92b9-471a-a5e1-53064e744028", "Plug Type2"),
    YAKUBOVSKOGO("Якубовского", "f53c8b47-de88-4445-868b-0c1596b8395a", "CCS");

    public final String name;
    public final String locationId;
    public final String type;

    Chargers(String name, String locationId, String type) {
        this.name = name;
        this.locationId = locationId;
        this.type = type;
    }
}
