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
 * 订单DAO（数据访问对象）类
 * 核心功能：负责订单表（d_orders）和订单详情表（d_order_details）的所有数据库操作
 * 关键要求：确保订单状态4（已完成且已评论）更新成功，支持查询指定用户的所有订单（不筛选状态）
 * 依赖说明：依赖DBUntil获取数据库连接、Tools工具类处理游标结果、OrderBean/OrderDetailBean封装数据
 */
public class OrderDao {
    // 全局数据库连接对象，从DBUntil工具类中获取，供所有静态方法复用
    public static SQLiteDatabase db = DBUntil.con;

    // 复用OrderBean中的订单状态常量，避免硬编码，提高可维护性
    private static final String ORDER_STA_UNHANDLED = OrderBean.ORDER_STA_UNHANDLED; // 状态1：未处理（待接单）
    private static final String ORDER_STA_CANCEL = OrderBean.ORDER_STA_CANCEL; // 状态2：已取消
    private static final String ORDER_STA_FINISH = OrderBean.ORDER_STA_FINISH; // 状态3：已完成（未评论）
    private static final String ORDER_STA_FINISH_COMMENTED = OrderBean.ORDER_STA_FINISH_COMMENTED; // 状态4：已完成且已评论

    /**
     * 通用订单状态更新方法
     * @param orderId 订单ID（唯一标识，不能为空）
     * @param newStatus 新订单状态（需使用OrderBean中定义的状态常量，不能为空）
     * @return 操作结果：1表示更新成功，0表示更新失败（参数非法/数据库异常/连接失效）
     */
    public static int updateOrderStatus(String orderId, String newStatus) {
        // 第一步：参数合法性校验，避免空指针和无效更新
        if (orderId == null || orderId.trim().isEmpty() || newStatus == null || newStatus.trim().isEmpty()) {
            return 0;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con; // 从工具类重新获取数据库连接
            if (db == null) { // 连接获取失败，直接返回失败
                return 0;
            }
        }
        // 第三步：构建更新SQL语句（参数化查询，防止SQL注入）
        String sql = "UPDATE d_orders SET s_order_sta = ? WHERE s_order_id = ?";
        try {
            // 执行SQL更新，传入状态和订单ID参数
            db.execSQL(sql, new String[]{newStatus, orderId});
            return 1; // 更新成功，返回1
        } catch (SQLException e) {
            // 捕获数据库异常（如表不存在、字段错误等），打印异常堆栈便于调试
            e.printStackTrace();
            return 0; // 更新失败，返回0
        }
    }

