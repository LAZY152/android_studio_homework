package com.ccf.feige.orderfood.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ccf.feige.orderfood.bean.UserBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.db.DBUntil;

/**
 * 管理员/用户数据访问对象（DAO）
 * 负责处理商家用户和普通用户的数据库操作，包括新增、修改、查询、登录验证、逻辑删除等功能
 * 所有操作均基于SQLite数据库，采用逻辑删除（标记删除状态）而非物理删除，保障数据可追溯性
 */
public class AdminDao {

    /**
     * 全局SQLite数据库实例，从DBUntil工具类中获取已初始化的数据库连接
     */
    public static SQLiteDatabase  db=DBUntil.con;

    // 逻辑删除状态常量（便于维护，避免魔法值，提升代码可读性）
    /** 未注销/未删除状态标识，对应数据库表中s_is_delete字段值为0 */
    private static final int NOT_DELETED = 0;
    /** 已注销/已删除状态标识，对应数据库表中s_is_delete字段值为1 */
    private static final int IS_DELETED = 1;

    // 注销后统一昵称常量
    /** 用户/商家注销后，统一设置的昵称，对应数据库表中s_name字段值 */
    private static final String DELETED_USER_NAME = "用户已注销";

    /**
     * 保存（新增）商家用户信息到数据库
     * @param id 商家唯一标识（s_id字段，作为账号）
     * @param pwd 商家登录密码（s_pwd字段）
     * @param name 商家名称（s_name字段）
     * @param des 商家描述/简介（s_describe字段）
     * @param type 商家类型/品类（s_type字段）
     * @param tx 商家头像图片路径（s_img字段）
     * @return 操作结果标识，1表示新增成功，0表示新增失败（捕获到异常）
     */
    public static int saveBusinessUser(String id,String pwd,String name,String des,String type,String tx){
        String data[]={id,pwd, name, des,type,tx};
        try {
            // 执行SQL插入语句，新增商家记录，默认设置s_is_delete为NOT_DELETED（0），表示未注销
            db.execSQL("INSERT INTO d_business (s_id, s_pwd, s_name, s_describe, s_type, s_img, s_is_delete) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)", // 新增s_is_delete字段，记录商家注销状态
                    new String[]{id,pwd, name, des,type,tx, String.valueOf(NOT_DELETED)}); // 默认未删除
            return 1;
        }catch (Exception e){
            // 捕获异常并打印堆栈信息，便于排查新增失败问题
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 更改（更新）商家用户的基本信息（不含密码）
     * @param id 商家唯一标识（s_id字段，用于定位要修改的商家记录）
     * @param name 修改后的商家名称（s_name字段）
     * @param des 修改后的商家描述/简介（s_describe字段）
     * @param type 修改后的商家类型/品类（s_type字段）
     * @param tx 修改后的商家头像图片路径（s_img字段）
     * @return 操作结果标识，1表示更新成功，0表示更新失败（捕获到异常）
     */
    public static int updateBusinessUser(String id,String name,String des,String type,String tx){
        String data[]={name, des,type,tx,id};
        try {
            // 执行SQL更新语句，仅更新s_is_delete为NOT_DELETED（0）的未注销商家记录
            db.execSQL("update d_business  set s_name=? ,s_describe=? ,s_type=?, s_img=? where  s_id=? and s_is_delete=?",
                    new String[]{name, des,type,tx,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的商家
            return 1;
        }catch (Exception e){
            // 捕获异常并打印堆栈信息，便于排查更新失败问题
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 更改（更新）商家用户的登录密码
     * @param id 商家唯一标识（s_id字段，用于定位要修改的商家记录）
     * @param pwd 商家新的登录密码（s_pwd字段）
     * @return 操作结果标识，1表示密码更新成功，0表示密码更新失败（捕获到异常）
     */
    public static int updateBusinessUserPwd(String id,String pwd){
        String data[]={pwd,id};
        try {
            // 执行SQL更新语句，仅更新s_is_delete为NOT_DELETED（0）的未注销商家的密码
            db.execSQL("update d_business  set s_pwd=?  where  s_id=? and s_is_delete=?",
                    new String[]{pwd,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的商家
            return 1;
        }catch (Exception e){
            // 捕获异常并打印堆栈信息，便于排查密码更新失败问题
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 修改普通用户的登录密码
     * @param id 普通用户唯一标识（s_id字段，用于定位要修改的用户记录）
     * @param pwd 普通用户新的登录密码（s_pwd字段）
     * @return 操作结果标识，1表示密码更新成功，0表示密码更新失败（捕获到异常）
     */
    public static int updateCommentUserPwd(String id,String pwd){
        String data[]={pwd,id};
        try {
            // 执行SQL更新语句，仅更新s_is_delete为NOT_DELETED（0）的未注销普通用户的密码
            db.execSQL("update d_user   set s_pwd=?  where  s_id=? and s_is_delete=?",
                    new String[]{pwd,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的用户
            return 1;
        }catch (Exception e){
            // 捕获异常并打印堆栈信息，便于排查密码更新失败问题
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 保存（新增）普通用户信息到数据库
     * @param id 普通用户唯一标识（s_id字段，作为账号）
     * @param pwd 普通用户登录密码（s_pwd字段）
     * @param name 普通用户昵称（s_name字段）
     * @param sex 普通用户性别（s_sex字段）
     * @param address 普通用户默认地址（s_address字段）
     * @param phone 普通用户联系电话（s_phone字段）
     * @param tx 普通用户头像图片路径（s_img字段）
     * @return 操作结果标识，1表示新增成功，0表示新增失败（捕获到异常）
     */
    public static int saveCommonUser(String id,String pwd,String name,String sex,String address,String phone,String tx){
        String data[]={id,pwd, name, sex,address,phone,tx};
        try {
            // 执行SQL插入语句，新增普通用户记录，默认设置s_is_delete为NOT_DELETED（0），表示未注销
            db.execSQL("INSERT INTO d_user (s_id, s_pwd, s_name,s_sex, s_address, s_phone, s_img, s_is_delete) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", // 新增s_is_delete字段，记录用户注销状态
                    new String[]{id,pwd, name, sex,address,phone,tx, String.valueOf(NOT_DELETED)}); // 默认未删除
            return 1;
        }catch (Exception e){
            // 捕获异常并打印堆栈信息，便于排查新增失败问题
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 更改（更新）普通用户的基本信息（不含密码）
     * @param id 普通用户唯一标识（s_id字段，用于定位要修改的用户记录）
     * @param name 修改后的普通用户昵称（s_name字段）
     * @param sex 修改后的普通用户性别（s_sex字段）
     * @param address 修改后的普通用户默认地址（s_address字段）
     * @param phone 修改后的普通用户联系电话（s_phone字段）
     * @param tx 修改后的普通用户头像图片路径（s_img字段）
     * @return 操作结果标识，1表示更新成功，0表示更新失败（捕获到异常）
     */
    public static int updateCommonUser(String id,String name,String sex,String address,String phone,String tx){
        String data[]={name, sex,address,phone,tx,id};
        try {
            // 执行SQL更新语句，仅更新s_is_delete为NOT_DELETED（0）的未注销普通用户记录
            db.execSQL("update  d_user set s_name=?,s_sex=?, s_address=?, s_phone=?, s_img=? where s_id=? and s_is_delete=?",
                    new String[]{name, sex,address,phone,tx,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的用户
            return 1;
        }catch (Exception e){
            // 捕获异常并打印堆栈信息，便于排查更新失败问题
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 商家账号登录验证（过滤已注销商家）
     * @param account 商家登录账号（对应s_id字段）
     * @param pwd 商家登录密码（对应s_pwd字段）
     * @return 验证结果标识，1表示登录成功（账号密码正确且商家未注销），0表示登录失败（账号密码错误/商家已注销/无该商家）
     */
    public static int loginBusiness(String account,String pwd){
        String data[]={account,pwd, String.valueOf(NOT_DELETED)};
        String sql="select * from d_business where s_id=? and s_pwd=? and s_is_delete=?"; // 新增s_is_delete=0条件，过滤已注销商家
        Cursor result = db.rawQuery(sql,data);
        try {
            // 遍历游标结果集，若存在匹配记录则表示登录成功
            while(result.moveToNext()){
                return 1;
            }
        } finally {
            // 最终关闭游标，释放数据库资源，避免内存泄漏
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return 0;
    }

    /**
     * 普通用户账号登录验证（过滤已注销用户）
     * @param account 普通用户登录账号（对应s_id字段）
     * @param pwd 普通用户登录密码（对应s_pwd字段）
     * @return 验证结果标识，1表示登录成功（账号密码正确且用户未注销），0表示登录失败（账号密码错误/用户已注销/无该用户）
     */
    public static int loginUser(String account,String pwd){
        String data[]={account,pwd, String.valueOf(NOT_DELETED)};
        String sql="select * from d_user where s_id=? and s_pwd=? and s_is_delete=?"; // 新增s_is_delete=0条件，过滤已注销用户
        Cursor result = db.rawQuery(sql,data);
        try {
            // 遍历游标结果集，若存在匹配记录则表示登录成功
            while(result.moveToNext()){
                return 1;
            }
        } finally {
            // 最终关闭游标，释放数据库资源，避免内存泄漏
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return 0;
    }

    /**
     * 根据商家账号获取商家完整信息（过滤已注销商家）
     * @param account 商家账号（对应s_id字段）
     * @return 封装了商家完整信息的UserBean对象，null表示商家不存在/已注销/查询失败
     */
    @SuppressLint("Range")
    public static UserBean getBusinessUser(String account){
        String data[]={account, String.valueOf(NOT_DELETED)};
        String sql="select * from d_business where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件，过滤已注销商家
        Cursor result = db.rawQuery(sql,data);
        try {
            // 遍历游标结果集，将查询到的商家记录封装为UserBean对象
            while(result.moveToNext()){
                String id=result.getString(result.getColumnIndex("s_id"));
                String pwd=result.getString(result.getColumnIndex("s_pwd"));
                String name=result.getString(result.getColumnIndex("s_name"));
                String des=result.getString(result.getColumnIndex("s_describe"));
                String type=result.getString(result.getColumnIndex("s_type"));
                String img=result.getString(result.getColumnIndex("s_img"));
                UserBean userBean=new UserBean(id,pwd,name,des,type,img);
                return userBean;
            }
        } finally {
            // 最终关闭游标，释放数据库资源，避免内存泄漏
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    /**
     * 根据普通用户账号获取用户完整信息（过滤已注销用户）
     * @param account 普通用户账号（对应s_id字段）
     * @return 封装了普通用户完整信息的UserCommonBean对象，null表示用户不存在/已注销/查询失败
     */
    @SuppressLint("Range")
    public static UserCommonBean getCommonUser(String account){
        String data[]={account, String.valueOf(NOT_DELETED)};
        String sql="select * from d_user where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件，过滤已注销用户
        Cursor result = db.rawQuery(sql,data);
        try {
            // 遍历游标结果集，将查询到的普通用户记录封装为UserCommonBean对象
            while(result.moveToNext()){
                String id=result.getString(result.getColumnIndex("s_id"));
                String pwd=result.getString(result.getColumnIndex("s_pwd"));
                String name=result.getString(result.getColumnIndex("s_name"));
                String sex=result.getString(result.getColumnIndex("s_sex"));
                String address=result.getString(result.getColumnIndex("s_address"));
                String phone=result.getString(result.getColumnIndex("s_phone"));
                String img=result.getString(result.getColumnIndex("s_img"));
                UserCommonBean userCommonBean=new UserCommonBean(id,pwd,name,sex,address,phone,img);
                return userCommonBean;
            }
        } finally {
            // 最终关闭游标，释放数据库资源，避免内存泄漏
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    /**
     * 根据商家账号获取商家登录密码（过滤已注销商家）
     * @param account 商家账号（s_id）
     * @return 商家密码（s_pwd），null表示查询失败/商家不存在/已注销/账号为空
     */
    @SuppressLint("Range")
    public static String getBusinessUserPwd(String account) {
        // 前置参数校验，避免空指针异常和无效查询
        if (account == null || account.trim().isEmpty()) {
            return null;
        }
        String[] data = {account, String.valueOf(NOT_DELETED)};
        String sql = "select s_pwd from d_business where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件，过滤已注销商家
        Cursor result = db.rawQuery(sql, data);
        try {
            // 遍历游标结果集，获取商家密码
            while (result.moveToNext()) {
                String pwd = result.getString(result.getColumnIndex("s_pwd"));
                return pwd;
            }
        } finally {
            // 最终关闭游标，释放数据库资源，避免内存泄漏
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    /**
     * 根据账号获取普通用户密码（过滤已注销用户）
     * @param account 普通用户账号（s_id）
     * @return 用户密码（s_pwd），null表示查询失败/用户不存在/已注销/账号为空
     */
    @SuppressLint("Range")
    public static String getCommonUserPwd(String account) {
        // 前置参数校验，避免空指针异常和无效查询
        if (account == null || account.trim().isEmpty()) {
            return null;
        }
        String[] data = {account, String.valueOf(NOT_DELETED)};
        String sql = "select s_pwd from d_user where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件，过滤已注销用户
        Cursor result = db.rawQuery(sql, data);
        try {
            // 遍历游标结果集，获取普通用户密码
            while (result.moveToNext()) {
                String pwd = result.getString(result.getColumnIndex("s_pwd"));
                return pwd;
            }
        } finally {
            // 最终关闭游标，释放数据库资源，避免内存泄漏
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    // ====================== 商家伪删除：标记s_is_delete=1，不物理删除，同步修改昵称为“用户已注销” ======================
    /**
     * 商家注销（逻辑删除）：标记s_is_delete=1，同时修改s_name为“用户已注销”
     * 说明：逻辑删除不物理删除数据库记录，仅修改状态标识，保障数据可追溯，便于后续恢复或统计
     * @param account 商家账号（s_id）
     * @return true-标记成功，false-标记失败（账号为空/捕获到异常）
     */
    public static boolean deleteBusinessUser(String account) {
        // 前置参数校验，避免空指针异常和无效操作
        if (account == null || account.trim().isEmpty()) {
            return false;
        }
        // 调整参数：1. 注销后昵称 2. 逻辑删除标记 3. 商家账号
        String[] data = {DELETED_USER_NAME, String.valueOf(IS_DELETED), account};
        try {
            // 同步更新s_name和s_is_delete字段，保证原子操作（要么都更新成功，要么都失败）
            db.execSQL("UPDATE d_business SET s_name=?, s_is_delete=? WHERE s_id=?", data);
            return true;
        } catch (Exception e) {
            // 捕获异常并打印堆栈信息，便于排查注销失败问题
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 普通用户伪删除：标记s_is_delete=1，不物理删除，同步修改昵称为“用户已注销” ======================
    /**
     * 普通用户注销（逻辑删除）：标记s_is_delete=1，同时修改s_name为“用户已注销”
     * 说明：逻辑删除不物理删除数据库记录，仅修改状态标识，保障数据可追溯，便于后续恢复或统计
     * @param account 普通用户账号（s_id）
     * @return true-标记成功，false-标记失败（账号为空/捕获到异常）
     */
    public static boolean deleteCommonUser(String account) {
        // 前置参数校验，避免空指针异常和无效操作
        if (account == null || account.trim().isEmpty()) {
            return false;
        }
        // 调整参数：1. 注销后昵称 2. 逻辑删除标记 3. 普通用户账号
        String[] data = {DELETED_USER_NAME, String.valueOf(IS_DELETED), account};
        try {
            // 同步更新s_name和s_is_delete字段，保证原子操作（要么都更新成功，要么都失败）
            db.execSQL("UPDATE d_user SET s_name=?, s_is_delete=? WHERE s_id=?", data);
            return true;
        } catch (Exception e) {
            // 捕获异常并打印堆栈信息，便于排查注销失败问题
            e.printStackTrace();
            return false;
        }
    }

}