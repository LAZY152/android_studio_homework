package com.ccf.feige.orderfood.activity.user.dialog;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.OrderNoFinishIstDetailAdapter;
import com.ccf.feige.orderfood.activity.user.ManageUserBuyActivity;
import com.ccf.feige.orderfood.activity.user.adapter.AddressListAdapter;
import com.ccf.feige.orderfood.activity.user.adapter.UserBuyFoodOrderDetailAdapter;
import com.ccf.feige.orderfood.bean.AddressBean;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AddressDao;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.FoodDao;
import com.ccf.feige.orderfood.dao.OrderDao;
import com.ccf.feige.orderfood.until.Tools;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserBottomDialog  {

    private Context context;

    private ManageUserBuyActivity man;

    private String businessId;

    public  UserBottomDialog (Context context,String businessId){
        man = (ManageUserBuyActivity) context;
        this.context=context;
        this.businessId=businessId;
        init();
    }

    private void init(){

        View bottomSheetLayout =man. getLayoutInflater().inflate(R.layout.user_buy_food_bottom_meu_dialog, null);//找到布局文件
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheetLayout);
        bottomSheetDialog.show();


        UserCommonBean user = AdminDao.getCommonUser(Tools.getOnAccount(context));
        //实现加载头像
        ImageView userTx= bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_img);
        userTx.setImageBitmap(BitmapFactory.decodeFile(user.getsImg()));


        TextView userName=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_name);
        userName.setText(user.getsName());




        TextView userBuyOderTime=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_time);

        Date date1=new Date();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time=sdf1.format(date1);
        userBuyOderTime.setText(time);




        //加载收货信息
        TextView receivePeo=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePeo);
        receivePeo.setText("");

        TextView receiveAddress=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_receiveAddress);
        receiveAddress.setText("");

        TextView receivePhone=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePhone);
        receivePhone.setText("");
        //默认显示为空

       RecyclerView addressRecycle = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_address_list);
       //查询当前用户的地址
        List<AddressBean> addressList = AddressDao.getAllAddressByUserId(Tools.getOnAccount(context));

        AddressListAdapter addressListAdapter=new AddressListAdapter(bottomSheetLayout,addressList);
        addressRecycle.setLayoutManager(new LinearLayoutManager(context));
        if(addressList==null||addressList.size()==0){
            addressRecycle.setAdapter(null);
        }else{
            addressRecycle.setAdapter(addressListAdapter);
        }



        RecyclerView listView = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_food_list);
        //获取所有商品信息
       TextView  buyFoodListT= man.findViewById(R.id.user_buy_businessFood);
       String buyFoodListJSON=buyFoodListT.getText().toString();
        JSONArray jsonArray=JSONArray.parseArray(buyFoodListJSON);

        List<OrderDetailBean> list=new ArrayList<>();// 图片，价格，名称
        for (Object o : jsonArray) {
            JSONObject temp = JSONObject.parseObject(o.toString());
            OrderDetailBean orderDetailBean=new OrderDetailBean();
            orderDetailBean.setFoodId(temp.getString("foodId"));
            orderDetailBean.setFoodQuantity(temp.getString("num"));
            if(temp.getString("num").equals("0")){
                continue;
            }
            FoodBean food = FoodDao.getAllFoodById(temp.getString("foodId"));
            orderDetailBean.setFoodPrice(food.getFoodPrice());
            orderDetailBean.setFoodImage(food.getFoodImg());
            orderDetailBean.setFoodName(food.getFoodName());
            orderDetailBean.setFoodDescription(food.getFoodDes());
            list.add(orderDetailBean);
        }



        UserBuyFoodOrderDetailAdapter de=new UserBuyFoodOrderDetailAdapter(list);
        listView.setLayoutManager(new LinearLayoutManager(context));
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(de);
            de.notifyDataSetChanged();
        }




        TextView businessPrice = man.findViewById(R.id.user_buy_businessPrice);
        TextView sumPrice = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_sumPrice);
        sumPrice.setText(businessPrice.getText().toString());


        Button cancelButton = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });


        Button okButton = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_okOrder);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建订单，向详情表 和订单表插入数据

                //加载收货信息




                if(receivePeo==null||receivePeo.getText().toString().equals("")){
                    Toast.makeText(bottomSheetLayout.getContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                }else
                if(receiveAddress==null||receiveAddress.getText().toString().equals("")){
                    Toast.makeText(bottomSheetLayout.getContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                }else
                if(receivePhone==null||receivePhone.getText().toString().equals("")){
                    Toast.makeText(bottomSheetLayout.getContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                }else{

                    String address=receivePeo.getText().toString()+"-"+receiveAddress.getText().toString()+"-"+receivePhone.getText().toString();

                    String orderId= UUID.randomUUID().toString().replace("-","");//订单ID
                    String orderDetailId= UUID.randomUUID().toString().replace("-","");//订单ID
                    int a=OrderDao.installOrder(orderId,time,businessId,user.getsId(),orderDetailId,"1",address);
                    if(a==1){

                        for (OrderDetailBean orderDetailBean : list) {//所有的订单详细信息
                            orderDetailBean.setDetailsId(orderDetailId);
                            OrderDao.saveOrderDetail(orderDetailBean);
                        }
                        bottomSheetDialog.cancel();
                        Toast.makeText(bottomSheetLayout.getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(bottomSheetLayout.getContext(), "购买失败", Toast.LENGTH_SHORT).show();

                    }




                }





            }
        });







    }


}