    /**
     * 新增订单到订单表（d_orders）
     * @param orderId 订单ID（唯一标识，必填）
     * @param time 订单创建时间（可选，为空则存入空字符串）
     * @param businessId 商家ID（必填，关联商家表）
     * @param userId 用户ID（必填，关联用户表）
     * @param orderDetailID 订单详情ID（可选，关联订单详情表，为空则存入空字符串）
     * @param sta 订单初始状态（必填，需使用OrderBean状态常量）
     * @param address 订单收货地址（可选，为空则存入空字符串）
     * @return 操作结果：1表示新增成功，0表示新增失败（参数非法/数据库异常/连接失效）
     */
    public static int installOrder(String orderId, String time, String businessId, String userId,
                                   String orderDetailID, String sta, String address) {
        // 第一步：核心参数合法性校验，订单ID、商家ID、用户ID、状态为必填项
        if (orderId == null || orderId.trim().isEmpty() || businessId == null || businessId.trim().isEmpty()
                || userId == null || userId.trim().isEmpty() || sta == null || sta.trim().isEmpty()) {
            return 0;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return 0;
            }
        }
        try {
            // 第三步：构建插入SQL语句，填充订单表所有字段，可选参数做非空处理
            db.execSQL("INSERT INTO d_orders (s_order_id, s_order_time, s_business_id,s_user_id, " +
                            "s_order_details_id,s_order_sta, s_order_address) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{orderId, time == null ? "" : time, businessId, userId,
                            orderDetailID == null ? "" : orderDetailID, sta, address == null ? "" : address});
            return 1; // 新增成功，返回1
        } catch (SQLException e) {
            // 捕获数据库异常（如主键冲突、字段类型不匹配等），打印异常堆栈便于调试
            e.printStackTrace();
            return 0; // 新增失败，返回0
        }
    }

    /**
     * 根据订单详情ID查询对应的订单详情列表（一个订单详情ID对应多条菜品记录）
     * @param id 订单详情ID（唯一标识，不能为空）
     * @return 订单详情列表：包含该详情ID下的所有菜品信息，无数据/参数非法/连接失效时返回空列表
     */
    public static List<OrderDetailBean> getAllOrderDetail(String id) {
        // 初始化返回结果列表，避免返回null引发空指针异常
        List<OrderDetailBean> orderDetailBeanList = new ArrayList<>();
        // 第一步：参数合法性校验
        if (id == null || id.trim().isEmpty()) {
            return orderDetailBeanList;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderDetailBeanList;
            }
        }
        Cursor rs = null; // 声明数据库游标，用于遍历查询结果
        String sql = "select * from d_order_details where s_details_id=?"; // 构建查询SQL（参数化查询）
        try {
            // 执行查询，传入订单详情ID参数，获取游标结果集
            rs = db.rawQuery(sql, new String[]{id});
            // 遍历游标结果集，逐行封装订单详情对象
            while (rs != null && rs.moveToNext()) {
                // 使用Tools工具类从游标中安全获取字符串字段，避免字段不存在/类型不匹配引发异常
                String detailId = Tools.getResultString(rs, "s_details_id");
                String foodId = Tools.getResultString(rs, "s_food_id");
                String foodName = Tools.getResultString(rs, "s_food_name");
                String foodDes = Tools.getResultString(rs, "s_food_des");
                String foodPrice = Tools.getResultString(rs, "s_food_price");
                String foodNum = Tools.getResultString(rs, "s_food_num");
                String foodImg = Tools.getResultString(rs, "s_food_img");

                // 封装OrderDetailBean对象，添加到结果列表
                OrderDetailBean orderDetailBean = new OrderDetailBean(detailId, foodId, foodName,
                        foodDes, foodPrice, foodNum, foodImg);
                orderDetailBeanList.add(orderDetailBean);
            }
        } catch (SQLException e) {
            // 捕获数据库查询异常，打印异常堆栈便于调试
            e.printStackTrace();
        } finally {
            // 最终步骤：关闭游标，释放数据库资源，避免内存泄漏
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        // 返回封装后的订单详情列表
        return orderDetailBeanList;
    }

    /**
     * 查询订单表中的所有订单（不区分用户/商家/状态）
     * @return 所有订单列表：按订单创建时间倒序排列，无数据/连接失效时返回空列表
     */
    public static List<OrderBean> getAllOrders() {
        // 初始化返回结果列表，避免返回null引发空指针异常
        List<OrderBean> orderBeanList = new ArrayList<>();
        // 第一步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null; // 声明数据库游标，用于遍历查询结果
        // 构建查询SQL：查询所有订单，按时间字符串解析后倒序排列（最新订单在前）
        String sql = "select * from d_orders ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time ) desc";
        try {
            // 执行查询，无查询参数，获取游标结果集
            rs = db.rawQuery(sql, null);
            // 遍历游标结果集，逐行封装订单对象
            while (rs != null && rs.moveToNext()) {
                // 调用抽取的公共方法，从游标中封装OrderBean对象，减少代码冗余
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            // 捕获数据库查询异常，打印异常堆栈便于调试
            e.printStackTrace();
        } finally {
            // 最终步骤：关闭游标，释放数据库资源，避免内存泄漏
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        // 返回封装后的所有订单列表
        return orderBeanList;
    }

    /**
     * 按商家ID+订单状态查询订单（商家视角，筛选指定状态的订单）
     * @param account 商家ID（必填，关联商家表）
     * @param staZ 订单状态（必填，需使用OrderBean状态常量）
     * @return 符合条件的订单列表：按创建时间倒序排列，无数据/参数非法/连接失效时返回空列表
     */
    public static List<OrderBean> getAllOrdersBySta(String account, String staZ) {
        // 初始化返回结果列表，避免返回null引发空指针异常
        List<OrderBean> orderBeanList = new ArrayList<>();
        // 第一步：参数合法性校验，商家ID和订单状态为必填项
        if (account == null || account.trim().isEmpty() || staZ == null || staZ.trim().isEmpty()) {
            return orderBeanList;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null; // 声明数据库游标，用于遍历查询结果
        // 构建查询SQL：按商家ID和状态筛选，按时间倒序排列
        String sql = "select * from d_orders where s_business_id=? and s_order_sta=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account, staZ}; // 封装查询参数
        try {
            // 执行参数化查询，获取游标结果集
            rs = db.rawQuery(sql, data);
            // 遍历游标结果集，逐行封装订单对象
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            // 捕获数据库查询异常，打印异常堆栈便于调试
            e.printStackTrace();
        } finally {
            // 最终步骤：关闭游标，释放数据库资源，避免内存泄漏
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        // 返回封装后的商家指定状态订单列表
        return orderBeanList;
    }

    /**
     * 按用户ID+订单状态查询订单（用户视角，筛选指定状态的订单）
     * @param account 用户ID（必填，关联用户表）
     * @param staZ 订单状态（必填，需使用OrderBean状态常量）
     * @return 符合条件的订单列表：按创建时间倒序排列，无数据/参数非法/连接失效时返回空列表
     */
    public static List<OrderBean> getAllOrdersByStaAndUser(String account, String staZ) {
        // 初始化返回结果列表，避免返回null引发空指针异常
        List<OrderBean> orderBeanList = new ArrayList<>();
        // 第一步：参数合法性校验，用户ID和订单状态为必填项
        if (account == null || account.trim().isEmpty() || staZ == null || staZ.trim().isEmpty()) {
            return orderBeanList;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null; // 声明数据库游标，用于遍历查询结果
        // 构建查询SQL：按用户ID和状态筛选，按时间倒序排列
        String sql = "select * from d_orders where s_user_id=? and s_order_sta=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account, staZ}; // 封装查询参数
        try {
            // 执行参数化查询，获取游标结果集
            rs = db.rawQuery(sql, data);
            // 遍历游标结果集，逐行封装订单对象
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            // 捕获数据库查询异常，打印异常堆栈便于调试
            e.printStackTrace();
        } finally {
            // 最终步骤：关闭游标，释放数据库资源，避免内存泄漏
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        // 返回封装后的用户指定状态订单列表
        return orderBeanList;
    }

    /**
     * 按用户ID查询该用户所有订单（核心功能：不筛选订单状态，返回用户全部订单）
     * @param account 用户ID（必填，关联用户表）
     * @param staZ 保留参数（兼容原有调用逻辑，无实际筛选作用，可传入任意值）
     * @return 用户所有订单列表：按创建时间倒序排列，无数据/参数非法/连接失效时返回空列表
     */
    public static List<OrderBean> getAllOrdersByStaAndUserFinish(String account, String staZ) {
        // 初始化返回结果列表，避免返回null引发空指针异常
        List<OrderBean> orderBeanList = new ArrayList<>();
        // 第一步：参数合法性校验，仅校验用户ID（保留参数无需校验）
        if (account == null || account.trim().isEmpty()) {
            return orderBeanList;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null; // 声明数据库游标，用于遍历查询结果
        // 核心SQL：仅按用户ID筛选，不添加状态条件，返回用户全部订单，按时间倒序排列
        String sql = "select * from d_orders where s_user_id=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account}; // 封装查询参数（仅用户ID）
        try {
            // 执行参数化查询，获取游标结果集
            rs = db.rawQuery(sql, data);
            // 遍历游标结果集，逐行封装订单对象
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            // 捕获数据库查询异常，打印异常堆栈便于调试
            e.printStackTrace();
        } finally {
            // 最终步骤：关闭游标，释放数据库资源，避免内存泄漏
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        // 返回封装后的用户全部订单列表
        return orderBeanList;
    }

    /**
     * 按商家ID查询已完成相关订单（商家视角，排除未处理订单）
     * @param account 商家ID（必填，关联商家表）
     * @return 符合条件的订单列表：排除状态1（未处理），按创建时间倒序排列，无数据/参数非法/连接失效时返回空列表
     */
    public static List<OrderBean> getAllOrdersFinish(String account) {
        // 初始化返回结果列表，避免返回null引发空指针异常
        List<OrderBean> orderBeanList = new ArrayList<>();
        // 第一步：参数合法性校验，商家ID为必填项
        if (account == null || account.trim().isEmpty()) {
            return orderBeanList;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return orderBeanList;
            }
        }
        Cursor rs = null; // 声明数据库游标，用于遍历查询结果
        // 构建查询SQL：按商家ID筛选，排除未处理状态（1），按时间倒序排列
        String sql = "select * from d_orders where s_business_id=? and s_order_sta!=? " +
                "ORDER BY strftime('%Y-%m-%d %H:%M:%S', s_order_time )  DESC ";
        String data[] = {account, ORDER_STA_UNHANDLED}; // 封装查询参数
        try {
            // 执行参数化查询，获取游标结果集
            rs = db.rawQuery(sql, data);
            // 遍历游标结果集，逐行封装订单对象
            while (rs != null && rs.moveToNext()) {
                OrderBean orderBean = createOrderBeanFromCursor(rs);
                orderBeanList.add(orderBean);
            }
        } catch (SQLException e) {
            // 捕获数据库查询异常，打印异常堆栈便于调试
            e.printStackTrace();
        } finally {
            // 最终步骤：关闭游标，释放数据库资源，避免内存泄漏
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }
        // 返回封装后的商家非未处理订单列表
        return orderBeanList;
    }

    /**
     * 保存单个订单详情（菜品记录）到订单详情表（d_order_details）
     * @param orderDetailBean 订单详情对象（封装了单道菜品的信息，不能为空）
     */
    public static void saveOrderDetail(OrderDetailBean orderDetailBean) {
        // 第一步：参数合法性校验，避免空对象操作
        if (orderDetailBean == null) {
            return;
        }
        // 第二步：检查并重建数据库连接，确保连接有效
        if (db == null || !db.isOpen()) {
            db = DBUntil.con;
            if (db == null) {
                return;
            }
        }
        try {
            // 第三步：构建插入SQL语句，封装菜品所有信息并执行插入
            db.execSQL("INSERT INTO d_order_details (s_details_id,s_food_id, s_food_name,s_food_des, " +
                            "s_food_price, s_food_num,s_food_img) VALUES (?, ?,?, ?, ?, ?, ?)",
                    new Object[]{orderDetailBean.getDetailsId(), orderDetailBean.getFoodId(),
                            orderDetailBean.getFoodName(), orderDetailBean.getFoodDescription(),
                            orderDetailBean.getFoodPrice(), orderDetailBean.getFoodQuantity(),
                            orderDetailBean.getFoodImage()});
        } catch (SQLException e) {
            // 捕获数据库插入异常（如主键冲突、字段不匹配等），打印异常堆栈便于调试
            e.printStackTrace();
        }
    }

    /**
     * 专用方法：将订单状态更新为4（已完成且已评论），确保更新逻辑一致性
     * 核心：复用通用updateOrderStatus方法，避免重复编写更新逻辑，保障更新成功率
     * @param orderId 订单ID（唯一标识，不能为空）
     * @return 操作结果：1表示更新成功，0表示更新失败（参数非法/数据库异常/连接失效）
     */
    public static int updateOrderStatusToCommented(String orderId) {
        // 复用通用状态更新方法，传入状态4常量，确保逻辑统一、更新可靠
        return updateOrderStatus(orderId, ORDER_STA_FINISH_COMMENTED);
    }

    /**
     * 私有公共方法：从Cursor游标中提取字段，封装为OrderBean对象
     * 作用：抽取重复的封装逻辑，减少代码冗余，提高代码可维护性，统一字段提取规则
     * @param rs 数据库查询游标（已指向有效数据行，非空）
     * @return 封装完成的OrderBean对象（包含订单的所有字段信息）
     */
    private static OrderBean createOrderBeanFromCursor(Cursor rs) {
        // 使用Tools工具类从游标中安全获取各字段值，避免字段异常引发错误
        String orderId = Tools.getResultString(rs, "s_order_id");
        String time = Tools.getResultString(rs, "s_order_time");
        String businessId = Tools.getResultString(rs, "s_business_id");
        String userId = Tools.getResultString(rs, "s_user_id");
        String detailsId = Tools.getResultString(rs, "s_order_details_id");
        String sta = Tools.getResultString(rs, "s_order_sta");
        String address = Tools.getResultString(rs, "s_order_address");
        // 实例化OrderBean并返回，封装所有订单字段
        return new OrderBean(orderId, time, businessId, userId, detailsId, sta, address);
    }
}