package com.ccf.feige.orderfood.bean;

import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.OrderDao;

import java.util.List;

/**
 * 订单实体类（OrderBean）
 * 作用：封装订单相关的所有数据信息，对应订单业务表的一条记录，是订单业务逻辑中数据传递和存储的核心载体
 * 包含：订单基础字段、订单状态常量、订单状态判断辅助方法、构造方法、Getter/Setter方法、toString方法
 */
public class OrderBean {

    // ====================== 新增：订单状态常量（与DBUntil保持一致，避免魔法值） ======================
    /**
     * 订单状态常量：未处理订单
     * 状态值："1"
     * 说明：用户提交订单后，商家尚未进行任何处理的初始状态
     */
    public static final String ORDER_STA_UNHANDLED = "1";

    /**
     * 订单状态常量：取消订单
     * 状态值："2"
     * 说明：用户主动取消或系统/商家取消的订单，该状态订单不再进行后续流转
     */
    public static final String ORDER_STA_CANCEL = "2";

    /**
     * 订单状态常量：完成的订单（未评论）
     * 状态值："3"
     * 说明：用户已收到商品/服务，订单交易流程完成，但尚未对该订单进行评价
     */
    public static final String ORDER_STA_FINISH = "3";

    /**
     * 订单状态常量：订单完成且被评论
     * 状态值："4"
     * 说明：订单交易完成，且用户已完成对该订单的评价，订单生命周期结束
     */
    public static final String ORDER_STA_FINISH_COMMENTED = "4";

    // 原有字段保持不变
    /**
     * 订单唯一标识ID
     * 对应数据库订单表的主键字段，用于唯一定位一条订单记录
     */
    private String orderId;

    /**
     * 订单创建时间
     * 格式：一般为字符串格式的时间戳或格式化日期（如yyyy-MM-dd HH:mm:ss），记录用户提交订单的时间
     */
    private String orderTime;

    /**
     * 商家唯一标识ID
     * 关联商家表的主键，用于定位该订单所属的商家
     */
    private String businessId;

    /**
     * 用户唯一标识ID
     * 关联用户表的主键，用于定位该订单的下单用户
     */
    private String userId;

    /**
     * 订单详情关联ID
     * 关联订单详情表的主键，用于批量查询该订单对应的所有商品/服务明细
     */
    private String orderDetailsId;

    /**
     * 订单当前状态
     * 取值范围：对应本类中定义的订单状态常量（ORDER_STA_*系列）
     * 说明：记录订单当前所处的生命周期节点，用于控制订单业务流转
     */
    private String orderStatus;

    /**
     * 订单收货/服务地址
     * 记录用户下单时填写的收货地址或服务提供地址，用于商家履约
     */
    private String orderAddress;

    /**
     * 下单用户昵称
     * 来源于用户表的用户昵称字段，用于前端展示或订单详情追溯，无需关联用户表即可获取用户展示名称
     */
    private String userName;

    /**
     * 订单明细列表
     * 封装该订单对应的所有商品/服务明细数据，每个元素为一个OrderDetailBean对象
     * 说明：一对多关联（一个订单对应多条明细），通过orderDetailsId关联查询获取
     */
    private List<OrderDetailBean> orderDetailBeanList;

    // ====================== 新增：辅助方法 - 判断订单是否为“完成且被评论”状态 ======================
    /**
     * 判断当前订单是否是“完成且被评论”状态（状态4）
     * <p>
     * 核心逻辑：使用常量ORDER_STA_FINISH_COMMENTED作为调用方，调用String.equals()方法
     * 优势：避免orderStatus为null时抛出空指针异常（NullPointerException）
     * @return 布尔值：true表示当前订单是“完成且被评论”状态，false表示不是该状态
     */
    public boolean isFinishAndCommented() {
        // 避免空指针：先判断orderStatus不为null，再比对状态值
        return ORDER_STA_FINISH_COMMENTED.equals(this.orderStatus);
    }

    /**
     * 可选新增：判断订单是否为“已完成（未评论）”状态（状态3）
     * <p>
     * 核心逻辑：使用常量ORDER_STA_FINISH作为调用方，调用String.equals()方法
     * 优势：避免orderStatus为null时抛出空指针异常（NullPointerException）
     * @return 布尔值：true表示当前订单是“已完成（未评论）”状态，false表示不是该状态
     */
    public boolean isFinishUnCommented() {
        return ORDER_STA_FINISH.equals(this.orderStatus);
    }

