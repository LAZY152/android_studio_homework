package com.ccf.feige.orderfood.activity.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.OrderNoFinishIstDetailAdapter;
import com.ccf.feige.orderfood.activity.user.ManageUserBuyActivity;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.bean.UserBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.CommentDao;
import com.ccf.feige.orderfood.dao.FoodDao;

import java.math.BigDecimal;
import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 */
public class UserBuyFoodLIstAdapter extends RecyclerView.Adapter<UserBuyFoodLIstAdapter.UserBuyFoodViewHolder> {

    private List<FoodBean> list;



    private Context contextFather;
    JSONArray jsonArray ;
    public UserBuyFoodLIstAdapter(List<FoodBean> list,Context contextFather) {
        this.list=list;
        this.contextFather=contextFather;
        jsonArray=new JSONArray();//;
        for (FoodBean foodBean : list) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("foodId",foodBean.getFoodId());
            jsonObject.put("num","0");
            jsonArray.add(jsonObject);
        }
    }


    @NonNull
    @Override
    public UserBuyFoodLIstAdapter.UserBuyFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.list_user_buy_food_list, parent, false);



        return new UserBuyFoodLIstAdapter.UserBuyFoodViewHolder(convertView);
    }


    @Override
    public void onBindViewHolder(@NonNull UserBuyFoodLIstAdapter.UserBuyFoodViewHolder holder, int position) {
        FoodBean tem = list.get(position);



        String businessId=tem.getBusinessId();//商家的账号
        UserBean businessUser = AdminDao.getBusinessUser(businessId);//获取商家信息

        //价格
        ManageUserBuyActivity fatherView = (ManageUserBuyActivity) contextFather;
        TextView priceZ = fatherView.findViewById(R.id.user_buy_businessPrice);
        TextView food = fatherView.findViewById(R.id.user_buy_businessFood);//存放购买那些商品
        String foodJson=food.getText().toString();


        if(foodJson.isEmpty()){//代表第一次向里面放东西
            //将所有的商品都放入这个food，这个商家的
            food.setText(jsonArray.toJSONString());
        }











        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numT=holder.numZ.getText().toString();
                int sl=Integer.valueOf(numT)+1;//0
                holder.numZ.setText(String.valueOf(sl));//是数量
                //
                BigDecimal priceB=new BigDecimal(tem.getFoodPrice());//19.86

                BigDecimal sumPriceF=new BigDecimal(priceZ.getText().toString());

                //0  19.8  2*19.8

                BigDecimal jgB = sumPriceF.add(priceB);//计算后的价格
                priceZ.setText(jgB.toString());

                String foodJson=food.getText().toString();//获取存放购买内容
                JSONArray z = JSONArray.parseArray(foodJson);

                JSONArray newJson=new JSONArray();
                for (Object o : z) {
                    JSONObject temJSon = JSONObject.parseObject(o.toString());
                    if(temJSon.get("foodId").equals(tem.getFoodId())){
                        temJSon.put("num",holder.numZ.getText());
                    }
                    newJson.add(temJSon);
                }
                food.setText(newJson.toJSONString());
                Log.d("AAAA",food.getText().toString());


            }
        });

        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numT=holder.numZ.getText().toString();
                int sl=Integer.valueOf(numT)-1;//0
                if(sl>=0){
                    holder.numZ.setText(String.valueOf(sl));
                    BigDecimal sumPriceF=new BigDecimal(priceZ.getText().toString());
                    //
                    BigDecimal priceB=new BigDecimal(tem.getFoodPrice());
                    BigDecimal jgB = sumPriceF.subtract(priceB);//计算后的价格
                    priceZ.setText(jgB.toString());


                    String foodJson=food.getText().toString();//获取存放购买内容
                    JSONArray z = JSONArray.parseArray(foodJson);

                    JSONArray newJson=new JSONArray();
                    for (Object o : z) {
                        JSONObject temJSon = JSONObject.parseObject(o.toString());
                        if(temJSon.get("foodId").equals(tem.getFoodId())){
                            temJSon.put("num",holder.numZ.getText());
                        }
                        newJson.add(temJSon);
                    }
                    food.setText(newJson.toJSONString());
                    Log.d("AAAA",food.getText().toString());
                }

            }
        });




        //统计当前月份的销量

        Bitmap bitmap = BitmapFactory.decodeFile(tem.getFoodImg());
        holder.img.setImageBitmap(bitmap);
        holder.name.setText(tem.getFoodName());
        holder.price.setText("价格:"+tem.getFoodPrice());
        holder.des.setText("描述:"+tem.getFoodDes());


        int saleNum = FoodDao.getMouSalesNum(tem.getFoodId());
        holder.num.setText("月销:"+String.valueOf(saleNum));



    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class  UserBuyFoodViewHolder extends  RecyclerView.ViewHolder{
        ImageView img;
        TextView name;
        TextView num;
        TextView price;
        TextView des;


        ImageView add;
                TextView numZ;
        ImageView sub;

        public UserBuyFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.user_buy_food_list_foodImg);
            name=itemView.findViewById(R.id.user_buy_food_list_name);
            num=itemView.findViewById(R.id.user_buy_food_list_saleNum);//不在咱们的数据当中
            price=itemView.findViewById(R.id.user_buy_food_list_price);
            des=itemView.findViewById(R.id.user_buy_food_list_des);

            add=itemView.findViewById(R.id.user_buy_food_list_add_num);//加
            numZ=itemView.findViewById(R.id.user_buy_food_list_num);//数量
            sub=itemView.findViewById(R.id.user_buy_food_list_sub_num);//减


        }
    }


}
