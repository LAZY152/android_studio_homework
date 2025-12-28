package com.ccf.feige.orderfood.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.until.FileImgUntil;

/**
 * 链接数据库
 */
public class DBUntil extends SQLiteOpenHelper { //ALT +回车

    // 关键：修改表结构/状态后，版本号+1（原32→33），确保onUpgrade触发重建表，新状态生效
    private static final int version=33;
    private static final  String databaseName="db_takeaway.db";//数据库名称必须以db结尾
    private  Context context;

    public static SQLiteDatabase con;//链接数据库的链接，通过他可以操作数据库

    // 逻辑删除状态常量（与Bean、Dao层保持一致）
    private static final int NOT_DELETED = 0;

    // ====================== 新增：订单状态常量（避免魔法值，便于维护） ======================
    private static final String ORDER_STA_UNHANDLED = "1"; // 1. 未处理订单
    private static final String ORDER_STA_CANCEL = "2"; // 2. 取消订单
    private static final String ORDER_STA_FINISH = "3"; // 3. 完成的订单（未评论）
    private static final String ORDER_STA_FINISH_COMMENTED = "4"; // 4. 订单完成且被评论

    public DBUntil(Context context) {
        super(context, databaseName, null, version,null);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建数据库
        db.execSQL("PRAGMA foreign_keys = false");

        // ====================== 1. 修改d_business表：新增s_is_delete字段，修正varcahr拼写错误 ======================
        db.execSQL("drop table if exists d_business");//如果这表存在则删除
        db.execSQL("create table d_business(s_id varchar(20) primary key," +
                "s_pwd varchar(20)," +// 修正：varcahr→varchar
                "s_name varchar(20)," +// 修正：varcahr→varchar
                "s_describe varchar(200)," +// 修正：varcahr→varchar
                "s_type varchar(20)," +// 修正：varcahr→varchar
                "s_img varchar(255)," +
                "s_is_delete INTEGER DEFAULT " + NOT_DELETED + ")");// 新增：逻辑删除字段，默认未注销

        String shop= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.njdpd,shop);


        // 插入d_business数据：补充s_is_delete字段（默认未注销）
        db.execSQL("INSERT INTO d_business (s_id, s_pwd, s_name, s_describe, s_type, s_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"admin", "123456", "南京大排档", "南京大排档始创于1994年，三十年如一日磨出饮食佳境。全国上百家直营门店，地道金陵味。如有问题，请随时联系本店，定会竭力为您解决。", "餐饮店", shop, NOT_DELETED});


        // ====================== 2. 修改d_user表：新增s_is_delete字段，修正varcahr拼写错误 ======================
        db.execSQL("drop table if exists d_user");//如果这表存在则删除
        db.execSQL("create table d_user(s_id varchar(20) primary key," +
                "s_pwd varchar(20)," +// 修正：varcahr→varchar
                "s_name varchar(20)," +// 修正：varcahr→varchar
                "s_sex varchar(200)," +// 修正：varcahr→varchar
                "s_address varchar(200)," +// 修正：varcahr→varchar
                "s_phone varchar(20)," +// 修正：varcahr→varchar
                "s_img varchar(255)," +
                "s_is_delete INTEGER DEFAULT " + NOT_DELETED + ")");// 新增：逻辑删除字段，默认未注销

        String user= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.zs,user);

        // 插入d_user数据：补充s_is_delete字段（默认未注销）
        db.execSQL("INSERT INTO d_user (s_id, s_pwd, s_name,s_sex, s_address, s_phone, s_img, s_is_delete) " +
                        "VALUES (?, ?, ?,?, ?,?, ?, ?)",
                new Object[]{"test", "123456", "张硕", "男", "徐州市","12312312312", user, NOT_DELETED});

        // ====================== 3. 修改d_food表：新增s_is_delete字段，修正varcahr拼写错误 ======================
        //写一个存储食物的表
        db.execSQL("drop table if exists d_food");//如果这表存在则删除
        db.execSQL("create table d_food(s_food_id varchar(20) primary key," +
                "s_business_id varchar(20)," +// 修正：varcahr→varchar
                "s_food_name varchar(20)," +// 修正：varcahr→varchar
                "s_food_des varchar(200)," +// 修正：varcahr→varchar
                "s_food_price varchar(200)," +// 修正：varcahr→varchar
                "s_food_img varchar(255)," +
                "s_is_delete INTEGER DEFAULT " + NOT_DELETED + ")");// 新增：逻辑删除字段，默认未删除

