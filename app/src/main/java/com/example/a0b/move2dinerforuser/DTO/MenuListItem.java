package com.example.a0b.move2dinerforuser.DTO;


public class MenuListItem {
    private String foodName;
    private String foodDescribe;
    private int foodPrice;
    private String foodStoragePath;

    public MenuListItem() {
    }

    public MenuListItem(String foodName, String foodDescribe, int foodPrice) {
        this.foodName = foodName;
        this.foodDescribe = foodDescribe;
        this.foodPrice = foodPrice;
    }

    public MenuListItem(String foodName, String foodDescribe, int foodPrice, String foodStoragePath) {
        this.foodName = foodName;
        this.foodDescribe = foodDescribe;
        this.foodPrice = foodPrice;
        this.foodStoragePath = foodStoragePath;
    }

    public String getFoodStoragePath() {
        return foodStoragePath;
    }

    public void setFoodStoragePath(String foodStoragePath) {
        this.foodStoragePath = foodStoragePath;
    }

    public void setFoodName(String name) {
        foodName = name;
    }

    public void setFoodDescribe(String describe) {
        foodDescribe = describe;
    }

    public void setFoodPrice(int price) {
        foodPrice = price;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodDescribe() {
        return foodDescribe;
    }

    public int getFoodPrice() {
        return foodPrice;
    }
}
