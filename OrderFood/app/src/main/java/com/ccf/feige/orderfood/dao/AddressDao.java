package com.ccf.feige.orderfood.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ccf.feige.orderfood.bean.AddressBean;
import com.ccf.feige.orderfood.db.DBUntil;
import com.ccf.feige.orderfood.until.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddressDao {

    public static SQLiteDatabase db= DBUntil.con;


    /**
     * 删除地址
     * @param id
     * @return
     */
    public static  int deleteAddressById(String id){
        try {
            db.execSQL("delete from d_address where s_id=?",new String []{id});
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }







    /**
     * 查询地址
     * @param userId
     * @return
     */
    public static List<AddressBean> getAllAddressByUserId(String userId){
        Cursor rs = db.rawQuery("select * from d_address where s_user_id=?", new String[]{userId});
        List<AddressBean> list=new ArrayList<>();
        while(rs.moveToNext()){
            AddressBean addressBean=new AddressBean();
            addressBean.setsId(Tools.getResultString(rs,"s_id"));
            addressBean.setsUserId(Tools.getResultString(rs,"s_user_id"));
            addressBean.setsUserName(Tools.getResultString(rs,"s_user_name"));
            addressBean.setsUserAddress(Tools.getResultString(rs,"s_user_address"));
            addressBean.setsUserPhone(Tools.getResultString(rs,"s_user_phone"));
            list.add(addressBean);
        }

        return list;


    }



    /**
     * 更改收货地址
     * @param id
     * @param name
     * @param address
     * @param phone
     * @return
     */

    public static int updateAddress(String id,String name,String address,String phone){
        try{
            String data[]={name,address,phone,id};
            db.execSQL("update d_address set s_user_name=?,s_user_address=?,s_user_phone=? where s_id=?",data);
            return 1;
        }catch (Exception e){
            return 0;
        }

    }

    /**
     * 添加功能
     * @param id
     * @param name
     * @param address
     * @param phone
     * @return
     */
    public static int addAddress(String id,String name,String address,String phone){
        try{
            String uuid= UUID.randomUUID().toString().replace("-","");
            db.execSQL("INSERT INTO d_address (s_id,s_user_id, s_user_name,s_user_address" +
                            ",s_user_phone) " +
                            "VALUES (?, ?, ?,?, ?)",
                    new Object[]{uuid, id, name, address, phone});
            return 1;
        }catch (Exception e){
            return 0;
        }

    }




}
