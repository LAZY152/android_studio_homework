package com.ccf.feige.orderfood.bean;

/**
 * 订单详情的bean
 * 该类用于封装单个订单中某一款食品的详细信息，记录订单与食品的关联及食品自身的核心属性
 */
public class OrderDetailBean {

    /**
     * 订单详情唯一标识ID
     * 用于区分不同的订单详情记录，与订单主表、食品表形成关联映射
     */
    private String detailsId;

    /**
     * 食品唯一标识ID
     * 关联食品表的主键ID，用于定位对应的食品基础信息
     */
    private String foodId;

    /**
     * 食品名称
     * 展示食品的具体名称，如"番茄炒蛋"、"经典汉堡"等
     */
    private String foodName;

    /**
     * 食品描述信息
     * 用于补充说明食品的配料、口味、规格等详情，如"微辣、含芝士、现做现卖"
     */
    private String foodDescription;

    /**
     * 食品单价
     * 记录该食品在当前订单中的售卖单价，以字符串格式存储
     */
    private String  foodPrice;

    /**
     * 重写toString方法
     * 用于打印/日志输出当前OrderDetailBean对象的所有属性及对应值，方便调试和问题排查
     * @return 包含所有属性信息的字符串
     */
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

    /**
     * 获取订单详情唯一标识ID
     * @return 订单详情ID字符串
     */
    public String getDetailsId() {
        return detailsId;
    }

    /**
     * 设置订单详情唯一标识ID
     * @param detailsId 订单详情ID字符串
     */
    public void setDetailsId(String detailsId) {
        this.detailsId = detailsId;
    }

    /**
     * 获取食品唯一标识ID
     * @return 食品ID字符串
     */
    public String getFoodId() {
        return foodId;
    }

    /**
     * 设置食品唯一标识ID
     * @param foodId 食品ID字符串
     */
    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    /**
     * 获取食品名称
     * @return 食品名称字符串
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * 设置食品名称
     * @param foodName 食品名称字符串
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    /**
     * 获取食品描述信息
     * @return 食品描述字符串
     */
    public String getFoodDescription() {
        return foodDescription;
    }

    /**
     * 设置食品描述信息
     * @param foodDescription 食品描述字符串
     */
    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    /**
     * 获取食品单价
     * @return 食品单价字符串
     */
    public String getFoodPrice() {
        return foodPrice;
    }

    /**
     * 设置食品单价
     * @param foodPrice 食品单价字符串
     */
    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    /**
     * 获取食品购买数量
     * @return 食品数量字符串
     */
    public String getFoodQuantity() {
        return foodQuantity;
    }

    /**
     * 设置食品购买数量
     * @param foodQuantity 食品数量字符串
     */
    public void setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    /**
     * 获取食品图片关联路径/标识
     * @return 食品图片相关字符串（路径或唯一标识）
     */
    public String getFoodImage() {
        return foodImage;
    }

    /**
     * 设置食品图片关联路径/标识
     * @param foodImage 食品图片相关字符串（路径或唯一标识）
     */
    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    /**
     * 无参构造方法
     * 用于创建空的OrderDetailBean对象，后续可通过setter方法为属性赋值
     */
    public OrderDetailBean() {
    }

    /**
     * 全参构造方法
     * 用于一次性为OrderDetailBean的所有属性赋值，创建完整的订单详情对象
     * @param detailsId 订单详情唯一标识ID
     * @param foodId 食品唯一标识ID
     * @param foodName 食品名称
     * @param foodDescription 食品描述信息
     * @param foodPrice 食品单价
     * @param foodQuantity 食品购买数量
     * @param foodImage 食品图片关联路径/标识
     */
    public OrderDetailBean(String detailsId, String foodId, String foodName, String foodDescription, String foodPrice, String foodQuantity, String foodImage) {
        this.detailsId = detailsId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodDescription = foodDescription;
        this.foodPrice = foodPrice;
        this.foodQuantity = foodQuantity;
        this.foodImage = foodImage;
    }

    /**
     * 食品购买数量
     * 记录当前订单详情中该食品的购买份数/数量
     */
    private String foodQuantity;

    /**
     * 食品图片关联路径/标识
     * 用于关联食品的展示图片，可存储图片本地路径或服务器远程路径/唯一标识
     */
    private String foodImage;
}