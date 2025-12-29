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

/**
 * 用户下单确认底部弹窗类
 * 功能：展示订单确认信息（用户信息、收货地址、商品清单、总价），提供取消订单和确认下单的操作
 */
public class UserBottomDialog  {

    // 上下文对象，用于页面相关操作（如加载布局、弹出提示）
    private Context context;

    // 用户购买页面实例，用于获取页面中的商品信息、价格信息等控件数据
    private ManageUserBuyActivity man;

    // 商家ID，用于关联订单对应的商家
    private String businessId;

    /**
     * 构造方法
     * @param context 上下文对象（实际为ManageUserBuyActivity实例）
     * @param businessId 商家ID
     */
    public  UserBottomDialog (Context context,String businessId){
        // 将上下文强制转换为ManageUserBuyActivity，便于后续获取页面控件
        man = (ManageUserBuyActivity) context;
        // 赋值上下文对象
        this.context=context;
        // 赋值商家ID
        this.businessId=businessId;
        // 初始化弹窗（加载布局、绑定控件、填充数据、设置点击事件）
        init();
    }

    /**
     * 弹窗初始化核心方法
     * 负责加载弹窗布局、初始化所有控件、填充各类数据、设置按钮点击事件
     */
    private void init(){
        // 1. 加载底部弹窗布局文件，通过ManageUserBuyActivity的LayoutInflater获取布局视图
        View bottomSheetLayout =man. getLayoutInflater().inflate(R.layout.user_buy_food_bottom_meu_dialog, null);//找到布局文件
        // 2. 创建BottomSheetDialog底部弹窗实例，绑定上下文
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        // 3. 为弹窗设置加载好的布局视图
        bottomSheetDialog.setContentView(bottomSheetLayout);
        // 4. 显示底部弹窗
        bottomSheetDialog.show();

        // 5. 填充用户信息（头像、用户名）
        // 5.1 通过当前登录账号获取用户通用信息（从AdminDao中查询）
        UserCommonBean user = AdminDao.getCommonUser(Tools.getOnAccount(context));
        // 5.2 加载用户头像：找到头像ImageView控件，通过本地文件路径解码为Bitmap并设置
        ImageView userTx= bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_img);
        userTx.setImageBitmap(BitmapFactory.decodeFile(user.getsImg()));

