package com.group11.healthtrackerapp.FoodTracker.Schema;

public class FoodOption {

    private String foodName;
    private String imageUrl;

    public FoodOption(String foodName, String imageUrl) {
        this.foodName = foodName;
        this.imageUrl = imageUrl;
    }

    public FoodOption() {
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}