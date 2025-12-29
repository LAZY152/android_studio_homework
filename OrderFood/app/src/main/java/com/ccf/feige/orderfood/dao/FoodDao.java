package com.ccf.feige.orderfood.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.db.DBUntil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 对食物的增删改查（逻辑删除版）
 * 说明：该类所有数据库操作均采用「逻辑删除」方案，不物理删除数据库中的记录，
 * 而是通过标记字段`s_is_delete`区分数据有效性；同时查询操作会联动商家表（d_business），
 * 过滤掉「菜品已删除」或「商家已注销」的无效数据，保证返回结果的有效性。
 */
public class FoodDao {
    // 全局SQLite数据库实例，从DBUntil工具类中获取已初始化的数据库连接
    public static SQLiteDatabase db= DBUntil.con;

    // 逻辑删除状态常量（与AdminDao保持一致，保证全局状态定义统一）
    private static final int NOT_DELETED = 0; // 未删除/商家未注销：表示数据有效，可参与正常业务查询
    private static final int IS_DELETED = 1; // 已删除/商家已注销：表示数据无效，不参与正常业务查询

    /**
     * 获取所有有效菜品（菜品未删除 + 商家未注销）
     * 业务说明：查询全量有效菜品，不附加其他筛选条件，适用于展示所有可上架/可购买的菜品列表
     * @return 有效菜品列表（List<FoodBean>），无有效数据时返回空列表（非null）
     */
    public static List<FoodBean> getAllFoodList(){
        // 初始化菜品列表，避免返回null导致空指针异常
        List<FoodBean> list=new ArrayList<>();

        // 联表查询SQL：左联商家表d_business，仅查询菜品表d_food的全字段
        // 关联条件：菜品表的商家ID（s_business_id）与商家表的主键ID（s_id）一致
        // 过滤条件：菜品自身未删除（f.s_is_delete=0）+ 菜品所属商家未注销（b.s_is_delete=0）
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_is_delete=? AND b.s_is_delete=?";

        // 绑定查询参数，与SQL中的占位符?一一对应（顺序不可颠倒）
        String[] data = {String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};

        // 执行原生SQL查询，返回游标（Cursor）用于遍历查询结果
        Cursor cursor=db.rawQuery(sql, data);

        try {
            // 循环遍历游标，将每一条有效记录解析为FoodBean对象并加入列表
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            // 最终关闭游标，释放数据库游标资源，避免内存泄漏
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }

        // 返回解析后的菜品列表
        return list;
    }

    /**
     * 根据菜品ID获取有效菜品（菜品未删除 + 商家未注销）
     * 业务说明：根据指定菜品ID精准查询，仅返回该ID对应的有效菜品，适用于菜品详情查询、单个菜品校验
     * 注意：返回值为List<FoodBean>，因菜品ID为主键，列表最多包含1条数据
     * @param account 菜品ID（入参命名account为历史兼容，实际为s_food_id的值）
     * @return 有效菜品列表，无匹配数据/数据无效时返回空列表
     */
    public static List<FoodBean> getAllFoodListByFoodId(String account){
        // 绑定查询参数：菜品ID + 菜品未删除 + 商家未注销（与SQL占位符对应）
        String data[]={account, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};

        // 联表查询SQL：精准匹配菜品ID，同时过滤有效数据
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_food_id=? AND f.s_is_delete=? AND b.s_is_delete=?";

        // 初始化菜品列表
        List<FoodBean> list=new ArrayList<>();

        // 执行查询并获取游标
        Cursor cursor=db.rawQuery(sql,data);

        try {
            // 遍历游标解析数据
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            // 关闭游标释放资源
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }

        return list;
    }

    /**
     * 根据商家ID获取有效菜品（菜品未删除 + 商家未注销）
     * 业务说明：查询指定商家下的所有有效菜品，适用于商家店铺菜品列表展示、商家菜品批量操作前置查询
     * @param account 商家ID（入参命名account为历史兼容，实际为s_business_id的值）
     * @return 该商家下的有效菜品列表，无匹配数据/数据无效时返回空列表
     */
    public static List<FoodBean> getAllFoodListByBusinessId(String account){
        // 绑定查询参数：商家ID + 菜品未删除 + 商家未注销
        String data[]={account, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};

        // 联表查询SQL：精准匹配商家ID，同时过滤有效数据
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_business_id=? AND f.s_is_delete=? AND b.s_is_delete=?";

        // 初始化菜品列表
        List<FoodBean> list=new ArrayList<>();

        // 执行查询并获取游标
        Cursor cursor=db.rawQuery(sql,data);

        try {
            // 遍历游标解析数据
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            // 关闭游标释放资源
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }

        return list;
    }

