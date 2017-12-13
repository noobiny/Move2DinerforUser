package com.example.a0b.move2dinerforuser.DTO;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ItemTruckDes implements Serializable {
    private String truckName;
    private String truckDes;
    private String thumbnail;
    private String busiInfo;
    private String keys;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    private String recentAddress;
    private String recentLat;
    private String recentLon,startTime;
    private Boolean onBusiness;
    private Integer distance;
    private Boolean payCard;


    public Boolean getPayCard() {
        return payCard;
    }

    public void setPayCard(Boolean payCard) {
        this.payCard = payCard;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Boolean getOnBusiness() {
        return onBusiness;
    }

    public void setOnBusiness(Boolean onBusiness) {
        this.onBusiness = onBusiness;
    }

    public String getRecentAddress() {
        return recentAddress;
    }

    public void setRecentAddress(String recentAddress) {
        this.recentAddress = recentAddress;
    }

    public String getRecentLat() {
        return recentLat;
    }

    public void setRecentLat(String recentLat) {
        this.recentLat = recentLat;
    }

    public String getRecentLon() {
        return recentLon;
    }

    public void setRecentLon(String recentLon) {
        this.recentLon = recentLon;
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

    public String getTruckDes() {
        return truckDes;
    }

    public void setTruckDes(String truckDes) {
        this.truckDes = truckDes;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBusiInfo() {
        return busiInfo;
    }

    public void setBusiInfo(String busiInfo) {
        this.busiInfo = busiInfo;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }

}
