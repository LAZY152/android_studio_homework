package com.ccf.feige.orderfood.bean;

import java.io.Serializable;

/**
 * 食品/菜品实体类
 * 对应数据库表：d_food
 * 实现Serializable接口：支持对象序列化（便于网络传输、本地持久化等场景）
 * 功能：封装菜品的所有属性信息，提供属性的访问（getter）和修改（setter）方法，以及对象字符串格式化方法
 */
public class FoodBean implements Serializable {
    // ************************ 逻辑删除状态常量 ************************
    /**
     * 逻辑删除状态：未删除/正常
     * 常量设计目的：
     * 1. 避免魔法值（硬编码数字），提高代码可读性和可维护性
     * 2. 与Dao层（数据访问层）、DBUtil（数据库工具类）保持一致，统一状态标识，减少对接错误
     * 3. 常量为public static final：全局可访问、不可修改、属于类级别的常量
     */
    public static final int NOT_DELETED = 0; // 未删除/正常

    /**
     * 逻辑删除状态：已逻辑删除
     * 逻辑删除说明：并非物理删除数据库中的记录，而是通过修改该状态字段标记为已删除，便于数据恢复和留存业务痕迹
     */
    public static final int IS_DELETED = 1; // 已逻辑删除

    // ************************ 菜品核心属性字段 ************************
    /**
     * 菜品唯一标识ID
     * 对应数据库表d_food的food_id字段
     */
    private String foodId;

    /**
     * 所属商家唯一标识ID
     * 对应数据库表d_food的business_id字段，用于关联商家表，确定菜品归属
     */
    private String businessId;

    /**
     * 菜品名称
     * 对应数据库表d_food的food_name字段
     */
    private String foodName;

    /**
     * 菜品描述/简介
     * 对应数据库表d_food的food_des字段，用于展示菜品的详细说明、食材构成等信息
     */
    private String foodDes;

    /**
     * 菜品价格
     * 对应数据库表d_food的food_price字段，使用String类型适配不同价格格式展示需求
     */
    private String foodPrice;

    /**
     * 菜品图片路径/图片名称
     * 对应数据库表d_food的food_img字段，用于加载和展示菜品图片（本地路径或网络图片地址）
     */
    private String foodImg;

    // ************************ 新增逻辑删除属性字段 ************************
    /**
     * 逻辑删除状态字段
     * 对应数据库表d_food的s_is_delete字段
     * 字段值约束：仅支持0（未删除）和1（已删除），通过setter方法做合法性校验
     */
    private int sIsDelete;

    // ************************ 构造方法 ************************
    /**
     * 无参构造方法（原有）
     * 功能：创建一个空的FoodBean对象，同时初始化逻辑删除状态为默认值
     * 默认值说明：将sIsDelete设为NOT_DELETED（0），与数据库表d_food中s_is_delete字段的默认值保持一致，确保对象属性与数据库记录默认状态同步
     */
    public FoodBean() {
        // 默认未删除，与数据库默认值一致
        this.sIsDelete = NOT_DELETED;
    }

    /**
     * 有参构造方法（原有，兼容旧代码）
     * 功能：通过菜品核心属性创建FoodBean对象，不包含逻辑删除字段，适配旧代码的对象创建逻辑
     * 兼容说明：保留原有构造方法签名，避免修改旧代码导致的编译错误和业务异常
     * 初始化说明：自动将逻辑删除状态设为NOT_DELETED（0），保持与数据库默认值一致，无需调用方手动设置
     * @param foodId 菜品唯一标识ID
     * @param businessId 所属商家唯一标识ID
     * @param foodName 菜品名称
     * @param foodDes 菜品描述/简介
     * @param foodPrice 菜品价格
     * @param foodImg 菜品图片路径/图片名称
     */
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

