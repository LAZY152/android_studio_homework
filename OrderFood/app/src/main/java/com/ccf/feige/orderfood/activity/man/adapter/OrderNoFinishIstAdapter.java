// 包声明：该适配器类所在的包路径，对应商家端订单相关适配器的存储包
package com.ccf.feige.orderfood.activity.man.adapter;

// 导入Android系统相关控件、上下文、布局等核心类，为适配器提供基础支持
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

// 导入AndroidX相关的RecyclerView组件，用于实现订单详情的列表展示
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// 导入项目自定义的资源类、实体类、数据访问类（DAO），提供业务相关的依赖支持
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.OrderDao;

import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 * 补充注释：该适配器本质为【商家端未完成订单列表适配器】，继承自ArrayAdapter<OrderBean>，
 * 专门用于展示商家端处于未完成状态的订单数据，实现订单信息绑定、订单状态修改等功能
 */
public class OrderNoFinishIstAdapter extends ArrayAdapter<OrderBean> {

    // 成员变量：存储商家端未完成订单数据的集合，承载所有需要展示的订单实体
    private List<OrderBean> list;

    // 成员变量：上下文对象，用于获取布局资源、弹出提示框、初始化控件等操作
    private Context context;

    /**
     * 适配器的构造方法，用于初始化适配器的核心参数
     * @param context 上下文对象，传递自调用该适配器的页面/组件
     * @param list 未完成订单数据集合，包含所有需要展示的OrderBean实体
     */
    public OrderNoFinishIstAdapter(@NonNull Context context, List<OrderBean> list) {
        // 调用父类ArrayAdapter的构造方法，指定列表项的布局文件和订单数据集合
        super(context, R.layout.list_man_order_no_finish_list,list);
        // 初始化当前适配器的上下文对象
        this.context=context;
        // 初始化当前适配器的订单数据集合
        this.list=list;
    }

