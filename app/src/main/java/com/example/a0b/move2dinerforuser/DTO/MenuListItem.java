package com.example.a0b.move2dinerforuser.DTO;


public class MenuListItem {
    private String foodName;

    public MenuListItem() {
    }

    public MenuListItem(String foodName, String foodDescribe, int foodPrice) {
        this.foodName = foodName;
        this.foodDescribe = foodDescribe;
        this.foodPrice = foodPrice;
    }

    private String foodDescribe;
    private int foodPrice;

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
