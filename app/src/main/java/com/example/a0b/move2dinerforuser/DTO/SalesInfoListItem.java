package com.example.a0b.move2dinerforuser.DTO;

public class SalesInfoListItem {
    private String locationlat, locationlon, endtime, starttime, truckName, truckUid, addressLine, thumbnail, salesdate,truckKey;
    private Integer distance;
    private Boolean onBusiness;

    public SalesInfoListItem() {
    }


    public String getTruckKey() {
        return truckKey;
    }

    public void setTruckKey(String truckKey) {
        this.truckKey = truckKey;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getSalesdate() {
        return salesdate;
    }

    public void setSalesdate(String salesdate) {
        this.salesdate = salesdate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getLocationlat() {
        return locationlat;
    }

    public void setLocationlat(String locationlat) {
        this.locationlat = locationlat;
    }

    public String getLocationlon() {
        return locationlon;
    }

    public void setLocationlon(String locationlon) {
        this.locationlon = locationlon;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public String getTruckUid() {
        return truckUid;
    }

    public void setTruckUid(String truckUid) {
        this.truckUid = truckUid;
    }

    public Boolean getOnBusiness() {
        return onBusiness;
    }

    public void setOnBusiness(Boolean onBusiness) {
        this.onBusiness = onBusiness;
    }
}
