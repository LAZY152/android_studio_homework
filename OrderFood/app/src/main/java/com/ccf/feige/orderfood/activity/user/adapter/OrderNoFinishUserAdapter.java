package com.ccf.feige.orderfood.activity.user.adapter;

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
import com.ccf.feige.orderfood.activity.man.adapter.OrderNoFinishIstDetailAdapter;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.OrderDao;

import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 * 说明：该适配器继承自ArrayAdapter，专门用于展示用户未完成的订单列表
 * 适配的布局为R.layout.list_user_order_no_finish_list，数据类型为OrderBean
 */
public class OrderNoFinishUserAdapter extends ArrayAdapter<OrderBean> {

    // 保存用户未完成订单的数据列表
    private List<OrderBean> list;

    // 保存上下文对象，用于加载布局、显示吐司等操作
    private Context context;

    /**
     * 适配器构造方法，用于初始化适配器核心参数
     * @param context 上下文对象，通常为当前Activity或Fragment
     * @param list 用户未完成订单的数据列表，数据源
     */
    public OrderNoFinishUserAdapter(@NonNull Context context, List<OrderBean> list) {
        // 调用父类ArrayAdapter的构造方法，指定布局资源和数据源
        super(context, R.layout.list_user_order_no_finish_list,list);
        // 赋值上下文对象，供后续方法使用
        this.context=context;
        // 赋值订单数据列表，供后续方法使用
        this.list=list;
    }

    /**
     * 重写ArrayAdapter的getView方法，用于创建并填充每个列表项的视图
     * 该方法会在列表项需要显示时被调用，负责将数据绑定到对应的UI控件上
     * @param position 当前列表项在数据列表中的索引位置
     * @param convertView 复用的视图对象，用于优化列表性能，减少布局加载次数
     * @param viewGroup 父容器视图，即当前列表项的所属父布局
     * @return 填充好数据的当前列表项视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        // 判断复用视图是否为空，为空则重新加载布局，不为空则直接复用
        if(convertView==null){
            // 获取布局填充器，用于将xml布局转换为View对象
            LayoutInflater inflater=LayoutInflater.from(getContext());
            // 加载当前订单列表项的布局，转换为View对象并赋值给convertView
            convertView=inflater.inflate(R.layout.list_user_order_no_finish_list,viewGroup,false);
        }

        // 注释：原本用于测试的文本控件赋值代码，现已注释保留
        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");

        // 根据当前列表项的索引，从数据列表中获取对应的订单对象
        OrderBean tem = list.get(position);
        // 从当前订单对象中获取用户ID
        String userId=tem.getUserId();
        // 调用AdminDao的静态方法，根据用户ID查询普通用户的信息对象
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);//普通用户

        // 找到布局中的用户头像ImageView控件
        ImageView imageView=convertView.findViewById(R.id.list_user_order_no_finish_list_img);
        // 根据用户信息中的图片路径，解码为Bitmap并设置到头像控件中
        imageView.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));
        // 注释：开发中的疑问记录，用户头像数据缺失的问题
        //需要加载用户的头像，但是这读取的数据当中没事用户的头像这就很尴尬

        // 找到布局中的用户名TextView控件
        TextView name=convertView.findViewById(R.id.list_user_order_no_finish_list_name);
        // 将用户信息中的用户名设置到对应的文本控件上
        name.setText(commonUser.getsName());

        // 找到布局中的订单时间TextView控件
        TextView time=convertView.findViewById(R.id.list_user_order_no_finish_list_time);
        // 将当前订单的创建时间设置到对应的文本控件上
        time.setText(tem.getOrderTime());

        // 将订单中的地址字符串按照"-"进行分割，拆分出收件人、电话、详细地址等信息
        String address[]=tem.getOrderAddress().split("-");

        // 找到布局中的收件人TextView控件
        TextView receivePeo=convertView.findViewById(R.id.list_user_order_no_finish_list_receivePeo);
        // 将拆分后的地址数组第0位（收件人）设置到对应的文本控件上
        receivePeo.setText(address[0]);

        // 找到布局中的详细地址TextView控件
        TextView receiveAdderss=convertView.findViewById(R.id.list_user_order_no_finish_list_receiveAdderss);
        // 将拆分后的地址数组第2位（详细地址）设置到对应的文本控件上
        receiveAdderss.setText(address[2]);

        // 找到布局中的收件电话TextView控件
        TextView phone=convertView.findViewById(R.id.list_user_order_no_finish_list_receivePhone);
        // 将拆分后的地址数组第1位（联系电话）设置到对应的文本控件上
        phone.setText(address[1]);

        // 找到布局中的订单详情RecyclerView控件，用于展示当前订单的商品明细
        RecyclerView listDe=convertView.findViewById(R.id.list_user_order_no_finish_list_foodList);
        // 注：此处为重复代码，保留原代码未修改，再次给电话控件赋值
        phone.setText(address[1]);

        // 从当前订单对象中获取订单明细数据列表（包含商品信息、数量、价格等）
        List<OrderDetailBean> detailList = tem.getOrderDetailBeanList();

        // 注释：开发中的思路记录，需要嵌套展示订单明细列表
        //再加载一个listview

        // 创建订单明细适配器对象，传入订单明细数据列表
        OrderNoFinishIstDetailAdapter de=new OrderNoFinishIstDetailAdapter(detailList);

        // 为订单详情RecyclerView设置布局管理器（线性布局管理器，纵向排列）
        listDe.setLayoutManager(new LinearLayoutManager(getContext()));

        // 判断订单明细列表是否为空或长度为0
        if(detailList==null||detailList.size()==0){
            // 若明细列表为空，设置RecyclerView的适配器为null，不展示内容
            listDe.setAdapter(null);
        }else{
            // 若明细列表不为空，设置RecyclerView的适配器为创建好的明细适配器
            listDe.setAdapter(de);
            // 通知明细适配器数据已更新，刷新明细列表展示
            de.notifyDataSetChanged();
        }

        // 找到布局中的订单总金额TextView控件
        TextView sumPrice=convertView.findViewById(R.id.list_user_order_no_finish_list_sumPrice);
        // 调用明细适配器的方法，获取订单总金额并设置到对应的文本控件上
        sumPrice.setText(de.getSumPrice());

        // 找到布局中的取消订单Button控件
        Button cancel =convertView.findViewById(R.id.list_user_order_no_finish_list_cancelOrder);
        // 为取消订单按钮设置点击事件监听器
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击事件逻辑：调用OrderDao的静态方法，将该订单的状态更新为"2"（取消状态）
                // 参数1：当前订单的ID；参数2：要更新的订单状态值
                int a=OrderDao.updateOrderStatus(tem.getOrderId(),"2");

                // 判断订单状态更新是否成功（返回值1表示成功，其他表示失败）
                if(a==1){
                    // 若更新成功，从本地数据列表中移除该订单
                    list.remove(position);
                    // 通知适配器数据已发生变化，刷新整个订单列表
                    notifyDataSetChanged();
                    // 显示吐司提示，告知用户取消订单成功
                    Toast.makeText(getContext(), "取消订单成功", Toast.LENGTH_SHORT).show();
                }else{
                    // 若更新失败，显示吐司提示，告知用户取消订单失败
                    Toast.makeText(getContext(), "取消订单失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 返回填充好所有数据的当前列表项视图，展示在界面上
        return convertView;
    }
}