    /**
     * 根据商家ID和菜品名称模糊查询有效菜品（菜品未删除 + 商家未注销）
     * 业务说明：商家端专属查询，支持按菜品名称关键词模糊搜索，精准定位本商家下的目标菜品
     * @param businessIdZ 商家ID（入参命名businessIdZ为历史兼容，实际为s_business_id的值）
     * @param title 菜品名称关键词（支持任意字符，查询时自动拼接%实现模糊匹配）
     * @return 匹配条件的有效菜品列表，无匹配数据时返回空列表
     */
    public static List<FoodBean> getAllFoodList(String businessIdZ,String title){
        // 拼接模糊查询关键词：% + 输入关键词 + %，实现包含匹配（前后任意字符）
        String titleL="%"+title+"%";

        // 绑定查询参数：商家ID + 模糊匹配关键词 + 菜品未删除 + 商家未注销
        String data[]= {businessIdZ, titleL, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};

        // 联表查询SQL：匹配商家ID + 菜品名称模糊查询，同时过滤有效数据
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_business_id=? AND f.s_food_name like ? AND f.s_is_delete=? AND b.s_is_delete=?";

        // 初始化菜品列表
        List<FoodBean> list=new ArrayList<>();

        // 执行查询并获取游标
        Cursor cursor=db.rawQuery(sql,data);

        try {
            // 遍历游标解析数据
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            // 关闭游标释放资源
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }

        return list;
    }

    /**
     * 根据菜品名称模糊查询有效菜品（菜品未删除 + 商家未注销）
     * 业务说明：用户端专属查询，不限制商家，全网搜索包含关键词的有效菜品，适用于用户菜品搜索
     * @param title 菜品名称关键词（支持任意字符，查询时自动拼接%实现模糊匹配）
     * @return 匹配条件的有效菜品列表，无匹配数据时返回空列表
     */
    public static List<FoodBean> getAllFoodListUser(String title){
        // 拼接模糊查询关键词：% + 输入关键词 + %，实现包含匹配
        String titleL="%"+title+"%";

        // 绑定查询参数：模糊匹配关键词 + 菜品未删除 + 商家未注销
        String data[]= {titleL, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};

        // 联表查询SQL：菜品名称模糊查询，同时过滤有效数据（不限制商家）
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_food_name like ? AND f.s_is_delete=? AND b.s_is_delete=?";

        // 初始化菜品列表
        List<FoodBean> list=new ArrayList<>();

        // 执行查询并获取游标
        Cursor cursor=db.rawQuery(sql,data);

        try {
            // 遍历游标解析数据
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            // 关闭游标释放资源
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }

        return list;
    }

    /**
     * 根据菜品ID获取有效菜品（菜品未删除 + 商家未注销）
     * 业务说明：与getAllFoodListByFoodId功能类似，但返回单个FoodBean对象，更便于直接使用菜品详情
     * @param id 菜品ID（s_food_id的值）
     * @return 有效菜品对象（FoodBean），null表示菜品不存在/已删除/所属商家已注销
     */
    public static FoodBean getAllFoodById(String id){
        // 绑定查询参数：菜品ID + 菜品未删除 + 商家未注销
        String[] data = {id, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};

        // 联表查询SQL：精准匹配菜品ID，同时过滤有效数据
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_food_id=? AND f.s_is_delete=? AND b.s_is_delete=?";

        // 执行查询并获取游标
        Cursor cursor=db.rawQuery(sql, data);

        try {
            // 游标移动到第一条记录（因菜品ID为主键，最多只有一条匹配记录）
            if (cursor.moveToNext()){
                // 解析游标数据为FoodBean并返回
                return parseFoodBeanFromCursor(cursor);
            }
        } finally {
            // 关闭游标释放资源
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }

        // 无匹配有效数据，返回null
        return null;
    }

    /**
     * 获取当前月的销售数量（仅统计有效菜品）
     * 业务说明：统计指定菜品在当前自然月的总销量，仅统计已完成订单（订单状态s_order_sta='3'）中的销量
     * @param foodId 菜品ID（s_food_id的值）
     * @return 当月销售数量（int），无效菜品/无销售记录时返回0
     */
    @SuppressLint("Range")
    public static int getMouSalesNum(String foodId){
        // 前置校验：先判断菜品是否有效（未删除 + 商家未注销），无效则直接返回0
        FoodBean validFood = getAllFoodById(foodId);
        if (validFood == null) {
            return 0; // 无效菜品，销售数量为0
        }

        // 查询SQL：获取当前月所有已完成（s_order_sta='3'）的订单ID（s_order_details_id）
        // 日期筛选：使用strftime函数格式化订单时间，匹配当前年月（%Y-%m：年-月，如2025-12）
        Cursor rs = db.rawQuery("SELECT *   FROM d_orders   " +
                "WHERE s_order_sta='3' and   strftime('%Y-%m', s_order_time) = strftime('%Y-%m', 'now');",null);

        // 初始化订单ID列表，用于存储当前月已完成订单的详情ID
        List<String> list=new ArrayList<>();

        try {
            // 遍历游标，提取订单详情ID并加入列表
            while(rs.moveToNext()){
                String temp= rs.getString(rs.getColumnIndex("s_order_details_id"));
                list.add(temp);
            }
        } finally {
            // 关闭游标释放资源
            if (rs != null) {
                rs.close(); // 关闭游标释放资源
            }
        }

        // 初始化总销量计数器
        int salNum=0;

        // 遍历所有当前月已完成订单，累加每个订单中该菜品的销售数量
        for (String s : list) {
            salNum=salNum+getOrderDetailsByOrderAndFoodId(s,foodId);
        }

        // 返回当月总销量
        return salNum;
    }