    /**
     * 有参构造方法（新增，带逻辑删除字段）
     * 功能：创建包含完整字段（含逻辑删除状态）的FoodBean对象，适配Dao层的数据查询和新增操作
     * 设计目的：完整接收数据库表d_food的所有字段数据，实现数据库记录与Java实体对象的完整映射
     * @param foodId 菜品唯一标识ID
     * @param businessId 所属商家唯一标识ID
     * @param foodName 菜品名称
     * @param foodDes 菜品描述/简介
     * @param foodPrice 菜品价格
     * @param foodImg 菜品图片路径/图片名称
     * @param sIsDelete 逻辑删除状态（0=未删除，1=已逻辑删除）
     */
    public FoodBean(String foodId, String businessId, String foodName, String foodDes, String foodPrice, String foodImg, int sIsDelete) {
        this.foodId = foodId;
        this.businessId = businessId;
        this.foodName = foodName;
        this.foodDes = foodDes;
        this.foodPrice = foodPrice;
        this.foodImg = foodImg;
        this.sIsDelete = sIsDelete;
    }

    // ************************ 原有字段getter/setter方法 ************************
    /**
     * 获取菜品唯一标识ID
     * @return 菜品ID（String类型）
     */
    public String getFoodId() {
        return foodId;
    }

    /**
     * 设置菜品唯一标识ID
     * @param foodId 菜品ID（String类型）
     */
    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    /**
     * 获取所属商家唯一标识ID
     * @return 商家ID（String类型）
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * 设置所属商家唯一标识ID
     * @param businessId 商家ID（String类型）
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * 获取菜品名称
     * @return 菜品名称（String类型）
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * 设置菜品名称
     * @param foodName 菜品名称（String类型）
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    /**
     * 获取菜品描述/简介
     * @return 菜品描述（String类型）
     */
    public String getFoodDes() {
        return foodDes;
    }

    /**
     * 设置菜品描述/简介
     * @param foodDes 菜品描述（String类型）
     */
    public void setFoodDes(String foodDes) {
        this.foodDes = foodDes;
    }

    /**
     * 获取菜品价格
     * @return 菜品价格（String类型）
     */
    public String getFoodPrice() {
        return foodPrice;
    }

    /**
     * 设置菜品价格
     * @param foodPrice 菜品价格（String类型）
     */
    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    /**
     * 获取菜品图片路径/图片名称
     * @return 菜品图片相关信息（String类型）
     */
    public String getFoodImg() {
        return foodImg;
    }

    /**
     * 设置菜品图片路径/图片名称
     * @param foodImg 菜品图片相关信息（String类型）
     */
    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }

    // ************************ 新增逻辑删除字段getter/setter方法 ************************
    /**
     * 获取菜品逻辑删除状态
     * 命名风格：与UserBean保持一致，遵循项目内实体类属性访问方法的统一规范
     * @return 逻辑删除状态（0=未删除，1=已逻辑删除）
     */
    public int getsIsDelete() {
        return sIsDelete;
    }

    /**
     * 设置菜品逻辑删除状态
     * 功能亮点：包含合法性校验，避免非法值（非0、非1）存入，防止因非法状态导致的业务逻辑异常和数据库数据脏污
     * 校验规则：仅接受NOT_DELETED（0）和IS_DELETED（1），非法值默认设为NOT_DELETED（0）
     * @param sIsDelete 待设置的逻辑删除状态（0=未删除，1=已逻辑删除）
     */
    public void setsIsDelete(int sIsDelete) {
        // 校验字段值，确保只有0/1两种合法状态，避免非法值导致业务异常
        this.sIsDelete = (sIsDelete == NOT_DELETED || sIsDelete == IS_DELETED) ? sIsDelete : NOT_DELETED;
    }

    // ************************ 重写toString()方法 ************************
    /**
     * 重写Object类的toString()方法
     * 重写目的：
     * 1. 便于开发调试：打印对象时可直接看到所有属性的具体值，无需逐个获取属性
     * 2. 便于日志打印：输出清晰的对象信息，便于问题排查和日志分析
     * 3. 优化展示效果：将逻辑删除状态的数字值转换为文字描述（"未删除"/"已逻辑删除"），更直观易懂
     * @return 格式化后的FoodBean对象字符串，包含所有属性信息
     */
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