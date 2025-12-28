package com.ccf.feige.orderfood.bean;

import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.OrderDao;

import java.util.List;

public class OrderBean {

    // ====================== 新增：订单状态常量（与DBUntil保持一致，避免魔法值） ======================
    public static final String ORDER_STA_UNHANDLED = "1"; // 1. 未处理订单
    public static final String ORDER_STA_CANCEL = "2"; // 2. 取消订单
    public static final String ORDER_STA_FINISH = "3"; // 3. 完成的订单（未评论）
    public static final String ORDER_STA_FINISH_COMMENTED = "4"; // 4. 订单完成且被评论

    // 原有字段保持不变
    private String orderId;
    private String orderTime;
    private String businessId;
    private String userId;
    private String orderDetailsId;
    private String orderStatus;
    private String orderAddress;
    private String userName;
    private List<OrderDetailBean> orderDetailBeanList;

    // ====================== 新增：辅助方法 - 判断订单是否为“完成且被评论”状态 ======================
    /**
     * 判断当前订单是否是“完成且被评论”状态（状态4）
     * @return 是返回true，否返回false
     */
    public boolean isFinishAndCommented() {
        // 避免空指针：先判断orderStatus不为null，再比对状态值
        return ORDER_STA_FINISH_COMMENTED.equals(this.orderStatus);
    }

    /**
     * 可选新增：判断订单是否为“已完成（未评论）”状态（状态3）
     * @return 是返回true，否返回false
     */
    public boolean isFinishUnCommented() {
        return ORDER_STA_FINISH.equals(this.orderStatus);
    }

    // 原有toString方法保持不变
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

    // 原有Getter/Setter方法保持不变
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDetailsId() {
        return orderDetailsId;
    }

    public void setOrderDetailsId(String orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<OrderDetailBean> getOrderDetailBeanList() {
        return orderDetailBeanList;
    }

    public void setOrderDetailBeanList(List<OrderDetailBean> orderDetailBeanList) {
        this.orderDetailBeanList = orderDetailBeanList;
    }

    // 原有有参构造方法保持不变（兼容原有业务逻辑，无破坏性修改）
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

    // 原有无参构造方法保持不变
    public OrderBean() {
    }
}