    /**
     * 通过订单ID和菜品ID获取商品数量（仅统计有效菜品）
     * 业务说明：查询单个订单详情中指定菜品的购买数量，为销量统计提供原子数据
     * @param orderId 订单详情ID（s_order_details_id的值）
     * @param foodId 菜品ID（s_food_id的值）
     * @return 该订单中该菜品的购买数量（int），无效菜品/无匹配订单详情时返回0
     */
    @SuppressLint("Range")
    public static int getOrderDetailsByOrderAndFoodId(String orderId,String foodId){
        // 前置校验：先判断菜品是否有效，无效则直接返回0
        FoodBean validFood = getAllFoodById(foodId);
        if (validFood == null) {
            return 0;
        }

        // 绑定查询参数：订单详情ID + 菜品ID
        String data[]={orderId,foodId};

        // 查询SQL：精准匹配订单详情ID和菜品ID，获取该菜品的购买数量
        Cursor rs = db.rawQuery("select * from d_order_details where s_details_id=? and s_food_id=?",data);

        try {
            // 遍历游标（最多一条匹配记录），提取购买数量并返回
            while(rs.moveToNext()){
                int tm = rs.getInt(rs.getColumnIndex("s_food_num"));
                return tm;
            }
        } finally {
            // 关闭游标释放资源
            if (rs != null) {
                rs.close(); // 关闭游标释放资源
            }
        }

        // 无匹配记录，返回0
        return 0;
    }

