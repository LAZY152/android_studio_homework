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
 * 数据库辅助工具类（继承SQLiteOpenHelper）
 * 核心功能：负责外卖系统SQLite数据库的创建、版本升级，定义表结构并插入初始化测试数据
 * 注意：继承SQLiteOpenHelper后需实现onCreate()和onUpgrade()方法，可通过ALT+回车快速补全
 */
public class DBUntil extends SQLiteOpenHelper { //ALT +回车

    // ====================== 数据库核心常量定义 ======================
    /**
     * 数据库版本号
     * 关键注意点：修改表结构/字段/业务状态后，必须将版本号+1（本次从32升级至33）
     * 版本号变更后会触发onUpgrade()方法，执行表结构重建，确保新的配置生效
     */
    private static final int version=33;

    /**
     * 数据库文件名
     * 命名规范：Android SQLite数据库文件必须以.db结尾，便于系统识别和后续维护
     * 该数据库为外卖系统的核心数据库，存储商家、用户、菜品、订单等全量业务数据
     */
    private static final  String databaseName="db_takeaway.db";//数据库名称必须以db结尾

    /**
     * 应用上下文对象
     * 用途：用于获取应用内的资源（如drawable中的图片资源）、访问文件目录等
     */
    private  Context context;

    /**
     * 静态SQLiteDatabase数据库连接对象
     * 核心作用：作为操作数据库的唯一入口，所有表的增、删、改、查操作均通过该对象执行
     */
    public static SQLiteDatabase con;//链接数据库的链接，通过他可以操作数据库

    /**
     * 逻辑删除状态常量（未删除/未注销）
     * 取值说明：0代表未删除/未注销，1代表已删除/已注销（后续可扩展）
     * 设计规范：该常量需与Bean实体类、Dao数据访问层保持一致，确保业务状态统一
     */
    private static final int NOT_DELETED = 0;

    // ====================== 订单状态常量定义（避免魔法值，提升可维护性） ======================
    /** 订单状态1：未处理（用户提交订单后，商家尚未处理） */
    private static final String ORDER_STA_UNHANDLED = "1";
    /** 订单状态2：已取消（用户主动取消或商家驳回订单） */
    private static final String ORDER_STA_CANCEL = "2";
    /** 订单状态3：已完成（用户确认收货，尚未进行订单评论） */
    private static final String ORDER_STA_FINISH = "3";
    /** 订单状态4：已完成且已评论（用户确认收货并提交评论，订单生命周期结束） */
    private static final String ORDER_STA_FINISH_COMMENTED = "4";

    /**
     * 构造方法：初始化数据库辅助类
     * @param context 应用上下文对象
     * 说明：调用父类SQLiteOpenHelper的构造方法，传入数据库名、游标工厂（null）、版本号、数据库错误处理器（null）
     * 同时初始化当前类的上下文对象，用于后续获取图片资源等操作
     */
    public DBUntil(Context context) {
        super(context, databaseName, null, version,null);
        this.context=context;
    }