    /**
     * 重写ArrayAdapter的getView方法，用于创建并返回每个订单列表项的视图
     * 核心功能：绑定订单数据到控件、初始化订单详情列表、设置按钮点击事件
     * @param position  当前列表项在订单集合中的索引位置
     * @param convertView  可复用的视图对象，用于提升列表渲染性能（避免重复创建视图）
     * @param viewGroup  当前列表项视图的父容器（即订单列表的ListView）
     * @return  绑定好所有数据的当前订单列表项视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        // 视图复用判断：如果没有可复用的视图，就从布局文件加载新视图
        if(convertView==null){
            // 获取布局填充器，用于从xml布局文件加载视图对象
            LayoutInflater inflater=LayoutInflater.from(getContext());
            // 加载订单列表项的布局文件，生成视图对象并赋值给convertView
            convertView=inflater.inflate(R.layout.list_man_order_no_finish_list,viewGroup,false);
        }

        // 注释：该行代码为注释预留，未实际执行功能（原代码注释掉的TextView赋值）
        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");

        // 根据当前列表项的索引，从订单集合中获取对应的订单实体对象
        OrderBean tem = list.get(position);
        // 从订单实体中获取下单用户的ID
        String userId=tem.getUserId();
        // 调用AdminDao的静态方法，通过用户ID获取普通用户的详细信息实体
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);//普通用户

        // 初始化用户头像ImageView控件：从当前列表项视图中获取头像控件
        ImageView imageView=convertView.findViewById(R.id.list_man_order_no_finish_list_img);
        // 为头像控件设置图片：通过BitmapFactory解析用户头像文件路径，生成Bitmap并设置
        imageView.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));
        // 注释：开发备注，当前数据中可能缺少用户头像信息，导致头像展示异常

        // 初始化用户名TextView控件：获取用户名展示控件并绑定用户姓名数据
        TextView name=convertView.findViewById(R.id.list_man_order_no_finish_list_name);
        name.setText(commonUser.getsName());

        // 初始化订单时间TextView控件：获取订单时间展示控件并绑定订单创建时间数据
        TextView time=convertView.findViewById(R.id.list_man_order_no_finish_list_time);
        time.setText(tem.getOrderTime());

        // 订单地址处理：将订单地址字符串按"-"分割为字符串数组，拆分收件人、电话、详细地址
        String address[]=tem.getOrderAddress().split("-");

        // 初始化收件人TextView控件：获取收件人展示控件并绑定地址数组中的收件人信息
        TextView receivePeo=convertView.findViewById(R.id.list_man_order_no_finish_list_receivePeo);
        receivePeo.setText(address[0]);

        // 初始化收件地址TextView控件：获取收件地址展示控件并绑定地址数组中的详细地址信息
        TextView receiveAdderss=convertView.findViewById(R.id.list_man_order_no_finish_list_receiveAdderss);
        receiveAdderss.setText(address[2]);

        // 初始化联系电话TextView控件：获取联系电话展示控件并绑定地址数组中的电话信息
        TextView phone=convertView.findViewById(R.id.list_man_order_no_finish_list_receivePhone);
        phone.setText(address[1]);

        // 初始化订单详情RecyclerView控件：获取用于展示订单商品明细的列表控件
        RecyclerView listDe=convertView.findViewById(R.id.list_man_order_no_finish_list_foodList);
        // 注释：原代码重复给联系电话TextView赋值，保持原有代码逻辑，无额外业务意义
        phone.setText(address[1]);

        // 从当前订单实体中获取订单明细数据集合（包含该订单下的所有商品信息）
        List<OrderDetailBean> detailList = tem.getOrderDetailBeanList();

        // 注释：订单详情列表初始化，复用内部适配器展示商品明细
        //再加载一个listview
        // 创建订单详情适配器实例，传入订单明细数据集合
        OrderNoFinishIstDetailAdapter de=new OrderNoFinishIstDetailAdapter(detailList);

        // 为订单详情RecyclerView设置布局管理器：使用线性布局管理器，适配上下文环境
        listDe.setLayoutManager(new LinearLayoutManager(getContext()));

        // 订单详情数据判空处理：如果明细集合为空或长度为0，设置适配器为null
        if(detailList==null||detailList.size()==0){
            listDe.setAdapter(null);
        }else{
            // 明细数据非空时，为RecyclerView设置详情适配器
            listDe.setAdapter(de);
            // 通知详情适配器数据已变更，刷新详情列表展示
            de.notifyDataSetChanged();
        }

        // 初始化订单总价TextView控件：获取总价展示控件并绑定详情适配器计算的总价数据
        TextView sumPrice=convertView.findViewById(R.id.list_man_order_no_finish_list_sumPrice);
        sumPrice.setText(de.getSumPrice());

        // 初始化取消订单按钮：从当前列表项视图中获取取消订单按钮控件
        Button cancel =convertView.findViewById(R.id.list_man_order_no_finish_list_cancelOrder);
        // 为取消订单按钮设置点击事件监听器
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击事件逻辑：调用OrderDao的静态方法，将订单状态更新为"2"（取消状态）
                int a=OrderDao.updateOrderStatus(tem.getOrderId(),"2");
                // 判断订单状态更新是否成功（返回1表示成功）
                if(a==1){
                    // 从订单集合中移除当前位置的订单
                    list.remove(position);
                    // 通知适配器数据已变更，刷新整个订单列表
                    notifyDataSetChanged();
                    // 弹出提示框，告知用户取消订单成功
                    Toast.makeText(getContext(), "取消订单成功", Toast.LENGTH_SHORT).show();
                }else{
                    // 弹出提示框，告知用户取消订单失败
                    Toast.makeText(getContext(), "取消订单失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 初始化完成订单按钮：从当前列表项视图中获取完成订单按钮控件
        Button ok =convertView.findViewById(R.id.list_man_order_no_finish_list_okOrder);
        // 为完成订单按钮设置点击事件监听器
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击事件逻辑：调用OrderDao的静态方法，将订单状态更新为"3"（完成状态）
                int a= OrderDao.updateOrderStatus(tem.getOrderId(),"3");
                // 判断订单状态更新是否成功（返回1表示成功）
                if(a==1){
                    // 从订单集合中移除当前位置的订单
                    list.remove(position);
                    // 通知适配器数据已变更，刷新整个订单列表
                    notifyDataSetChanged();
                    // 弹出提示框，告知用户完成订单成功
                    Toast.makeText(getContext(), "完成订单成功", Toast.LENGTH_SHORT).show();
                }else{
                    // 弹出提示框，告知用户完成订单失败
                    Toast.makeText(getContext(), "完成订单失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 返回绑定好所有数据和事件的当前订单列表项视图
        return convertView;
    }

}