    /**
     * 实现添加商品（默认未删除）
     * 业务说明：新增菜品记录，自动生成唯一菜品ID，默认标记为「未删除」（s_is_delete=0），新增后菜品直接生效
     * @param businessId 商家ID（菜品所属商家，s_business_id的值）
     * @param foodName 菜品名称（s_food_name的值）
     * @param des 菜品描述（s_food_des的值）
     * @param foodPrice 菜品价格（s_food_price的值，建议传入格式化后的字符串）
     * @param img 菜品图片（s_food_img的值，通常为图片路径或Base64编码字符串）
     * @return 1-添加成功，0-添加失败（如参数非法、数据库异常、商家不存在等）
     */
    public static int addFood(String businessId,String foodName,String des,String foodPrice,String img){
        // 生成唯一菜品ID：使用UUID.randomUUID()生成唯一标识，去除中间的「-」符号简化格式
        String id= UUID.randomUUID().toString().replace("-","");

        // 绑定插入参数：菜品ID + 商家ID + 菜品名称 + 菜品描述 + 菜品价格 + 菜品图片 + 未删除标记
        String data[]={id,businessId,foodName,des,foodPrice,img, String.valueOf(NOT_DELETED)};

        try {
            // 执行插入SQL，向d_food表新增一条菜品记录
            db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                            "VALUES (?, ?, ?,?,  ?, ?, ?)", // 新增s_is_delete字段
                    data);

            // 插入成功，返回1
            return 1;
        }catch (Exception e){
            // 捕获数据库异常（如字段不匹配、主键重复等），打印异常堆栈
            e.printStackTrace();

            // 插入失败，返回0
            return 0;
        }
    }

    // ====================== 菜品伪删除：标记s_is_delete=1，不物理删除 ======================
    /**
     * 菜品删除（逻辑删除）：标记s_is_delete=1
     * 业务说明：不物理删除菜品记录，仅将s_is_delete字段更新为1，使其在后续查询中被过滤，实现「软删除」
     * @param foodId 菜品ID（s_food_id的值）
     * @return 1-标记成功，0-标记失败（如菜品ID为空、菜品不存在、数据库异常等）
     */
    public static int delFoodById(String foodId){
        // 前置校验：菜品ID为空或空白字符串，直接返回0
        if (foodId == null || foodId.trim().isEmpty()) {
            return 0;
        }

        // 绑定更新参数：已删除标记（1） + 菜品ID
        String data[]={String.valueOf(IS_DELETED), foodId};

        try {
            // 执行更新SQL：将指定菜品的s_is_delete字段更新为1，实现逻辑删除
            db.execSQL("UPDATE d_food SET s_is_delete=? WHERE s_food_id=?", data);

            // 标记成功，返回1
            return 1;
        }catch (Exception e){
            // 捕获数据库异常，打印异常堆栈
            e.printStackTrace();

            // 标记失败，返回0
            return 0;
        }
    }

    // ====================== 批量标记商家菜品为已删除（商家注销时调用） ======================
    /**
     * 批量标记商家下所有菜品为已删除（逻辑删除）
     * 业务说明：商家注销时调用，批量将该商家下所有菜品的s_is_delete字段更新为1，避免残留无效菜品数据
     * @param businessId 商家ID（s_business_id的值）
     * @return 1-标记成功，0-标记失败（如商家ID为空、无对应菜品、数据库异常等）
     */
    public static int deleteFoodByBusinessId(String businessId) {
        // 前置校验：商家ID为空或空白字符串，直接返回0
        if (businessId == null || businessId.trim().isEmpty()) {
            return 0;
        }

        // 绑定更新参数：已删除标记（1） + 商家ID
        String[] data = {String.valueOf(IS_DELETED), businessId};

        try {
            // 执行批量更新SQL：将指定商家下所有菜品的s_is_delete字段更新为1
            db.execSQL("UPDATE d_food SET s_is_delete=? WHERE s_business_id=?", data);

            // 批量标记成功，返回1
            return 1;
        } catch (Exception e) {
            // 捕获数据库异常，打印异常堆栈
            e.printStackTrace();

            // 批量标记失败，返回0
            return 0;
        }
    }

    /**
     * 实现更改食物（仅更新未删除的菜品）
     * 业务说明：更新菜品的名称、描述、价格、图片信息，仅对「未删除」的菜品生效，避免更新无效数据
     * @param foodId 菜品ID（s_food_id的值，指定要更新的菜品）
     * @param foodName 新的菜品名称（s_food_name的值）
     * @param des 新的菜品描述（s_food_des的值）
     * @param foodPrice 新的菜品价格（s_food_price的值）
     * @param img 新的菜品图片（s_food_img的值）
     * @return 1-更新成功，0-更新失败（如菜品ID无效、菜品已删除、数据库异常等）
     */
    public static int updateFood(String foodId,String foodName,String des,String foodPrice,String img){
        // 绑定更新参数：新菜品名称 + 新描述 + 新价格 + 新图片 + 菜品ID + 未删除标记
        String data[]={foodName,des,foodPrice,img, foodId, String.valueOf(NOT_DELETED)};

        try {
            // 执行更新SQL：仅更新指定菜品ID且未删除的菜品记录
            db.execSQL("update  d_food set s_food_name=?, s_food_des=?,s_food_price=?,s_food_img=? where s_food_id=? and s_is_delete=?" ,
                    data); // 仅更新未删除的菜品

            // 更新成功，返回1
            return 1;
        }catch (Exception e){
            // 捕获数据库异常，打印异常堆栈
            e.printStackTrace();

            // 更新失败，返回0
            return 0;
        }
    }

    /**
     * 解析Cursor为FoodBean（提取重复逻辑，简化代码）
     * 工具方法说明：封装游标数据解析逻辑，避免在多个查询方法中重复编写解析代码，提高代码可维护性
     * @param cursor 数据库查询返回的游标（包含d_food表的完整字段数据）
     * @return 解析后的FoodBean对象，包含菜品的所有核心属性
     */
    @SuppressLint("Range")
    private static FoodBean parseFoodBeanFromCursor(Cursor cursor) {
        // 初始化FoodBean对象，用于存储解析后的菜品数据
        FoodBean foodBean=new FoodBean();

        // 从游标中提取对应字段的值，通过字段名获取列索引（getColumnIndex）
        String foodId=cursor.getString(cursor.getColumnIndex("s_food_id"));
        String businessId=cursor.getString(cursor.getColumnIndex("s_business_id"));
        String foodName=cursor.getString(cursor.getColumnIndex("s_food_name"));
        String foodDes=cursor.getString(cursor.getColumnIndex("s_food_des"));
        String foodPrice=cursor.getString(cursor.getColumnIndex("s_food_price"));
        String foodImg=cursor.getString(cursor.getColumnIndex("s_food_img"));

        // 将提取的字段值设置到FoodBean对象中
        foodBean.setFoodId(foodId);
        foodBean.setFoodDes(foodDes);
        foodBean.setFoodImg(foodImg);
        foodBean.setFoodName(foodName);
        foodBean.setFoodPrice(foodPrice);
        foodBean.setBusinessId(businessId);

        // 返回解析完成的FoodBean对象
        return foodBean;
    }
}