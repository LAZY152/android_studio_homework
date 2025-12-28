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
 */
public class FoodDao {
    public static SQLiteDatabase db= DBUntil.con;
    // 逻辑删除状态常量（与AdminDao保持一致）
    private static final int NOT_DELETED = 0; // 未删除/商家未注销
    private static final int IS_DELETED = 1; // 已删除/商家已注销

    /**
     * 获取所有有效菜品（菜品未删除 + 商家未注销）
     * @return 有效菜品列表
     */
    public static List<FoodBean> getAllFoodList(){
        List<FoodBean> list=new ArrayList<>();
        // 联表查询：过滤菜品自身未删除 + 商家未注销
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_is_delete=? AND b.s_is_delete=?";
        String[] data = {String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};
        Cursor cursor=db.rawQuery(sql, data);
        try {
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }
        return list;
    }

    /**
     * 根据菜品ID获取有效菜品（菜品未删除 + 商家未注销）
     * @param account 菜品ID
     * @return 有效菜品列表
     */
    public static List<FoodBean> getAllFoodListByFoodId(String account){
        String data[]={account, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};
        // 联表查询：过滤菜品ID + 菜品未删除 + 商家未注销
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_food_id=? AND f.s_is_delete=? AND b.s_is_delete=?";
        List<FoodBean> list=new ArrayList<>();
        Cursor cursor=db.rawQuery(sql,data);
        try {
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }
        return list;
    }

    /**
     * 根据商家ID获取有效菜品（菜品未删除 + 商家未注销）
     * @param account 商家ID
     * @return 有效菜品列表
     */
    public static List<FoodBean> getAllFoodListByBusinessId(String account){
        String data[]={account, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};
        // 联表查询：过滤商家ID + 菜品未删除 + 商家未注销
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_business_id=? AND f.s_is_delete=? AND b.s_is_delete=?";
        List<FoodBean> list=new ArrayList<>();
        Cursor cursor=db.rawQuery(sql,data);
        try {
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }
        return list;
    }

    /**
     * 根据商家ID和菜品名称模糊查询有效菜品（菜品未删除 + 商家未注销）
     * @param businessIdZ 商家ID
     * @param title 菜品名称关键词
     * @return 有效菜品列表
     */
    public static List<FoodBean> getAllFoodList(String businessIdZ,String title){
        String titleL="%"+title+"%";
        String data[]= {businessIdZ, titleL, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};
        // 联表查询：过滤商家ID + 菜品名称模糊匹配 + 菜品未删除 + 商家未注销
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_business_id=? AND f.s_food_name like ? AND f.s_is_delete=? AND b.s_is_delete=?";
        List<FoodBean> list=new ArrayList<>();
        Cursor cursor=db.rawQuery(sql,data);
        try {
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }
        return list;
    }

