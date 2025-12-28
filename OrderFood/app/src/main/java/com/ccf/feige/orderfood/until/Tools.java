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

public class Tools {

    /**
     * 获取当前账号
     * @param context
     * @return
     */
    public static  String getOnAccount(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String businessId=sharedPreferences.getString("account","root");//如果这个值没有添加则使用默认的
        return businessId;
    }

    /**
     * 获取结果集当中指定行的内容
     * @param rs
     * @param columnName
     * @return
     */

    @SuppressLint("Range")
    public static String getResultString(Cursor rs, String columnName){

        return  rs.getString(rs.getColumnIndex(columnName));
    }


    /**
     * 输入列表和名字，实现查询功能
     * @param list
     * @param query
     * @return
     */
    public static List<OrderBean> filterOrder( List<OrderBean> list,String query){
        List<OrderBean> list1=new ArrayList<>();
        for (OrderBean orderBean : list) {//判断用户名字是否有内容
            if(orderBean.getUserName().contains(query)){
                list1.add(orderBean);
            }else{
                List<OrderDetailBean> list2 = orderBean.getOrderDetailBeanList();//详情表
                int a=0;
                for (OrderDetailBean orderDetailBean : list2) {//判断商品名字是否有内容
                    if(orderDetailBean.getFoodName().contains(query)){
                        a++;
                    }
                }
                if(a!=0){
                    list1.add(orderBean);
                }


            }
        }
        return  list1;
    }

    /**
     * 动态的显示星星
     * @param context
     * @param score
     */
    //todo 待写
    public static void setCommentStar(View context,int score,int conId,int starId[]){

       TextView con=context.findViewById(conId);//显示非常满意的内容
        String conT[]={"非常差","差","一般","满意","非常满意"};//代笔5个内容
        con.setText(conT[score-1]);


    }
}
