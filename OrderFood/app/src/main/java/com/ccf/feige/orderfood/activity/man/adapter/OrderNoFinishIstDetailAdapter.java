package com.ccf.feige.orderfood.activity.man.adapter;

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
 * 这是RecyclerView的适配器类，用于展示商家未完成订单的商品详情列表
 * 适配RecyclerView，负责将订单商品详情数据（OrderDetailBean）与列表项布局进行绑定，实现数据的可视化展示
 */
public class OrderNoFinishIstDetailAdapter extends RecyclerView.Adapter<OrderNoFinishIstDetailAdapter.OrderViewHolder> {

    // 订单商品详情数据列表，存储待展示的OrderDetailBean实体类集合
    private List<OrderDetailBean> list;

    /**
     * 适配器构造方法，用于初始化订单商品详情数据
     * @param list 订单商品详情数据集合（OrderDetailBean列表），作为适配器的数据源
     */
    public OrderNoFinishIstDetailAdapter(List<OrderDetailBean> list) {
        //super(context, R.layout.list_man_order_no_finish_detail_food_list,list);
        this.list=list;
    }

    /**
     * 创建RecyclerView的视图持有者（ViewHolder），负责加载列表项布局并初始化ViewHolder
     * @param parent 父容器（RecyclerView）
     * @param viewType 列表项视图类型（当前适配器仅一种视图类型）
     * @return 初始化完成的OrderViewHolder，持有列表项的布局控件
     */
    @NonNull
    @Override
    public OrderNoFinishIstDetailAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 从父容器上下文获取布局填充器
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        // 填充列表项布局文件，生成列表项视图，第三个参数设为false（避免自动添加到父容器，由RecyclerView统一管理）
        View convertView = inflater.inflate(R.layout.list_man_order_no_finish_detail_food_list, parent, false);

        // 返回封装了列表项视图的ViewHolder
        return new OrderViewHolder(convertView);
    }

    /**
     * 绑定数据到ViewHolder，将指定位置的商品数据展示到列表项控件上
     * @param holder 视图持有者，持有列表项的所有控件
     * @param position 当前列表项的位置索引
     */
    @Override
    public void onBindViewHolder(@NonNull OrderNoFinishIstDetailAdapter.OrderViewHolder holder, int position) {
        // 获取当前位置对应的订单商品详情实体
        OrderDetailBean tem = list.get(position);

        // 从本地文件路径解码Bitmap，设置到商品图片ImageView
        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(tem.getFoodImage()));
        // 设置商品名称到对应的TextView
        holder.name.setText(tem.getFoodName());
        // 设置商品购买数量到对应的TextView
        holder.num.setText(tem.getFoodQuantity());

        // 初始化BigDecimal对象，用于精确计算（避免浮点型运算精度丢失）
        BigDecimal priceZ=new BigDecimal(tem.getFoodPrice());// 商品单价
        BigDecimal numZ=new BigDecimal(tem.getFoodQuantity());// 商品购买数量

        // 计算商品总价（单价 * 数量），转换为字符串格式
        String jg = priceZ.multiply(numZ).toString();//价格使用的是总价
        // 设置商品总价到对应的TextView
        holder.price.setText(jg);
    }

    /**
     * 获取RecyclerView列表的总项数
     * @return 数据源列表的长度，即订单商品的总数量
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 计算所有订单商品的总价总和
     * @return 所有商品（单价*数量）之和的字符串格式，即订单商品总金额
     */
    public String getSumPrice(){
        // 所有商品数量*价格的总行
        BigDecimal total=new BigDecimal(0);// 初始化总金额为0
        // 遍历所有订单商品详情，累加单个商品总价
        for(OrderDetailBean orderDetailBean:list){
            BigDecimal priceZ=new BigDecimal(orderDetailBean.getFoodPrice());// 单个商品单价
            BigDecimal numZ=new BigDecimal(orderDetailBean.getFoodQuantity());// 单个商品购买数量
            BigDecimal dj = priceZ.multiply(numZ);//价格使用的是总价  DJ=priceZ*numZ（计算单个商品总价）
            total=total.add(dj);// 累加单个商品总价到总金额
            //toal=total+d"
        }
        // 返回总金额的字符串格式
        return  total.toString();
    }

    /**
     * RecyclerView的视图持有者类，用于缓存列表项控件，避免重复findViewById，提升列表滑动性能
     * 静态内部类，减少对外部适配器类的引用，避免内存泄漏
     */
    static class  OrderViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageView;// 商品图片控件
        TextView name;// 商品名称控件
        TextView num;// 商品数量控件
        TextView price;// 商品总价控件

        /**
         * ViewHolder构造方法，初始化列表项中的所有控件
         * @param itemView 列表项的根视图，通过该视图查找子控件
         */
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // 根据控件ID查找并绑定商品图片ImageView
            imageView=itemView.findViewById(R.id.list_man_order_no_finish_detail_list_img);
            // 根据控件ID查找并绑定商品名称TextView
            name=itemView.findViewById(R.id.list_man_order_no_finish_detail_list_name);
            name=itemView.findViewById(R.id.list_man_order_no_finish_detail_list_name);
            // 根据控件ID查找并绑定商品数量TextView
            num=itemView.findViewById(R.id.list_man_order_no_finish_detail_list_num);
            // 根据控件ID查找并绑定商品总价TextView
            price=itemView.findViewById(R.id.list_man_order_no_finish_detail_list_price);
        }
    }
}