    /**
     * 根据菜品名称模糊查询有效菜品（菜品未删除 + 商家未注销）
     * @param title 菜品名称关键词
     * @return 有效菜品列表
     */
    public static List<FoodBean> getAllFoodListUser(String title){
        String titleL="%"+title+"%";
        String data[]= {titleL, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};
        // 联表查询：过滤菜品名称模糊匹配 + 菜品未删除 + 商家未注销
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_food_name like ? AND f.s_is_delete=? AND b.s_is_delete=?";
        List<FoodBean> list=new ArrayList<>();
        Cursor cursor=db.rawQuery(sql,data);
        try {
            while (cursor.moveToNext()){
                FoodBean foodBean = parseFoodBeanFromCursor(cursor);
                list.add(foodBean);
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }
        return list;
    }

    /**
     * 根据菜品ID获取有效菜品（菜品未删除 + 商家未注销）
     * @param id 菜品ID
     * @return 有效菜品，null表示不存在/已删除/商家已注销
     */
    public static FoodBean getAllFoodById(String id){
        String[] data = {id, String.valueOf(NOT_DELETED), String.valueOf(NOT_DELETED)};
        // 联表查询：过滤菜品ID + 菜品未删除 + 商家未注销
        String sql = "SELECT f.* FROM d_food f " +
                "LEFT JOIN d_business b ON f.s_business_id = b.s_id " +
                "WHERE f.s_food_id=? AND f.s_is_delete=? AND b.s_is_delete=?";
        Cursor cursor=db.rawQuery(sql, data);
        try {
            if (cursor.moveToNext()){
                return parseFoodBeanFromCursor(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    /**
     * 获取当前月的销售数量（仅统计有效菜品）
     * @param foodId 菜品ID
     * @return 销售数量
     */
    @SuppressLint("Range")
    public static int getMouSalesNum(String foodId){
        // 先判断菜品是否有效（未删除 + 商家未注销）
        FoodBean validFood = getAllFoodById(foodId);
        if (validFood == null) {
            return 0; // 无效菜品，销售数量为0
        }
        Cursor rs = db.rawQuery("SELECT *   FROM d_orders   " +
                "WHERE s_order_sta='3' and   strftime('%Y-%m', s_order_time) = strftime('%Y-%m', 'now');",null);
        List<String> list=new ArrayList<>();
        try {
            while(rs.moveToNext()){
                String temp= rs.getString(rs.getColumnIndex("s_order_details_id"));
                list.add(temp);
            }
        } finally {
            if (rs != null) {
                rs.close(); // 关闭游标释放资源
            }
        }
        int salNum=0;
        for (String s : list) {
            salNum=salNum+getOrderDetailsByOrderAndFoodId(s,foodId);
        }
        return salNum;
    }

    /**
     * 通过订单ID和菜品ID获取商品数量（仅统计有效菜品）
     * @param orderId 订单ID
     * @param foodId 菜品ID
     * @return 商品数量
     */
    @SuppressLint("Range")
    public static int getOrderDetailsByOrderAndFoodId(String orderId,String foodId){
        // 先判断菜品是否有效
        FoodBean validFood = getAllFoodById(foodId);
        if (validFood == null) {
            return 0;
        }
        String data[]={orderId,foodId};
        Cursor rs = db.rawQuery("select * from d_order_details where s_details_id=? and s_food_id=?",data);
        try {
            while(rs.moveToNext()){
                int tm = rs.getInt(rs.getColumnIndex("s_food_num"));
                return tm;
            }
        } finally {
            if (rs != null) {
                rs.close(); // 关闭游标释放资源
            }
        }
        return 0;
    }

    /**
     * 实现添加商品（默认未删除）
     * @param businessId 商家ID
     * @param foodName 菜品名称
     * @param des 菜品描述
     * @param foodPrice 菜品价格
     * @param img 菜品图片
     * @return 1-添加成功，0-添加失败
     */
    public static int addFood(String businessId,String foodName,String des,String foodPrice,String img){
        String id= UUID.randomUUID().toString().replace("-","");
        String data[]={id,businessId,foodName,des,foodPrice,img, String.valueOf(NOT_DELETED)};
        try {
            db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                            "VALUES (?, ?, ?,?,  ?, ?, ?)", // 新增s_is_delete字段
                    data);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 菜品伪删除：标记s_is_delete=1，不物理删除 ======================
    /**
     * 菜品删除（逻辑删除）：标记s_is_delete=1
     * @param foodId 菜品ID
     * @return 1-标记成功，0-标记失败
     */
    public static int delFoodById(String foodId){
        if (foodId == null || foodId.trim().isEmpty()) {
            return 0;
        }
        String data[]={String.valueOf(IS_DELETED), foodId};
        try {
            // 更新s_is_delete为1，而非DELETE
            db.execSQL("UPDATE d_food SET s_is_delete=? WHERE s_food_id=?", data);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== 批量标记商家菜品为已删除（商家注销时调用） ======================
    /**
     * 批量标记商家下所有菜品为已删除（逻辑删除）
     * @param businessId 商家ID
     * @return 1-标记成功，0-标记失败
     */
    public static int deleteFoodByBusinessId(String businessId) {
        if (businessId == null || businessId.trim().isEmpty()) {
            return 0;
        }
        String[] data = {String.valueOf(IS_DELETED), businessId};
        try {
            // 批量更新该商家下所有菜品的s_is_delete为1
            db.execSQL("UPDATE d_food SET s_is_delete=? WHERE s_business_id=?", data);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 实现更改食物（仅更新未删除的菜品）
     * @param foodId 菜品ID
     * @param foodName 菜品名称
     * @param des 菜品描述
     * @param foodPrice 菜品价格
     * @param img 菜品图片
     * @return 1-更新成功，0-更新失败
     */
    public static int updateFood(String foodId,String foodName,String des,String foodPrice,String img){
        String data[]={foodName,des,foodPrice,img, foodId, String.valueOf(NOT_DELETED)};
        try {
            db.execSQL("update  d_food set s_food_name=?, s_food_des=?,s_food_price=?,s_food_img=? where s_food_id=? and s_is_delete=?" ,
                    data); // 仅更新未删除的菜品
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 解析Cursor为FoodBean（提取重复逻辑，简化代码）
     * @param cursor 游标
     * @return FoodBean
     */
    @SuppressLint("Range")
    private static FoodBean parseFoodBeanFromCursor(Cursor cursor) {
        FoodBean foodBean=new FoodBean();
        String foodId=cursor.getString(cursor.getColumnIndex("s_food_id"));
        String businessId=cursor.getString(cursor.getColumnIndex("s_business_id"));
        String foodName=cursor.getString(cursor.getColumnIndex("s_food_name"));
        String foodDes=cursor.getString(cursor.getColumnIndex("s_food_des"));
        String foodPrice=cursor.getString(cursor.getColumnIndex("s_food_price"));
        String foodImg=cursor.getString(cursor.getColumnIndex("s_food_img"));
        foodBean.setFoodId(foodId);
        foodBean.setFoodDes(foodDes);
        foodBean.setFoodImg(foodImg);
        foodBean.setFoodName(foodName);
        foodBean.setFoodPrice(foodPrice);
        foodBean.setBusinessId(businessId);
        return foodBean;
    }
}