    /**
     * 数据库首次创建时调用的方法
     * 核心职责：1. 配置数据库参数（如外键约束）；2. 创建所有业务表结构；3. 插入初始化测试数据
     * @param db SQLiteDatabase数据库操作对象
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // 创建数据库前，先关闭外键约束（避免创建表时因外键关联导致的异常，表创建完成后重新开启）
        db.execSQL("PRAGMA foreign_keys = false");

        // ====================== 1. 商家信息表（d_business） ======================
        // 先删除已存在的表（若表存在则删除，避免表结构冲突导致创建失败）
        db.execSQL("drop table if exists d_business");//如果这表存在则删除
        // 创建商家信息表，修正原代码中varchar的拼写错误（varcahr→varchar），新增逻辑删除字段
        db.execSQL("create table d_business(s_id varchar(20) primary key," +// 商家ID，主键（唯一标识商家）
                "s_pwd varchar(20)," +// 商家登录密码（修正：varcahr→varchar）
                "s_name varchar(20)," +// 商家名称（如南京大排档，修正：varcahr→varchar）
                "s_describe varchar(200)," +// 商家描述信息（详细介绍，修正：varcahr→varchar）
                "s_type varchar(20)," +// 商家类型（如餐饮店，修正：varcahr→varchar）
                "s_img varchar(255)," +// 商家头像图片的本地存储路径
                "s_is_delete INTEGER DEFAULT " + NOT_DELETED + ")");// 新增：逻辑删除字段，默认值为未注销（0）

        // 生成商家头像的唯一文件名（通过图片工具类获取，确保文件名不重复）
        String shop= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        // 将应用内置的商家图片（R.drawable.njdpd）保存到本地路径，用于初始化商家头像
        FileImgUntil.saveSystemImgToPath(context,R.drawable.njdpd,shop);

        // 插入商家初始化测试数据，补充逻辑删除字段（默认未注销）
        db.execSQL("INSERT INTO d_business (s_id, s_pwd, s_name, s_describe, s_type, s_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"admin", "123456", "南京大排档", "南京大排档始创于1994年，三十年如一日磨出饮食佳境。全国上百家直营门店，地道金陵味。如有问题，请随时联系本店，定会竭力为您解决。", "餐饮店", shop, NOT_DELETED});

        // ====================== 2. 用户信息表（d_user） ======================
        // 先删除已存在的表（若表存在则删除，避免表结构冲突导致创建失败）
        db.execSQL("drop table if exists d_user");//如果这表存在则删除
        // 创建用户信息表，修正原代码中varchar的拼写错误（varcahr→varchar），新增逻辑删除字段
        db.execSQL("create table d_user(s_id varchar(20) primary key," +// 用户ID，主键（唯一标识用户）
                "s_pwd varchar(20)," +// 用户登录密码（修正：varcahr→varchar）
                "s_name varchar(20)," +// 用户姓名（修正：varcahr→varchar）
                "s_sex varchar(200)," +// 用户性别（修正：varcahr→varchar）
                "s_address varchar(200)," +// 用户默认收货地址（修正：varcahr→varchar）
                "s_phone varchar(20)," +// 用户联系电话（修正：varcahr→varchar）
                "s_img varchar(255)," +// 用户头像图片的本地存储路径
                "s_is_delete INTEGER DEFAULT " + NOT_DELETED + ")");// 新增：逻辑删除字段，默认值为未注销（0）

        // 生成用户头像的唯一文件名（通过图片工具类获取，确保文件名不重复）
        String user= FileImgUntil.getImgName();//获取一个存储图片的路径名字
        // 将应用内置的用户图片（R.drawable.zs）保存到本地路径，用于初始化用户头像
        FileImgUntil.saveSystemImgToPath(context,R.drawable.zs,user);

        // 插入用户初始化测试数据，补充逻辑删除字段（默认未注销）
        db.execSQL("INSERT INTO d_user (s_id, s_pwd, s_name,s_sex, s_address, s_phone, s_img, s_is_delete) " +
                        "VALUES (?, ?, ?,?, ?,?, ?, ?)",
                new Object[]{"test", "123456", "张硕", "男", "徐州市","12312312312", user, NOT_DELETED});

        // ====================== 3. 菜品信息表（d_food） ======================
        // 先删除已存在的表（若表存在则删除，避免表结构冲突导致创建失败）
        db.execSQL("drop table if exists d_food");//如果这表存在则删除
        // 创建菜品信息表，修正原代码中varchar的拼写错误（varcahr→varchar），新增逻辑删除字段
        db.execSQL("create table d_food(s_food_id varchar(20) primary key," +// 菜品ID，主键（唯一标识菜品）
                "s_business_id varchar(20)," +// 所属商家ID（关联d_business表的s_id，修正：varcahr→varchar）
                "s_food_name varchar(20)," +// 菜品名称（修正：varcahr→varchar）
                "s_food_des varchar(200)," +// 菜品描述信息（详细介绍，修正：varcahr→varchar）
                "s_food_price varchar(200)," +// 菜品单价（修正：varcahr→varchar）
                "s_food_img varchar(255)," +// 菜品图片的本地存储路径
                "s_is_delete INTEGER DEFAULT " + NOT_DELETED + ")");// 新增：逻辑删除字段，默认值为未删除（0）

        // 初始化菜品1：南京烤鸭
        String foodImg1= FileImgUntil.getImgName();//获取菜品图片的唯一存储路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.roast_duck,foodImg1);// 保存菜品图片到本地
        // 插入南京烤鸭的初始化数据，补充逻辑删除字段（默认未删除）
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?,?,  ?, ?, ?)",
                new Object[]{"1", "admin", "南京烤鸭", "南京烤鸭香、脆、瞅、鲜、嫩，旺火细烤去除多余水分，配己以特制卤汁细细品味，肉质紧实，尝之把命不喊。", "19", foodImg1, NOT_DELETED});

        // 初始化菜品2：鸭血粉丝汤
        String foodImg2= FileImgUntil.getImgName();//获取菜品图片的唯一存储路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.nanjing_duck_blood_vermicelli_soup,foodImg2);// 保存菜品图片到本地
        // 插入鸭血粉丝汤的初始化数据，补充逻辑删除字段（默认未删除）
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"2", "admin", "鸭血粉丝汤", "南京传统地方风味，鲜嫩的鸭肝、鸭血与劲道的粉丝同煲，鲜美和比。", "24", foodImg2, NOT_DELETED});

        // 初始化菜品3：南京盐水鸭
        String foodImg3= FileImgUntil.getImgName();//获取菜品图片的唯一存储路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.salted_duck,foodImg3);// 保存菜品图片到本地
        // 插入南京盐水鸭的初始化数据，补充逻辑删除字段（默认未删除）
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"3", "admin", "南京盐水鸭", "搓盐、复卤、挂晾、炆煮，十$道工序，一招一式绝不走样，招牌菜来之不易。", "17", foodImg3, NOT_DELETED});

        // 初始化菜品4：金陵汤包
        String foodImg4= FileImgUntil.getImgName();//获取菜品图片的唯一存储路径名字
        FileImgUntil.saveSystemImgToPath(context,R.drawable.nanjing_xiaolongbao,foodImg4);// 保存菜品图片到本地
        // 插入金陵汤包的初始化数据，补充逻辑删除字段（默认未删除）
        db.execSQL("INSERT INTO d_food (s_food_id, s_business_id, s_food_name,s_food_des, s_food_price, s_food_img, s_is_delete) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{"4", "admin", "金陵汤包", "用烤鸭肉制成的小笼包，你吃过吗？本店创制，不可不尝哦。", "12", foodImg4, NOT_DELETED});

        // ====================== 4. 订单主表（d_orders） ======================
        // 先删除已存在的表（若表存在则删除，避免表结构冲突导致创建失败）
        db.execSQL("drop table if exists d_orders");//如果这表存在则删除
        // 创建订单主表，修正原代码中varchar的拼写错误（varcahr→varchar），新增订单状态4（已完成且已评论）
        db.execSQL("create table d_orders(s_order_id varchar(20) primary key," +// 订单ID，主键（唯一标识订单）
                "s_order_time varchar(30)," +// 订单创建时间（格式：yyyy-MM-dd HH:mm:ss，修正：varcahr→varchar）
                "s_business_id varchar(20)," +// 所属商家ID（关联d_business表的s_id，修正：varcahr→varchar）
                "s_user_id varchar(20)," +// 下单用户ID（关联d_user表的s_id，修正：varcahr→varchar）
                "s_order_details_id varchar(30)," +// 关联订单详情表的详情ID（修正：varcahr→varchar）
                "s_order_sta varchar(30)," +// 订单状态：1未处理 2取消 3完成（未评论）4完成且被评论
                "s_order_address varchar(255))");// 订单收货地址（存储详细地址信息）

        // ====================== 5. 订单详情表（d_order_details） ======================
        // 先删除已存在的表（若表存在则删除，避免表结构冲突导致创建失败）
        db.execSQL("drop table if exists d_order_details");//如果这表存在则删除
        // 创建订单详情表，修正原代码中varchar的拼写错误（varcahr→varchar），表结构保持不变
        db.execSQL("create table d_order_details(s_details_id varchar(30)," +// 订单详情ID（关联d_orders表的s_order_details_id）
                "s_food_id varchar(20)," +// 菜品ID（关联d_food表的s_food_id，修正：varcahr→varchar）
                "s_food_name varchar(20)," +// 菜品名称（修正：varcahr→varchar）
                "s_food_des varchar(200)," +// 菜品描述（修正：varcahr→varchar）
                "s_food_price varchar(20)," +// 菜品单价（修正：varcahr→varchar）
                "s_food_num varchar(20)," +// 菜品购买数量（修正：varcahr→varchar）
                "s_food_img varchar(255))");// 菜品图片的本地存储路径

        // ====================== 6. 评论表（d_comments） ======================
        // 先删除已存在的表（若表存在则删除，避免表结构冲突导致创建失败）
        db.execSQL("drop table if exists d_comments");//如果这表存在则删除
        // 创建评论表，修正原代码中varchar的拼写错误（varcahr→varchar），表结构保持不变
        db.execSQL("create table d_comments(s_comment_id varchar(20) primary key," +// 评论ID，主键（唯一标识评论）
                "s_comment_user_id varchar(20)," +// 评论用户ID（关联d_user表的s_id，修正：varcahr→varchar）
                "s_comment_business_id varchar(20)," +// 被评论商家ID（关联d_business表的s_id，修正：varcahr→varchar）
                "s_comment_con varchar(200)," +// 评论内容（修正：varcahr→varchar）
                "s_comment_time varchar(20)," +// 评论创建时间（格式：yyyy-MM-dd HH:mm:ss，修正：varcahr→varchar）
                "s_comment_score varchar(20)," +// 评论评分（如1-5星，修正：varcahr→varchar）
                "s_comment_img varchar(255))");// 评论配图的本地存储路径（可选，可为空）

        // ====================== 7. 收货地址表（d_address） ======================
        // 先删除已存在的表（若表存在则删除，避免表结构冲突导致创建失败）
        db.execSQL("drop table if exists d_address");//如果这表存在则删除
        // 创建收货地址表，修正原代码中varchar的拼写错误（varcahr→varchar），表结构保持不变
        db.execSQL("create table d_address(s_id varchar(20) primary key," +// 地址ID，主键（唯一标识地址）
                "s_user_id varchar(20)," +// 所属用户ID（关联d_user表的s_id，修正：varcahr→varchar）
                "s_user_name varchar(20)," +// 收件人姓名（修正：varcahr→varchar）
                "s_user_address varchar(200)," +// 详细收货地址（修正：varcahr→varchar）
                "s_user_phone varchar(255))");// 收件人联系电话（修正：varcahr→varchar）

        // 所有表创建完成后，重新开启外键约束，确保表间关联关系生效
        db.execSQL("PRAGMA foreign_keys = true");

    }

    /**
     * 数据库版本升级时调用的方法
     * 触发条件：当数据库版本号（newVersion）大于当前已安装的数据库版本号（oldVersion）时触发
     * 核心职责：通过调用onCreate()方法重建所有表结构，确保新的表结构（如订单状态4）和业务配置生效
     * 注意：该方法会清空原有数据，若需保留旧数据，需添加数据迁移逻辑（本次保持原有重建表逻辑不变）
     * @param db SQLiteDatabase数据库操作对象
     * @param oldVersion 旧的数据库版本号
     * @param newVersion 新的数据库版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 版本更新时，重建所有表（保持原有逻辑，确保新状态4生效）
        onCreate(db);
    }
}