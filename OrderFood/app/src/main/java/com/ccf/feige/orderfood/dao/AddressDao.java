package com.ccf.feige.orderfood.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ccf.feige.orderfood.bean.AddressBean;
import com.ccf.feige.orderfood.db.DBUntil;
import com.ccf.feige.orderfood.until.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 地址数据访问对象（DAO）
 * 负责处理收货地址相关的数据库操作，包括增、删、改、查功能
 * 所有方法均为静态方法，直接操作全局的SQLiteDatabase实例
 */
public class AddressDao {

    /**
     * 全局SQLite数据库实例，从DBUntil工具类中获取已初始化的数据库连接
     */
    public static SQLiteDatabase db= DBUntil.con;


    /**
     * 根据地址ID删除对应的收货地址
     * @param id 要删除的地址记录的唯一标识（s_id字段值）
     * @return 操作结果标识，1表示删除成功，0表示删除失败（捕获到异常）
     */
    public static  int deleteAddressById(String id){
        try {
            // 执行SQL删除语句，通过占位符传递地址ID，避免SQL注入
            db.execSQL("delete from d_address where s_id=?",new String []{id});
            return 1;
        }catch (Exception e){
            // 捕获异常并打印堆栈信息，便于排查问题
            e.printStackTrace();
            return 0;
        }

    }







    /**
     * 根据用户ID查询该用户名下的所有收货地址
     * @param userId 要查询地址的用户唯一标识（s_user_id字段值）
     * @return 封装了该用户所有收货地址的List<AddressBean>集合，无地址时返回空集合
     */
    public static List<AddressBean> getAllAddressByUserId(String userId){
        // 执行SQL查询语句，通过占位符传递用户ID，查询该用户的所有地址记录
        Cursor rs = db.rawQuery("select * from d_address where s_user_id=?", new String[]{userId});
        // 初始化地址集合，用于存储查询结果封装后的AddressBean对象
        List<AddressBean> list=new ArrayList<>();
        // 循环遍历游标结果集，将每条记录封装为AddressBean对象
        while(rs.moveToNext()){
            AddressBean addressBean=new AddressBean();
            // 通过Tools工具类获取游标中对应字段的值，并设置到AddressBean对象中
            addressBean.setsId(Tools.getResultString(rs,"s_id"));
            addressBean.setsUserId(Tools.getResultString(rs,"s_user_id"));
            addressBean.setsUserName(Tools.getResultString(rs,"s_user_name"));
            addressBean.setsUserAddress(Tools.getResultString(rs,"s_user_address"));
            addressBean.setsUserPhone(Tools.getResultString(rs,"s_user_phone"));
            // 将封装好的地址对象添加到集合中
            list.add(addressBean);
        }

        // 返回封装好的地址集合
        return list;


    }



    /**
     * 根据地址ID修改收货地址的联系人、详细地址和联系电话
     * @param id 要修改的地址记录的唯一标识（s_id字段值）
     * @param name 修改后的联系人姓名
     * @param address 修改后的详细收货地址
     * @param phone 修改后的联系电话
     * @return 操作结果标识，1表示修改成功，0表示修改失败（捕获到异常）
     */
    public static int updateAddress(String id,String name,String address,String phone){
        try{
            // 封装修改参数，按SQL语句中占位符的顺序排列
            String data[]={name,address,phone,id};
            // 执行SQL更新语句，更新对应地址ID的联系人、地址、电话信息
            db.execSQL("update d_address set s_user_name=?,s_user_address=?,s_user_phone=? where s_id=?",data);
            return 1;
        }catch (Exception e){
            // 捕获异常，返回修改失败标识
            return 0;
        }

    }

    /**
     * 新增一条收货地址记录
     * @param id 新增地址所属用户的唯一标识（s_user_id字段值）
     * @param name 收货联系人姓名
     * @param address 详细收货地址
     * @param phone 收货联系电话
     * @return 操作结果标识，1表示添加成功，0表示添加失败（捕获到异常）
     */
    public static int addAddress(String id,String name,String address,String phone){
        try{
            // 生成唯一UUID并去除中间的"-"符号，作为地址记录的唯一标识（s_id字段值）
            String uuid= UUID.randomUUID().toString().replace("-","");
            // 执行SQL插入语句，将UUID、用户ID、联系人、地址、电话插入到地址表中
            db.execSQL("INSERT INTO d_address (s_id,s_user_id, s_user_name,s_user_address" +
                            ",s_user_phone) " +
                            "VALUES (?, ?, ?,?, ?)",
                    new Object[]{uuid, id, name, address, phone});
            return 1;
        }catch (Exception e){
            // 捕获异常，返回添加失败标识
            return 0;
        }

    }




}