        // 5.3 加载用户名：找到用户名TextView控件，设置用户昵称
        TextView userName=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_name);
        userName.setText(user.getsName());

        // 6. 填充下单时间
        // 6.1 找到时间展示TextView控件
        TextView userBuyOderTime=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_time);
        // 6.2 获取当前系统时间，格式化为"yyyy-MM-dd HH:mm"格式
        Date date1=new Date();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time=sdf1.format(date1);
        // 6.3 设置下单时间到控件上
        userBuyOderTime.setText(time);

        // 7. 初始化收货信息控件（默认显示为空，后续选择地址后更新）
        // 7.1 收货人姓名控件
        TextView receivePeo=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePeo);
        receivePeo.setText("");
        // 7.2 收货地址控件
        TextView receiveAddress=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_receiveAddress);
        receiveAddress.setText("");
        // 7.3 收货人电话控件
        TextView receivePhone=bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePhone);
        receivePhone.setText("");
        //默认显示为空

        // 8. 加载用户收货地址列表（通过RecyclerView展示）
        // 8.1 找到地址列表RecyclerView控件
        RecyclerView addressRecycle = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_address_list);
        // 8.2 从AddressDao中查询当前登录用户的所有收货地址
        List<AddressBean> addressList = AddressDao.getAllAddressByUserId(Tools.getOnAccount(context));
        // 8.3 创建地址列表适配器，绑定布局视图和地址数据
        AddressListAdapter addressListAdapter=new AddressListAdapter(bottomSheetLayout,addressList);
        // 8.4 设置RecyclerView为线性布局（垂直排列）
        addressRecycle.setLayoutManager(new LinearLayoutManager(context));
        // 8.5 根据地址列表是否为空，设置适配器（为空则设为null，否则设置创建好的适配器）
        if(addressList==null||addressList.size()==0){
            addressRecycle.setAdapter(null);
        }else{
            addressRecycle.setAdapter(addressListAdapter);
        }

        // 9. 加载订单商品清单（通过RecyclerView展示）
        // 9.1 找到商品列表RecyclerView控件
        RecyclerView listView = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_food_list);
        // 9.2 从ManageUserBuyActivity页面中获取存储商品信息的TextView（存储的是JSON格式字符串）
        TextView  buyFoodListT= man.findViewById(R.id.user_buy_businessFood);
        String buyFoodListJSON=buyFoodListT.getText().toString();
        // 9.3 解析JSON字符串为JSONArray数组（存储了多个商品的信息）
        JSONArray jsonArray=JSONArray.parseArray(buyFoodListJSON);

        // 9.4 初始化订单详情列表，用于存储商品的订单详情数据（图片、价格、名称、数量等）
        List<OrderDetailBean> list=new ArrayList<>();// 图片，价格，名称
        // 9.5 遍历JSONArray，解析每个商品的信息并封装为OrderDetailBean
        for (Object o : jsonArray) {
            // 将当前对象转为JSONObject，便于获取对应字段值
            JSONObject temp = JSONObject.parseObject(o.toString());
            // 创建订单详情实体类实例
            OrderDetailBean orderDetailBean=new OrderDetailBean();
            // 设置商品ID
            orderDetailBean.setFoodId(temp.getString("foodId"));
            // 设置商品购买数量
            orderDetailBean.setFoodQuantity(temp.getString("num"));
            // 若购买数量为0，跳过当前商品（不加入订单清单）
            if(temp.getString("num").equals("0")){
                continue;
            }
            // 从FoodDao中根据商品ID查询商品完整信息
            FoodBean food = FoodDao.getAllFoodById(temp.getString("foodId"));
            // 封装商品价格到订单详情
            orderDetailBean.setFoodPrice(food.getFoodPrice());
            // 封装商品图片路径到订单详情
            orderDetailBean.setFoodImage(food.getFoodImg());
            // 封装商品名称到订单详情
            orderDetailBean.setFoodName(food.getFoodName());
            // 封装商品描述到订单详情
            orderDetailBean.setFoodDescription(food.getFoodDes());
            // 将封装好的订单详情加入列表
            list.add(orderDetailBean);
        }

        // 9.6 创建商品订单详情适配器，绑定商品订单列表数据
        UserBuyFoodOrderDetailAdapter de=new UserBuyFoodOrderDetailAdapter(list);
        // 9.7 设置RecyclerView为线性布局（垂直排列）
        listView.setLayoutManager(new LinearLayoutManager(context));
        // 9.8 根据商品订单列表是否为空，设置适配器（为空则设为null，否则设置创建好的适配器并刷新数据）
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(de);
            de.notifyDataSetChanged();
        }

        // 10. 填充订单总价
        // 10.1 从ManageUserBuyActivity页面中获取总价控件的文本
        TextView businessPrice = man.findViewById(R.id.user_buy_businessPrice);
        // 10.2 找到弹窗中的总价展示控件
        TextView sumPrice = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_sumPrice);
        // 10.3 将页面中的总价设置到弹窗控件上
        sumPrice.setText(businessPrice.getText().toString());

        // 11. 设置取消按钮点击事件
        Button cancelButton = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击取消，关闭底部弹窗
                bottomSheetDialog.cancel();
            }
        });

        // 12. 设置确认下单按钮点击事件
        Button okButton = bottomSheetLayout.findViewById(R.id.user_buy_food_bottom_meu_dialog_okOrder);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建订单，向订单表和订单详情表插入数据

                // 12.1 校验收货信息是否完整（收货人、地址、电话均不能为空）
                if(receivePeo==null||receivePeo.getText().toString().equals("")){
                    Toast.makeText(bottomSheetLayout.getContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                }else
                if(receiveAddress==null||receiveAddress.getText().toString().equals("")){
                    Toast.makeText(bottomSheetLayout.getContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                }else
                if(receivePhone==null||receivePhone.getText().toString().equals("")){
                    Toast.makeText(bottomSheetLayout.getContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                }else{
                    // 12.2 拼接完整的收货信息（收货人-地址-电话）
                    String address=receivePeo.getText().toString()+"-"+receiveAddress.getText().toString()+"-"+receivePhone.getText().toString();

                    // 12.3 生成唯一的订单ID（去除UUID中的横杠）
                    String orderId= UUID.randomUUID().toString().replace("-","");//订单ID
                    // 12.4 生成唯一的订单详情ID（去除UUID中的横杠）
                    String orderDetailId= UUID.randomUUID().toString().replace("-","");//订单详情ID
                    // 12.5 调用OrderDao的方法插入订单主表数据，返回1表示插入成功
                    int a=OrderDao.installOrder(orderId,time,businessId,user.getsId(),orderDetailId,"1",address);
                    if(a==1){
                        // 12.6 若订单主表插入成功，遍历商品订单详情列表，插入订单详情表数据
                        for (OrderDetailBean orderDetailBean : list) {//所有的订单详细信息
                            // 为每个订单详情设置对应的订单详情ID
                            orderDetailBean.setDetailsId(orderDetailId);
                            // 调用OrderDao的方法保存订单详情数据
                            OrderDao.saveOrderDetail(orderDetailBean);
                        }
                        // 12.7 关闭底部弹窗
                        bottomSheetDialog.cancel();
                        // 12.8 弹出支付成功提示
                        Toast.makeText(bottomSheetLayout.getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                    }else{
                        // 12.9 若订单插入失败，弹出购买失败提示
                        Toast.makeText(bottomSheetLayout.getContext(), "购买失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}