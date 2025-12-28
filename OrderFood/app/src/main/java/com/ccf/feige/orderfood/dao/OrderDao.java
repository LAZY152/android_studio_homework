package com.ccf.feige.orderfood.dao;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.db.DBUntil;
import com.ccf.feige.orderfood.until.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单DAO（确保状态4更新成功+查询用户所有订单）
 */
public class OrderDao {
    // 全局数据库连接
    public static SQLiteDatabase db = DBUntil.con;

    // 复用OrderBean中的状态常量
    private static final String ORDER_STA_UNHANDLED = OrderBean.ORDER_STA_UNHANDLED; // "1"
    private static final String ORDER_STA_CANCEL = OrderBean.ORDER_STA_CANCEL; // "2"
    private static final String ORDER_STA_FINISH = OrderBean.ORDER_STA_FINISH; // "3"
    private static final String ORDER_STA_FINISH_COMMENTED = OrderBean.ORDER_STA_FINISH_COMMENTED; // "4"

    /**
     * 更改订单状态
     * @param orderId 订单ID
     * @param newStatus 新状态（使用OrderBean常量）
     * @return 成功返回1，失败返回0
     */
    public static int updateOrderStatus(String orderId, String newStatus) {
        if (orderId == null || orderId.trim().isEmpty() || newStatus == null || newStatus.trim().isEmpty()) {
            return 0;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return 0;
            }
        }
        String sql = "UPDATE d_orders SET s_order_sta = ? WHERE s_order_id = ?";
        try {
            db.execSQL(sql, new String[]{newStatus, orderId});
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 添加订单
     */
    public static int installOrder(String orderId, String time, String businessId, String userId,
                                   String orderDetailID, String sta, String address) {
        if (orderId == null || orderId.trim().isEmpty() || businessId == null || businessId.trim().isEmpty()
                || userId == null || userId.trim().isEmpty() || sta == null || sta.trim().isEmpty()) {
            return 0;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return 0;
            }
        }
        try {
            db.execSQL("INSERT INTO d_orders (s_order_id, s_order_time, s_business_id,s_user_id, " +
                            "s_order_details_id,s_order_sta, s_order_address) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{orderId, time == null ? "" : time, businessId, userId,
                            orderDetailID == null ? "" : orderDetailID, sta, address == null ? "" : address});
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据订单详情ID查询订单详情
     */
    public static List<OrderDetailBean> getAllOrderDetail(String id) {
        List<OrderDetailBean> orderDetailBeanList = new ArrayList<>();
        if (id == null || id.trim().isEmpty()) {
            return orderDetailBeanList;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderDetailBeanList;
            }
        }
        Cursor rs = null;
        String sql = "select * from d_order_details where s_details_id=?";
        try {
            rs = db.rawQuery(sql, new String[]{id});
            while (rs != null && rs.moveToNext()) {
                String detailId = Tools.getResultString(rs, "s_details_id");
                String foodId = Tools.getResultString(rs, "s_food_id");
                String foodName = Tools.getResultString(rs, "s_food_name");
                String foodDes = Tools.getResultString(rs, "s_food_des");
                String foodPrice = Tools.getResultString(rs, "s_food_price");
                String foodNum = Tools.getResultString(rs, "s_food_num");
                String foodImg = Tools.getResultString(rs, "s_food_img");

                OrderDetailBean orderDetailBean = new OrderDetailBean(detailId, foodId, foodName,
                        foodDes, foodPrice, foodNum, foodImg);
                orderDetailBeanList.add(orderDetailBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        return orderDetailBeanList;
    }

    /**
     * 查询所有订单
     */
    public static List<OrderBean> getAllOrders() {
        List<OrderBean> orderBeanList = new ArrayList<>();
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null;
        String sql = "select * from d_orders ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time ) desc";
        try {
            rs = db.rawQuery(sql, null);
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        return orderBeanList;
    }

    /**
     * 按商家ID+订单状态查询订单
     */
    public static List<OrderBean> getAllOrdersBySta(String account, String staZ) {
        List<OrderBean> orderBeanList = new ArrayList<>();
        if (account == null || account.trim().isEmpty() || staZ == null || staZ.trim().isEmpty()) {
            return orderBeanList;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null;
        String sql = "select * from d_orders where s_business_id=? and s_order_sta=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account, staZ};
        try {
            rs = db.rawQuery(sql, data);
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        return orderBeanList;
    }

    /**
     * 按用户ID+订单状态查询订单
     */
    public static List<OrderBean> getAllOrdersByStaAndUser(String account, String staZ) {
        List<OrderBean> orderBeanList = new ArrayList<>();
        if (account == null || account.trim().isEmpty() || staZ == null || staZ.trim().isEmpty()) {
            return orderBeanList;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null;
        String sql = "select * from d_orders where s_user_id=? and s_order_sta=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account, staZ};
        try {
            rs = db.rawQuery(sql, data);
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        return orderBeanList;
    }

    /**
     * 按用户ID查询该用户所有订单（核心：不筛选状态，返回全部订单）
     * @param account 用户ID
     * @param staZ 保留参数（兼容原有调用，无实际作用）
     * @return 用户所有订单列表
     */
    public static List<OrderBean> getAllOrdersByStaAndUserFinish(String account, String staZ) {
        List<OrderBean> orderBeanList = new ArrayList<>();
        if (account == null || account.trim().isEmpty()) {
            return orderBeanList;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null;
        // 核心：仅按用户ID查询，不筛选订单状态，返回所有订单
        String sql = "select * from d_orders where s_user_id=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account};
        try {
            rs = db.rawQuery(sql, data);
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        return orderBeanList;
    }

    /**
     * 按商家ID查询已完成订单
     */
    public static List<OrderBean> getAllOrdersFinish(String account) {
        List<OrderBean> orderBeanList = new ArrayList<>();
        if (account == null || account.trim().isEmpty()) {
            return orderBeanList;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null;
        String sql = "select * from d_orders where s_business_id=? and s_order_sta!=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account, ORDER_STA_UNHANDLED};
        try {
            rs = db.rawQuery(sql, data);
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        return orderBeanList;
    }

    /**
     * 保存订单详情（修正拼写错误）
     */
    public static void saveOrderDetail(OrderDetailBean orderDetailBean) {
        if (orderDetailBean == null) {
            return;
        }
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return;
            }
        }
        try {
            db.execSQL("INSERT INTO d_order_details (s_details_id,s_food_id, s_food_name,s_food_des, " +
                            "s_food_price, s_food_num,s_food_img) VALUES (?, ?,?, ?, ?, ?, ?)",
                    new Object[]{orderDetailBean.getDetailsId(), orderDetailBean.getFoodId(),
                            orderDetailBean.getFoodName(), orderDetailBean.getFoodDescription(),
                            orderDetailBean.getFoodPrice(), orderDetailBean.getFoodQuantity(),
                            orderDetailBean.getFoodImage()});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将订单状态更新为4（完成且被评论）：确保更新成功
     * @param orderId 订单ID
     * @return 成功返回1，失败返回0
     */
    public static int updateOrderStatusToCommented(String orderId) {
        // 复用updateOrderStatus方法，确保逻辑一致
        return updateOrderStatus(orderId, ORDER_STA_FINISH_COMMENTED);
    }

    /**
     * 从Cursor创建OrderBean（抽取重复代码）
     */
    private static OrderBean createOrderBeanFromCursor(Cursor rs) {
        String orderId = Tools.getResultString(rs, "s_order_id");
        String time = Tools.getResultString(rs, "s_order_time");
        String businessId = Tools.getResultString(rs, "s_business_id");
        String userId = Tools.getResultString(rs, "s_user_id");
        String detailsId = Tools.getResultString(rs, "s_order_details_id");
        String sta = Tools.getResultString(rs, "s_order_sta");
        String address = Tools.getResultString(rs, "s_order_address");
        return new OrderBean(orderId, time, businessId, userId, detailsId, sta, address);
    }
}