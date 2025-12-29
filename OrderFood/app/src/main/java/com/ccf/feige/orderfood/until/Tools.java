package com.ccf.feige.orderfood.until;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类：提供订单查询、SharedPreferences数据获取、数据库游标解析、评分星星展示等通用辅助功能
 * 该类中的方法均为静态方法，无需实例化即可直接调用，用于简化项目中重复的业务逻辑代码
 */
public class Tools {

    /**
     * 获取当前登录账号（从SharedPreferences中读取）
     * @param context 上下文对象，用于获取SharedPreferences实例，不能为null
     * @return 返回当前保存的账号字符串，若未保存过账号则返回默认值"root"
     */
    public static  String getOnAccount(Context context){
        // 获取名为"data"的SharedPreferences实例，模式为私有（仅当前应用可访问）
        SharedPreferences sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        // 从SharedPreferences中读取key为"account"的字符串值，无对应值时使用默认值"root"
        String businessId=sharedPreferences.getString("account","root");//如果这个值没有添加则使用默认的
        return businessId;
    }

    /**
     * 从数据库查询游标（Cursor）中获取指定列名对应的字符串值
     * @param rs 数据库查询返回的游标对象，需确保游标已处于有效位置（非关闭、非空）
     * @param columnName 要获取数据的列名，需与数据库表结构或查询语句中的列名一致
     * @return 返回指定列名对应的字符串数据，若列不存在或无数据则返回null（Cursor自身的返回逻辑）
     */
    @SuppressLint("Range")
    public static String getResultString(Cursor rs, String columnName){
        // 先通过列名获取列索引，再通过列索引从游标中获取对应的字符串值
        return  rs.getString(rs.getColumnIndex(columnName));
    }


    /**
     * 订单列表过滤查询：根据关键字匹配用户名或订单内商品名，筛选出符合条件的订单
     * @param list 原始订单列表（待过滤的完整订单数据）
     * @param query 查询关键字（可以是用户名片段或商品名片段，大小写敏感）
     * @return 返回筛选后的订单列表，仅包含用户名含关键字或包含匹配商品名的订单
     */
    public static List<OrderBean> filterOrder( List<OrderBean> list,String query){
        // 初始化空列表，用于存储筛选后的符合条件的订单
        List<OrderBean> list1=new ArrayList<>();
        // 遍历原始订单列表中的每一个订单
        for (OrderBean orderBean : list) {//判断用户名字是否有内容
            // 第一种匹配：判断当前订单的用户名是否包含查询关键字
            if(orderBean.getUserName().contains(query)){
                // 用户名匹配成功，将该订单加入结果列表
                list1.add(orderBean);
            }else{
                // 用户名不匹配时，获取该订单对应的订单详情列表（包含订单内的商品信息）
                List<OrderDetailBean> list2 = orderBean.getOrderDetailBeanList();//详情表
                // 初始化计数器，用于标记该订单是否有商品匹配关键字
                int a=0;
                // 遍历订单详情列表中的每一个商品
                for (OrderDetailBean orderDetailBean : list2) {//判断商品名字是否有内容
                    // 第二种匹配：判断当前商品名是否包含查询关键字
                    if(orderDetailBean.getFoodName().contains(query)){
                        // 商品名匹配成功，计数器自增（标记该订单符合条件）
                        a++;
                    }
                }
                // 若计数器大于0，说明该订单中有至少一个商品匹配关键字，将该订单加入结果列表
                if(a!=0){
                    list1.add(orderBean);
                }
            }
        }
        // 返回最终筛选后的订单列表
        return  list1;
    }

    /**
     * 动态设置评论星级对应的文字描述（待完善星级图片展示逻辑）
     * 功能说明：根据评分值设置对应的评价文字，后续需补充星星图片的选中/未选中状态展示
     * @param context 视图上下文（承载评分控件的父视图），用于查找布局中的控件
     * @param score 评分值（需为1-5的整数，对应"非常差"到"非常满意"）
     * @param conId 评价文字展示TextView的控件ID
     * @param starId 星级图片ImageView的控件ID数组（长度应与最大评分值一致，此处为5颗星）
     */
    //todo 待写（待完善：根据score设置starId数组中对应图片的显示状态，如选中高亮、未选中灰色）
    public static void setCommentStar(View context,int score,int conId,int starId[]){
        // 根据控件ID从父视图中查找用于展示评价文字的TextView控件
        TextView con=context.findViewById(conId);//显示非常满意的内容
        // 定义评分对应的文字描述数组，索引0-4分别对应评分1-5
        String conT[]={"非常差","差","一般","满意","非常满意"};//代表5个内容
        // 根据评分值（score-1对应数组索引）设置TextView的显示文字
        con.setText(conT[score-1]);
    }
}