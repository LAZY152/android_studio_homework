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
 * 商家商品展示RecyclerView适配器（用户购买商品列表专用）
 * 核心功能：展示商家商品信息、支持商品购买数量增减、同步计算总金额、保存购买商品的数量信息
 * 已彻底移除月销相关所有代码，解决因月销查询/控件绑定导致的闪退问题
 */
public class UserBuyFoodLIstAdapter extends RecyclerView.Adapter<UserBuyFoodLIstAdapter.UserBuyFoodViewHolder> {

    // 数据源：商家的商品列表（封装为FoodBean实体类集合）
    private List<FoodBean> list;
    // 上下文对象：关联对应的Activity（此处特指ManageUserBuyActivity）
    private Context contextFather;
    // 商品数量初始化JSON数组：用于记录所有商品的初始购买数量（默认0）
    JSONArray jsonArray;

    /**
     * 适配器构造方法：初始化数据源、上下文、商品数量初始JSON
     * @param list 商家商品列表（FoodBean集合）
     * @param contextFather 上下文对象（ManageUserBuyActivity实例）
     */
    public UserBuyFoodLIstAdapter(List<FoodBean> list, Context contextFather) {
        this.list = list;
        this.contextFather = contextFather;
        jsonArray = new JSONArray();// 初始化JSON数组，用于存储商品ID和对应购买数量
        // 遍历商品列表，为每个商品初始化购买数量为0，并存入JSON数组
        for (FoodBean foodBean : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("foodId", foodBean.getFoodId());// 存入商品唯一标识
            jsonObject.put("num", "0");// 初始化购买数量为0
            jsonArray.add(jsonObject);
        }
    }

