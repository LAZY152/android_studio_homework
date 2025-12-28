package com.ccf.feige.orderfood.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ccf.feige.orderfood.bean.UserBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.db.DBUntil;

/**
 * 操作数据库
 */
public class AdminDao {

    public static SQLiteDatabase  db=DBUntil.con;
    // 逻辑删除状态常量（便于维护，避免魔法值）
    private static final int NOT_DELETED = 0; // 未注销/未删除
    private static final int IS_DELETED = 1; // 已注销/已删除
    // 注销后统一昵称常量
    private static final String DELETED_USER_NAME = "用户已注销";

    /**
     * 实现保存商家
     * @param id
     * @param pwd
     * @param name
     * @param des
     * @param type
     * @param tx
     * @return
     */
    public static int saveBusinessUser(String id,String pwd,String name,String des,String type,String tx){
        String data[]={id,pwd, name, des,type,tx};
        try {
            db.execSQL("INSERT INTO d_business (s_id, s_pwd, s_name, s_describe, s_type, s_img, s_is_delete) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)", // 新增s_is_delete字段
                    new String[]{id,pwd, name, des,type,tx, String.valueOf(NOT_DELETED)}); // 默认未删除
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 更改商家用户
     * @param id
     * @param name
     * @param des
     * @param type
     * @param tx
     * @return
     */
    public static int updateBusinessUser(String id,String name,String des,String type,String tx){
        String data[]={name, des,type,tx,id};
        try {
            db.execSQL("update d_business  set s_name=? ,s_describe=? ,s_type=?, s_img=? where  s_id=? and s_is_delete=?",
                    new String[]{name, des,type,tx,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的商家
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static int updateBusinessUserPwd(String id,String pwd){
        String data[]={pwd,id};
        try {
            db.execSQL("update d_business  set s_pwd=?  where  s_id=? and s_is_delete=?",
                    new String[]{pwd,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的商家
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 修改普通用户密码
     * @param id
     * @param pwd
     * @return
     */
    public static int updateCommentUserPwd(String id,String pwd){
        String data[]={pwd,id};
        try {
            db.execSQL("update d_user   set s_pwd=?  where  s_id=? and s_is_delete=?",
                    new String[]{pwd,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的用户
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 这个是保存普通用户
     * @param id
     * @param pwd
     * @param name
     * @param sex
     * @param address
     * @param phone
     * @param tx
     * @return
     */
    public static int saveCommonUser(String id,String pwd,String name,String sex,String address,String phone,String tx){
        String data[]={id,pwd, name, sex,address,phone,tx};
        try {
            db.execSQL("INSERT INTO d_user (s_id, s_pwd, s_name,s_sex, s_address, s_phone, s_img, s_is_delete) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", // 新增s_is_delete字段
                    new String[]{id,pwd, name, sex,address,phone,tx, String.valueOf(NOT_DELETED)}); // 默认未删除
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 更改普通用户
     * @param id
     * @param name
     * @param sex
     * @param address
     * @param phone
     * @param tx
     * @return
     */
    public static int updateCommonUser(String id,String name,String sex,String address,String phone,String tx){
        String data[]={name, sex,address,phone,tx,id};
        try {
            db.execSQL("update  d_user set s_name=?,s_sex=?, s_address=?, s_phone=?, s_img=? where s_id=? and s_is_delete=?",
                    new String[]{name, sex,address,phone,tx,id, String.valueOf(NOT_DELETED)}); // 仅更新未注销的用户
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 登录商家账号（过滤已注销商家）
     * @param account
     * @param pwd
     * @return
     */
    public static int loginBusiness(String account,String pwd){
        String data[]={account,pwd, String.valueOf(NOT_DELETED)};
        String sql="select * from d_business where s_id=? and s_pwd=? and s_is_delete=?"; // 新增s_is_delete=0条件
        Cursor result = db.rawQuery(sql,data);
        try {
            while(result.moveToNext()){
                return 1;
            }
        } finally {
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return 0;
    }

    /**
     * 登录普通用户（过滤已注销用户）
     * @param account
     * @param pwd
     * @return
     */
    public static int loginUser(String account,String pwd){
        String data[]={account,pwd, String.valueOf(NOT_DELETED)};
        String sql="select * from d_user where s_id=? and s_pwd=? and s_is_delete=?"; // 新增s_is_delete=0条件
        Cursor result = db.rawQuery(sql,data);
        try {
            while(result.moveToNext()){
                return 1;
            }
        } finally {
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return 0;
    }

    /**
     * 获取用户账号信息内容（过滤已注销商家）
     * @param account
     * @return
     */
    @SuppressLint("Range")
    public static UserBean getBusinessUser(String account){
        String data[]={account, String.valueOf(NOT_DELETED)};
        String sql="select * from d_business where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件
        Cursor result = db.rawQuery(sql,data);
        try {
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
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    /**
     * 获取用户账号信息内容（过滤已注销普通用户）
     * @param account
     * @return
     */
    @SuppressLint("Range")
    public static UserCommonBean getCommonUser(String account){
        String data[]={account, String.valueOf(NOT_DELETED)};
        String sql="select * from d_user where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件
        Cursor result = db.rawQuery(sql,data);
        try {
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
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    /**
     * 根据账号获取商家密码（过滤已注销商家）
     * @param account 商家账号（s_id）
     * @return 商家密码（s_pwd），null表示查询失败/商家不存在/已注销
     */
    @SuppressLint("Range")
    public static String getBusinessUserPwd(String account) {
        if (account == null || account.trim().isEmpty()) {
            return null;
        }
        String[] data = {account, String.valueOf(NOT_DELETED)};
        String sql = "select s_pwd from d_business where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件
        Cursor result = db.rawQuery(sql, data);
        try {
            while (result.moveToNext()) {
                String pwd = result.getString(result.getColumnIndex("s_pwd"));
                return pwd;
            }
        } finally {
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    /**
     * 根据账号获取普通用户密码（过滤已注销用户）
     * @param account 普通用户账号（s_id）
     * @return 用户密码（s_pwd），null表示查询失败/用户不存在/已注销
     */
    @SuppressLint("Range")
    public static String getCommonUserPwd(String account) {
        if (account == null || account.trim().isEmpty()) {
            return null;
        }
        String[] data = {account, String.valueOf(NOT_DELETED)};
        String sql = "select s_pwd from d_user where s_id=? and s_is_delete=?"; // 新增s_is_delete=0条件
        Cursor result = db.rawQuery(sql, data);
        try {
            while (result.moveToNext()) {
                String pwd = result.getString(result.getColumnIndex("s_pwd"));
                return pwd;
            }
        } finally {
            if (result != null) {
                result.close(); // 关闭游标释放资源
            }
        }
        return null;
    }

    // ====================== 商家伪删除：标记s_is_delete=1，不物理删除，同步修改昵称为“用户已注销” ======================
    /**
     * 商家注销（逻辑删除）：标记s_is_delete=1，同时修改s_name为“用户已注销”
     * @param account 商家账号（s_id）
     * @return true-标记成功，false-标记失败
     */
    public static boolean deleteBusinessUser(String account) {
        if (account == null || account.trim().isEmpty()) {
            return false;
        }
        // 调整参数：1. 注销后昵称 2. 逻辑删除标记 3. 商家账号
        String[] data = {DELETED_USER_NAME, String.valueOf(IS_DELETED), account};
        try {
            // 同步更新s_name和s_is_delete字段，保证原子操作
            db.execSQL("UPDATE d_business SET s_name=?, s_is_delete=? WHERE s_id=?", data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 普通用户伪删除：标记s_is_delete=1，不物理删除，同步修改昵称为“用户已注销” ======================
    /**
     * 普通用户注销（逻辑删除）：标记s_is_delete=1，同时修改s_name为“用户已注销”
     * @param account 普通用户账号（s_id）
     * @return true-标记成功，false-标记失败
     */
    public static boolean deleteCommonUser(String account) {
        if (account == null || account.trim().isEmpty()) {
            return false;
        }
        // 调整参数：1. 注销后昵称 2. 逻辑删除标记 3. 普通用户账号
        String[] data = {DELETED_USER_NAME, String.valueOf(IS_DELETED), account};
        try {
            // 同步更新s_name和s_is_delete字段，保证原子操作
            db.execSQL("UPDATE d_user SET s_name=?, s_is_delete=? WHERE s_id=?", data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}