    /**
     * 重写toString()方法
     * 作用：返回订单核心字段的字符串拼接结果，方便日志打印、调试排查、控制台输出订单关键信息
     * 说明：仅包含订单基础核心字段，不包含订单明细列表（避免明细过多导致字符串过长）
     * @return 订单核心字段的格式化字符串
     */
    @Override
    public String toString() {
        return "OrderBean{" +
                "orderId='" + orderId + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", businessId='" + businessId + '\'' +
                ", userId='" + userId + '\'' +
                ", orderDetailsId='" + orderDetailsId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderAddress='" + orderAddress + '\'' +
                '}';
    }

    /**
     * 获取订单唯一标识ID
     * @return 订单ID字符串（orderId）
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置订单唯一标识ID
     * @param orderId 订单ID字符串
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取订单创建时间
     * @return 订单创建时间字符串（orderTime）
     */
    public String getOrderTime() {
        return orderTime;
    }

    /**
     * 设置订单创建时间
     * @param orderTime 订单创建时间字符串
     */
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    /**
     * 获取订单所属商家ID
     * @return 商家ID字符串（businessId）
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * 设置订单所属商家ID
     * @param businessId 商家ID字符串
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * 获取下单用户ID
     * @return 用户ID字符串（userId）
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置下单用户ID
     * @param userId 用户ID字符串
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取订单详情关联ID
     * @return 订单详情ID字符串（orderDetailsId）
     */
    public String getOrderDetailsId() {
        return orderDetailsId;
    }

    /**
     * 设置订单详情关联ID
     * @param orderDetailsId 订单详情ID字符串
     */
    public void setOrderDetailsId(String orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    /**
     * 获取订单当前状态
     * @return 订单状态字符串（orderStatus，对应本类订单状态常量）
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * 设置订单当前状态
     * @param orderStatus 订单状态字符串（需匹配本类订单状态常量）
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * 获取订单收货/服务地址
     * @return 订单地址字符串（orderAddress）
     */
    public String getOrderAddress() {
        return orderAddress;
    }

    /**
     * 设置订单收货/服务地址
     * @param orderAddress 订单地址字符串
     */
    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    /**
     * 获取下单用户昵称
     * @return 用户名昵称字符串（userName）
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置下单用户昵称
     * @param userName 用户名昵称字符串
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取订单明细列表
     * @return 订单明细List集合（List<OrderDetailBean>），包含该订单所有商品/服务明细
     */
    public List<OrderDetailBean> getOrderDetailBeanList() {
        return orderDetailBeanList;
    }

    /**
     * 设置订单明细列表
     * @param orderDetailBeanList 订单明细List集合（List<OrderDetailBean>）
     */
    public void setOrderDetailBeanList(List<OrderDetailBean> orderDetailBeanList) {
        this.orderDetailBeanList = orderDetailBeanList;
    }

    /**
     * 有参构造方法（兼容原有业务逻辑，无破坏性修改）
     * 作用：通过订单核心字段初始化OrderBean对象，同时自动关联查询订单明细和用户昵称
     * 初始化流程：
     * 1. 给订单基础字段赋值
     * 2. 通过orderDetailsId调用OrderDao查询订单明细列表并赋值
     * 3. 通过userId调用AdminDao查询用户信息，非空时提取用户昵称并赋值
     * @param orderId 订单唯一标识ID
     * @param orderTime 订单创建时间
     * @param businessId 商家唯一标识ID
     * @param userId 用户唯一标识ID
     * @param orderDetailsId 订单详情关联ID
     * @param orderStatus 订单当前状态
     * @param orderAddress 订单收货/服务地址
     */
    public OrderBean(String orderId, String orderTime, String businessId, String userId, String orderDetailsId, String orderStatus, String orderAddress) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.businessId = businessId;
        this.userId = userId;
        this.orderDetailsId = orderDetailsId;
        this.orderStatus = orderStatus;
        this.orderAddress = orderAddress;
        this.orderDetailBeanList = OrderDao.getAllOrderDetail(orderDetailsId);
        // 避免空指针：先判断AdminDao返回结果不为null
        if (AdminDao.getCommonUser(userId) != null) {
            String uname = AdminDao.getCommonUser(userId).getsName();//用户账号昵称
            this.userName = uname;
        }
    }

    /**
     * 无参构造方法
     * 作用：创建空的OrderBean对象，用于后续通过Setter方法赋值（兼容反射、框架实例化等场景）
     */
    public OrderBean() {
    }
}