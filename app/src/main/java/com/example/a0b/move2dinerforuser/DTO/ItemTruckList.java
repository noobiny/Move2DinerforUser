package com.example.a0b.move2dinerforuser.DTO;

public class ItemTruckList {
    private String keys;
    private String truckName;

    public ItemTruckList(String keys, String truckName) {
        this.keys = keys;
        this.truckName = truckName;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }
}