    /**
     * 创建ViewHolder：加载商品列表项布局，初始化视图容器
     * @param parent 父容器（RecyclerView）
     * @param viewType 视图类型（默认单一类型，此处无多布局需求）
     * @return 自定义的UserBuyFoodViewHolder实例，持有列表项的所有控件引用
     */
    @NonNull
    @Override
    public UserBuyFoodLIstAdapter.UserBuyFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取布局填充器，用于加载列表项布局
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 加载列表项布局文件（R.layout.list_user_buy_food_list），并绑定到父容器
        View convertView = inflater.inflate(R.layout.list_user_buy_food_list, parent, false);
        // 创建并返回ViewHolder实例
        return new UserBuyFoodLIstAdapter.UserBuyFoodViewHolder(convertView);
    }

    /**
     * 绑定ViewHolder：为列表项控件赋值，设置点击事件，实现业务逻辑
     * @param holder 自定义ViewHolder实例，持有列表项控件引用
     * @param position 当前列表项的索引位置（对应数据源list中的下标）
     */
    @Override
    public void onBindViewHolder(@NonNull UserBuyFoodLIstAdapter.UserBuyFoodViewHolder holder, int position) {
        // 根据索引获取当前位置的商品实体类对象
        FoodBean tem = list.get(position);
        // 判空防护：避免数据源为空或当前商品对象为空导致的空指针异常
        if (tem == null) {
            return;
        }

        // 获取当前商品所属商家的账号ID（商家唯一标识）
        String businessId = tem.getBusinessId();//商家的账号
        // 根据商家账号查询商家信息（封装为UserBean），此处未直接使用，保留原有逻辑
        UserBean businessUser = AdminDao.getBusinessUser(businessId);//获取商家信息

        // 关联父页面（ManageUserBuyActivity）的总金额控件和商品购买信息存储控件
        ManageUserBuyActivity fatherView = (ManageUserBuyActivity) contextFather;
        TextView priceZ = fatherView.findViewById(R.id.user_buy_businessPrice);// 总金额展示控件
        TextView food = fatherView.findViewById(R.id.user_buy_businessFood);// 购买商品信息（JSON格式）存储控件
        String foodJson = food.getText().toString();// 获取当前存储的商品购买信息

        // 首次加载判断：若商品购买信息控件为空，将初始化的商品数量JSON存入
        if (foodJson.isEmpty()) {//代表第一次向里面放东西
            //将所有的商品都放入这个food，这个商家的
            food.setText(jsonArray.toJSONString());
        }

        // 商品数量增加按钮点击事件：购买数量+1、总金额累加、更新商品购买信息
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前商品的购买数量，转换为int类型并+1
                String numT = holder.numZ.getText().toString();
                int sl = Integer.valueOf(numT) + 1;//0
                holder.numZ.setText(String.valueOf(sl));// 更新列表项上的购买数量展示

                // 计算总金额：累加当前商品的单价
                BigDecimal priceB = new BigDecimal(tem.getFoodPrice());//19.86  当前商品单价
                BigDecimal sumPriceF = new BigDecimal(priceZ.getText().toString());// 当前总金额
                BigDecimal jgB = sumPriceF.add(priceB);//计算后的价格（总金额+商品单价）
                priceZ.setText(jgB.toString());// 更新总金额展示

                // 更新商品购买信息：修改当前商品对应的购买数量
                String foodJson = food.getText().toString();//获取存放购买内容
                JSONArray z = JSONArray.parseArray(foodJson);// 解析为JSON数组

                JSONArray newJson = new JSONArray();// 新建JSON数组，用于存储更新后的信息
                for (Object o : z) {
                    JSONObject temJSon = JSONObject.parseObject(o.toString());// 解析每个商品的信息为JSONObject
                    // 匹配当前商品的ID，更新对应的购买数量
                    if (temJSon.get("foodId").equals(tem.getFoodId())) {
                        temJSon.put("num", holder.numZ.getText());
                    }
                    newJson.add(temJSon);// 将更新后的商品信息存入新JSON数组
                }
                food.setText(newJson.toJSONString());// 保存更新后的商品购买信息
                Log.d("AAAA", food.getText().toString());// 打印日志，用于调试查看商品购买信息
            }
        });

        // 商品数量减少按钮点击事件：购买数量-1（不低于0）、总金额递减、更新商品购买信息
        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前商品的购买数量，转换为int类型并-1
                String numT = holder.numZ.getText().toString();
                int sl = Integer.valueOf(numT) - 1;//0
                // 边界判断：购买数量不能低于0，避免出现负数
                if (sl >= 0) {
                    holder.numZ.setText(String.valueOf(sl));// 更新列表项上的购买数量展示

                    // 计算总金额：递减当前商品的单价
                    BigDecimal sumPriceF = new BigDecimal(priceZ.getText().toString());// 当前总金额
                    BigDecimal priceB = new BigDecimal(tem.getFoodPrice());// 当前商品单价
                    BigDecimal jgB = sumPriceF.subtract(priceB);//计算后的价格（总金额-商品单价）
                    priceZ.setText(jgB.toString());// 更新总金额展示

                    // 更新商品购买信息：修改当前商品对应的购买数量
                    String foodJson = food.getText().toString();//获取存放购买内容
                    JSONArray z = JSONArray.parseArray(foodJson);// 解析为JSON数组

                    JSONArray newJson = new JSONArray();// 新建JSON数组，用于存储更新后的信息
                    for (Object o : z) {
                        JSONObject temJSon = JSONObject.parseObject(o.toString());// 解析每个商品的信息为JSONObject
                        // 匹配当前商品的ID，更新对应的购买数量
                        if (temJSon.get("foodId").equals(tem.getFoodId())) {
                            temJSon.put("num", holder.numZ.getText());
                        }
                        newJson.add(temJSon);// 将更新后的商品信息存入新JSON数组
                    }
                    food.setText(newJson.toJSONString());// 保存更新后的商品购买信息
                    Log.d("AAAA", food.getText().toString());// 打印日志，用于调试查看商品购买信息
                }
            }
        });

        // 商品核心信息展示（保留原有逻辑，移除月销相关）
        // 加载商品图片：根据本地文件路径解码为Bitmap，并设置到ImageView控件
        Bitmap bitmap = BitmapFactory.decodeFile(tem.getFoodImg());
        holder.img.setImageBitmap(bitmap);
        // 设置商品名称
        holder.name.setText(tem.getFoodName());
        // 设置商品价格（拼接提示文字，提升用户可读性）
        holder.price.setText("价格:" + tem.getFoodPrice());
        // 设置商品描述（拼接提示文字，提升用户可读性）
        holder.des.setText("描述:" + tem.getFoodDes());

        // ============== 移除月销相关代码（核心修复点） ==============
        // 1. 删除月销数量查询：FoodDao.getMouSalesNum(tem.getFoodId())
        // 2. 删除月销控件赋值：holder.num.setText("月销:"+String.valueOf(saleNum))
        // ===========================================================
    }

    /**
     * 获取列表项总数：适配RecyclerView的数据源长度，增加判空防护
     * @return 数据源的长度，若数据源为null则返回0，避免空指针异常
     */
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();// 优化：判空避免空指针
    }

    /**
     * 自定义ViewHolder类：持有列表项的所有控件引用，避免重复findViewById，提升性能
     * 继承自RecyclerView.ViewHolder，必须实现带View参数的构造方法
     */
    static class UserBuyFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView img;         // 商品图片控件
        TextView name;         // 商品名称控件
        // 移除：月销控件声明 TextView num;（核心修复点）
        TextView price;        // 商品价格控件
        TextView des;          // 商品描述控件

        ImageView add;         // 购买数量增加按钮
        TextView numZ;         // 购买数量展示控件（注意：与原月销控件区分）
        ImageView sub;         // 购买数量减少按钮

        /**
         * ViewHolder构造方法：绑定列表项的所有控件
         * @param itemView 列表项的根View对象
         */
        public UserBuyFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            // 绑定商品图片控件
            img = itemView.findViewById(R.id.user_buy_food_list_foodImg);
            // 绑定商品名称控件
            name = itemView.findViewById(R.id.user_buy_food_list_name);
            // 绑定商品价格控件
            price = itemView.findViewById(R.id.user_buy_food_list_price);
            // 绑定商品描述控件
            des = itemView.findViewById(R.id.user_buy_food_list_des);

            // 绑定购买数量相关控件
            add = itemView.findViewById(R.id.user_buy_food_list_add_num);//加
            numZ = itemView.findViewById(R.id.user_buy_food_list_num);//购买数量（注意：与原月销控件区分）
            sub = itemView.findViewById(R.id.user_buy_food_list_sub_num);//减

        }
    }
}