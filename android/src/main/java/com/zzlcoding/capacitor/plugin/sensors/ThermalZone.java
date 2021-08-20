package com.zzlcoding.capacitor.plugin.sensors;

public class ThermalZone {
    private String name;
    private Float value;

    ThermalZone(String name, Float value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
