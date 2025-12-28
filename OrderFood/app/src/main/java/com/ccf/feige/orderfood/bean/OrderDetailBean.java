package com.ccf.feige.orderfood.bean;

/**
 * 订单详情的bean
 */
public class OrderDetailBean {

    private String detailsId;
    private String foodId;
    private String foodName;
    private String foodDescription;
    private String  foodPrice;

    @Override
    public String toString() {
        return "OrderDetailBean{" +
                "detailsId='" + detailsId + '\'' +
                ", foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodDescription='" + foodDescription + '\'' +
                ", foodPrice='" + foodPrice + '\'' +
                ", foodQuantity='" + foodQuantity + '\'' +
                ", foodImage='" + foodImage + '\'' +
                '}';
    }

    public String getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(String detailsId) {
        this.detailsId = detailsId;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public OrderDetailBean() {
    }

    public OrderDetailBean(String detailsId, String foodId, String foodName, String foodDescription, String foodPrice, String foodQuantity, String foodImage) {
        this.detailsId = detailsId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodDescription = foodDescription;
        this.foodPrice = foodPrice;
        this.foodQuantity = foodQuantity;
        this.foodImage = foodImage;
    }

    private String foodQuantity;
    private String foodImage;
}
