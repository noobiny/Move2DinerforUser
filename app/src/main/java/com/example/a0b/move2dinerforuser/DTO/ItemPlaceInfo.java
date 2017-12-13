package com.example.a0b.move2dinerforuser.DTO;


public class ItemPlaceInfo {
    private String placeName;
    private double latitude, longitude;

    public ItemPlaceInfo() {

    }

    public ItemPlaceInfo(String placeName, double latitude, double longitude) {

        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
