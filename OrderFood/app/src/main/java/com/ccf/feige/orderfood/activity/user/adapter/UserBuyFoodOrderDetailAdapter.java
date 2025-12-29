package com.ccf.feige.orderfood.activity.user.adapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.OrderDetailBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 * 功能说明：RecyclerView适配器，专门用于展示用户购买订单中的商品详情列表
 * 适配载体：RecyclerView，数据模型为OrderDetailBean，布局为list_user_buy_food_order_detail_food_list
 */
public class UserBuyFoodOrderDetailAdapter extends RecyclerView.Adapter<UserBuyFoodOrderDetailAdapter.OrderViewHolder> {

    // 订单详情数据列表，存储所有需要展示的商品订单信息
    private List<OrderDetailBean> list;

    /**
     * 适配器构造方法，用于初始化订单详情数据列表
     * @param list 订单详情数据列表（List<OrderDetailBean>），包含所有待展示的商品订单信息
     */
    public UserBuyFoodOrderDetailAdapter(List<OrderDetailBean> list) {
        //super(context, R.layout.list_man_order_no_finish_detail_food_list,list);
        this.list=list;
    }

    /**
     * 创建ViewHolder视图持有者，用于加载并初始化列表项布局
     * 生命周期说明：在RecyclerView需要新的ViewHolder来展示列表项时调用
     * @param parent 父容器（ViewGroup），即RecyclerView本身
     * @param viewType 列表项视图类型（int），当前适配器仅一种视图类型，暂未使用
     * @return 初始化完成的OrderViewHolder，持有列表项的视图控件引用
     */
    @NonNull
    @Override
    public UserBuyFoodOrderDetailAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 从父容器上下文获取布局填充器，用于加载布局文件
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        // 加载列表项布局文件，生成视图实例，并绑定到父容器（RecyclerView）
        View convertView = inflater.inflate(R.layout.list_user_buy_food_order_detail_food_list, parent, false);

        // 创建并返回ViewHolder，将加载好的布局视图传入
        return new OrderViewHolder(convertView);
    }

    /**
     * 绑定ViewHolder与数据，将对应位置的订单数据填充到列表项控件中
     * 生命周期说明：在RecyclerView将ViewHolder与具体数据绑定（或复用ViewHolder更新数据）时调用
     * @param holder 待绑定数据的OrderViewHolder，持有列表项的所有控件引用
     * @param position 当前列表项的位置索引（int），对应数据列表中的下标
     */
    @Override
    public void onBindViewHolder(@NonNull UserBuyFoodOrderDetailAdapter.OrderViewHolder holder, int position) {
        // 根据位置索引从数据列表中获取对应的订单详情实体类
        OrderDetailBean tem = list.get(position);

        // 从订单详情中获取商品图片路径，通过BitmapFactory解码为Bitmap并设置到ImageView中展示
        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(tem.getFoodImage()));
        // 给商品名称TextView设置当前订单的商品名称
        holder.name.setText(tem.getFoodName());
        // 给商品数量TextView设置当前订单的商品购买数量
        holder.num.setText(tem.getFoodQuantity());

        // 初始化商品单价BigDecimal对象，用于高精度数值计算，避免浮点型误差
        BigDecimal priceZ=new BigDecimal(tem.getFoodPrice());
        // 初始化商品数量BigDecimal对象，用于高精度数值计算
        BigDecimal numZ=new BigDecimal(tem.getFoodQuantity());

        // 计算商品总价（单价 * 数量），并转换为字符串格式
        String jg = priceZ.multiply(numZ).toString();//价格使用的是总价
        // 给商品总价TextView设置计算得到的商品总价
        holder.price.setText(jg);
    }

    /**
     * 获取数据列表的总长度，即RecyclerView的列表项总数
     * @return 数据列表的大小（int），即待展示的订单详情商品数量
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 计算并获取所有商品的总价总和
     * 功能说明：遍历所有订单详情数据，累加每个商品的（单价*数量），得到订单总金额
     * @return 所有商品的总价总和（String格式），基于BigDecimal计算，保证金额精度
     */
    public String getSumPrice(){
        // 初始化订单总价为0，使用BigDecimal保证金额计算精度
        BigDecimal total=new BigDecimal(0);
        // 遍历订单详情数据列表，逐个计算商品总价并累加
        for(OrderDetailBean orderDetailBean:list){
            // 获取当前商品单价并转换为BigDecimal
            BigDecimal priceZ=new BigDecimal(orderDetailBean.getFoodPrice());
            // 获取当前商品数量并转换为BigDecimal
            BigDecimal numZ=new BigDecimal(orderDetailBean.getFoodQuantity());
            // 计算当前商品的总价（单价 * 数量）
            BigDecimal dj = priceZ.multiply(numZ);//价格使用的是总价  DJ=priceZ*numZ
            // 将当前商品总价累加到订单总价格中
            total=total.add(dj);
            //toal=total+d"
        }
        // 将订单总价转换为字符串并返回
        return  total.toString();
    }

    /**
     * 自定义ViewHolder（视图持有者），用于缓存列表项的控件引用，避免重复findViewById，提升性能
     * 说明：继承RecyclerView.ViewHolder，持有列表项布局中的所有需要操作的控件
     */
    static class  OrderViewHolder extends  RecyclerView.ViewHolder{
        // 商品图片展示控件，用于显示商品的本地图片
        ImageView imageView;
        // 商品名称展示控件，用于显示商品的名称
        TextView name;
        // 商品购买数量展示控件，用于显示商品的购买件数
        TextView num;
        // 商品总价展示控件，用于显示商品的（单价*数量）总价
        TextView price;

        /**
         * ViewHolder构造方法，用于初始化控件引用，绑定布局中的控件ID
         * @param itemView 列表项的视图实例（View），即加载完成的list_user_buy_food_order_detail_food_list布局
         */
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // 通过控件ID查找并绑定商品图片ImageView
            imageView=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_img);
            // 通过控件ID查找并绑定商品名称TextView
            name=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_name);
            // 重复绑定商品名称TextView（代码保留原逻辑，未做修改）
            name=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_name);
            // 通过控件ID查找并绑定商品数量TextView
            num=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_num);
            // 通过控件ID查找并绑定商品总价TextView
            price=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_price);
        }
    }
}