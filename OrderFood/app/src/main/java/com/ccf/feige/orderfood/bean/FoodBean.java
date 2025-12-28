package com.ccf.feige.orderfood.bean;

import java.io.Serializable;

public class FoodBean implements Serializable {
    // 逻辑删除状态常量（与Dao层、DBUntil保持一致，便于维护，避免魔法值）
    public static final int NOT_DELETED = 0; // 未删除/正常
    public static final int IS_DELETED = 1; // 已逻辑删除

    // 原有字段
    private String foodId;
    private String businessId;
    private String foodName;
    private String foodDes;
    private String foodPrice;
    private String foodImg;

    // 新增：逻辑删除字段（对应数据库表 d_food 的 s_is_delete 字段）
    private int sIsDelete;

    // 原有无参构造
    public FoodBean() {
        // 默认未删除，与数据库默认值一致
        this.sIsDelete = NOT_DELETED;
    }

    // 原有有参构造（兼容旧代码，默认未删除）
    public FoodBean(String foodId, String businessId, String foodName, String foodDes, String foodPrice, String foodImg) {
        this.foodId = foodId;
        this.businessId = businessId;
        this.foodName = foodName;
        this.foodDes = foodDes;
        this.foodPrice = foodPrice;
        this.foodImg = foodImg;
        // 默认未删除，与数据库默认值一致
        this.sIsDelete = NOT_DELETED;
    }

    // 新增：带逻辑删除字段的有参构造（适配Dao层查询/新增，完整接收数据库字段）
    public FoodBean(String foodId, String businessId, String foodName, String foodDes, String foodPrice, String foodImg, int sIsDelete) {
        this.foodId = foodId;
        this.businessId = businessId;
        this.foodName = foodName;
        this.foodDes = foodDes;
        this.foodPrice = foodPrice;
        this.foodImg = foodImg;
        this.sIsDelete = sIsDelete;
    }

    // 原有字段的getter/setter（保持不变，兼容旧代码）
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodDes() {
        return foodDes;
    }

    public void setFoodDes(String foodDes) {
        this.foodDes = foodDes;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodImg() {
        return foodImg;
    }

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }

    // 新增：逻辑删除字段的getter/setter（与UserBean风格保持一致）
    public int getsIsDelete() {
        return sIsDelete;
    }

    public void setsIsDelete(int sIsDelete) {
        // 校验字段值，确保只有0/1两种合法状态，避免非法值导致业务异常
        this.sIsDelete = (sIsDelete == NOT_DELETED || sIsDelete == IS_DELETED) ? sIsDelete : NOT_DELETED;
    }

    // 重写toString()：新增逻辑删除字段，便于调试和日志打印，转换为文字描述更直观
    @Override
    public String toString() {
        return "FoodBean{" +
                "foodId='" + foodId + '\'' +
                ", businessId='" + businessId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodDes='" + foodDes + '\'' +
                ", foodPrice='" + foodPrice + '\'' +
                ", foodImg='" + foodImg + '\'' +
                ", sIsDelete=" + (sIsDelete == NOT_DELETED ? "未删除" : "已逻辑删除") +
                '}';
    }
}