        String foodImg1= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.roast_duck,foodImg1);

        // 插入d_food数据：补充s_is_delete字段（默认未删除）
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?,?,  ?, ?, ?)",
                new Object[]{"1", "admin", "南京烤鸭", "南京烤鸭香、脆、瞅、鲜、嫩，旺火细烤去除多余水分，配己以特制卤汁细细品味，肉质紧实，尝之把命不喊。", "19", foodImg1, NOT_DELETED});

        String foodImg2= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.nanjing_duck_blood_vermicelli_soup,foodImg2);
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"2", "admin", "鸭血粉丝汤", "南京传统地方风味，鲜嫩的鸭肝、鸭血与劲道的粉丝同煲，鲜美和比。", "24", foodImg2, NOT_DELETED});

        String foodImg3= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.salted_duck,foodImg3);
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"3", "admin", "南京盐水鸭", "搓盐、复卤、挂晾、炆煮，十$道工序，一招一式绝不走样，招牌菜来之不易。", "17", foodImg3, NOT_DELETED});

        String foodImg4= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.nanjing_xiaolongbao,foodImg4);
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"4", "admin", "金陵汤包", "用烤鸭肉制成的小笼包，你吃过吗？本店创制，不可不尝哦。", "12", foodImg4, NOT_DELETED});

        // ====================== 4. 修改d_orders表：新增状态4（订单完成且被评论），保留原有结构 ======================
        //订单编号，订单时间，购买人id,商家id,订单详情id,收货地址，商品详情
        db.execSQL("drop table if exists d_orders");//如果这表存在则删除
        db.execSQL("create table d_orders(s_order_id varchar(20) primary key," +
                "s_order_time varchar(30)," +// 修正：varcahr→varchar
                "s_business_id varchar(20)," +// 修正：varcahr→varchar
                "s_user_id varchar(20)," +// 修正：varcahr→varchar
                "s_order_details_id varchar(30)," +// 修正：varcahr→varchar
                "s_order_sta varchar(30)," +// 订单状态：1未处理 2取消 3完成（未评论）4完成且被评论
                "s_order_address varchar(255))");//存储是图片路径

        // ====================== 5. 以下表结构不变，保持原有逻辑 ======================
        //写一个存储食物的表
        db.execSQL("drop table if exists d_order_details");//如果这表存在则删除
        db.execSQL("create table d_order_details(s_details_id varchar(30)," +
                "s_food_id varchar(20)," +// 修正：varcahr→varchar
                "s_food_name varchar(20)," +// 修正：varcahr→varchar
                "s_food_des varchar(200)," +// 修正：varcahr→varchar
                "s_food_price varchar(20)," +// 修正：varcahr→varchar
                "s_food_num varchar(20)," +// 修正：varcahr→varchar
                "s_food_img varchar(255))");//存储是图片路径



        db.execSQL("drop table if exists d_comments");//如果这表存在则删除
        db.execSQL("create table d_comments(s_comment_id varchar(20) primary key," +//评论ID
                "s_comment_user_id varchar(20)," +//评论用户的ID（修正：varcahr→varchar）
                "s_comment_business_id varchar(20)," +//评论商家的ID（修正：varcahr→varchar）
                "s_comment_con varchar(200)," +//评论内容（修正：varcahr→varchar）
                "s_comment_time varchar(20)," +//评论时间（修正：varcahr→varchar）
                "s_comment_score varchar(20)," +//评论分数（修正：varcahr→varchar）
                "s_comment_img varchar(255))");//评论图片 可有 可无

        db.execSQL("drop table if exists d_address");//如果这表存在则删除
        db.execSQL("create table d_address(s_id varchar(20) primary key," +//评论ID
                "s_user_id varchar(20)," +//用户账号（修正：varcahr→varchar）
                "s_user_name varchar(20)," +//用户名称（修正：varcahr→varchar）
                "s_user_address varchar(200)," +//用户地址（修正：varcahr→varchar）
                "s_user_phone varchar(255))");//联系方法

        db.execSQL("PRAGMA foreign_keys = true");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 版本更新时，重建所有表（保持原有逻辑，确保新状态4生效）
        onCreate(db);
    }
}