package com.ccf.feige.orderfood.activity.man.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.OrderDao;

import java.util.List;

/**
 * 商家端已完成/已取消订单列表适配器
 * 继承自ArrayAdapter，用于将订单数据（OrderBean）与订单列表项视图进行绑定
 * 适配的列表项布局为：R.layout.list_man_order_finish_list
 */
public class OrderFinishIstAdapter extends ArrayAdapter<OrderBean> {

    /** 订单数据列表，存储所有需要展示的已完成/已取消订单信息 */
    private List<OrderBean> list;

    /** 上下文对象，用于加载布局、获取资源等操作 */
    private Context context;

    /**
     * 构造方法，初始化适配器并传递必要参数
     * @param context 上下文对象，通常为当前Activity/Fragment
     * @param list 待展示的订单数据列表
     */
    public OrderFinishIstAdapter(@NonNull Context context, List<OrderBean> list) {
        // 调用父类ArrayAdapter的构造方法，指定适配的布局和数据列表
        super(context, R.layout.list_man_order_finish_list,list);
        this.context=context;
        this.list=list;
    }

    /**
     * 重写ArrayAdapter的getView方法，用于创建并绑定每个列表项的视图
     * 实现数据与视图的映射，复用convertView优化列表性能
     * @param position 当前列表项的位置索引
     * @param convertView 可复用的视图对象，用于减少View的创建开销
     * @param viewGroup 列表项视图的父容器（即ListView/GridView）
     * @return 绑定好数据的当前列表项视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        // 判断是否有可复用的视图，若无则加载新的布局文件
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.list_man_order_finish_list,viewGroup,false);
        }

        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");
        // 获取当前位置对应的订单对象
        OrderBean tem = list.get(position);
        // 从订单对象中获取下单用户的ID
        String userId=tem.getUserId();
        // 通过AdminDao查询该用户的普通用户信息
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);//普通用户

        // 找到用户头像ImageView，绑定用户头像（从本地文件解码Bitmap）
        ImageView imageView=convertView.findViewById(R.id.list_man_order_finish_list_img);
        imageView.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));

        // 找到用户名TextView，绑定用户姓名
        TextView name=convertView.findViewById(R.id.list_man_order_finish_list_name);
        name.setText(commonUser.getsName());
        // 找到订单时间TextView，绑定订单创建时间
        TextView time=convertView.findViewById(R.id.list_man_order_finish_list_time);
        time.setText(tem.getOrderTime());

        // 将订单地址按"-"分割，拆分出收件人、电话、详细地址等信息
        String address[]=tem.getOrderAddress().split("-");

        // 找到收件人TextView，绑定拆分后的收件人信息（数组第0位）
        TextView receivePeo=convertView.findViewById(R.id.list_man_order_finish_list_receivePeo);
        receivePeo.setText(address[0]);

        // 找到收件地址TextView，绑定拆分后的详细地址（数组第2位）
        TextView receiveAdderss=convertView.findViewById(R.id.list_man_order_finish_list_receiveAddress);
        receiveAdderss.setText(address[2]);

        // 找到联系电话TextView，绑定拆分后的手机号（数组第1位）
        TextView phone=convertView.findViewById(R.id.list_man_order_finish_list_receivePhone);
        phone.setText(address[1]);

        // 找到订单详情RecyclerView，用于展示该订单下的商品明细
        RecyclerView listDe=convertView.findViewById(R.id.list_man_order_finish_list_foodList);

        // 从当前订单对象中获取订单详情（商品列表）数据
        List<OrderDetailBean> detailList = tem.getOrderDetailBeanList();

        //再加载一个listview
        // 创建订单详情适配器，传入商品明细数据
        OrderNoFinishIstDetailAdapter de=new OrderNoFinishIstDetailAdapter(detailList);

        // 为RecyclerView设置线性布局管理器（纵向排列）
        listDe.setLayoutManager(new LinearLayoutManager(getContext()));
        // 判断订单详情列表是否为空，为空则设置Adapter为null，否则设置并刷新
        if(detailList==null||detailList.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            // 通知详情适配器数据已变更，更新RecyclerView视图
            de.notifyDataSetChanged();
        }

        // 找到订单总金额TextView，绑定详情适配器计算的总金额
        TextView sumPrice=convertView.findViewById(R.id.list_man_order_finish_list_sumPrice);
        sumPrice.setText(de.getSumPrice());

        // 找到订单状态TextView，根据订单状态值设置对应文字描述
        TextView sta=convertView.findViewById(R.id.list_man_order_finish_list_sta);
        if(tem.getOrderStatus().equals("2")){
            // 状态值"2"对应"订单已取消"
            sta.setText("订单已取消");
        }else{
            // 其他状态值对应"订单已完成"
            sta.setText("订单已完成");
        }

        // 返回绑定好所有数据的当前列表项视图
        return convertView;
    }
}