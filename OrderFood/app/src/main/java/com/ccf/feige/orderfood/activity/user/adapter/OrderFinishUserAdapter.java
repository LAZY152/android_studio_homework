package com.ccf.feige.orderfood.activity.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.OrderNoFinishIstDetailAdapter;
import com.ccf.feige.orderfood.activity.user.ManageUserCommentActivity;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;

import java.util.List;

/**
 * 用户端已完成订单列表适配器
 * 作用：继承ArrayAdapter，用于绑定订单列表数据与布局，展示用户已完成/各类状态的订单信息
 */
public class OrderFinishUserAdapter extends ArrayAdapter<OrderBean> {

    // 保存订单列表数据，用于后续getView方法中获取对应位置的订单对象
    private final List<OrderBean> list;

    /**
     * 适配器构造方法
     * 移除冗余的context变量（ArrayAdapter自带getContext()方法）
     * @param context 上下文对象，用于加载布局、启动页面等
     * @param list 待展示的订单列表数据（OrderBean集合）
     */
    public OrderFinishUserAdapter(@NonNull Context context, List<OrderBean> list) {
        // 调用父类ArrayAdapter的构造方法，绑定布局文件与数据列表
        // 布局文件：R.layout.list_user_order_finish_list（用户已完成订单列表项布局）
        super(context, R.layout.list_user_order_finish_list, list);
        // 初始化当前适配器的订单列表
        this.list = list;
    }

    /**
     * 重写ArrayAdapter的getView方法，用于构建并返回每个列表项的视图
     * 负责将订单数据与布局控件进行绑定，实现数据的可视化展示
     * @param position 当前列表项的位置（索引）
     * @param convertView 复用的视图对象，用于优化列表性能，减少View的创建次数
     * @param viewGroup 视图容器，即当前列表项的父布局
     * @return 构建完成的当前列表项视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // 判空处理：如果复用视图为null，说明是首次创建该列表项视图，需要手动加载布局
        if (convertView == null) {
            // 获取布局加载器，用于从布局文件加载视图
            LayoutInflater inflater = LayoutInflater.from(getContext());
            // 加载订单列表项布局，绑定到convertView，不自动附加到父布局（第三个参数为false）
            convertView = inflater.inflate(R.layout.list_user_order_finish_list, viewGroup, false);
        }

        // 根据当前列表项位置，从订单列表中获取对应的订单对象
        OrderBean tem = list.get(position);
        // 从订单对象中获取下单用户的ID
        String userId = tem.getUserId();
        // 调用AdminDao的静态方法，根据用户ID查询获取普通用户的信息对象
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);

        // 加载用户头像：绑定布局中的头像ImageView控件，并设置头像图片
        ImageView imageView = convertView.findViewById(R.id.list_user_order_finish_list_img);
        // 从本地文件路径解码bitmap图片，设置到ImageView（commonUser.getsImg()返回用户头像的本地文件路径）
        imageView.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));

        // 绑定并设置下单用户的姓名
        TextView name = convertView.findViewById(R.id.list_user_order_finish_list_name);
        name.setText(commonUser.getsName());
        // 绑定并设置订单的创建时间
        TextView time = convertView.findViewById(R.id.list_user_order_finish_list_time);
        time.setText(tem.getOrderTime());

        // 解析订单地址信息：订单地址以"-"分隔，格式为「收货人-联系电话-详细地址」
        String address[] = tem.getOrderAddress().split("-");
        // 绑定并设置收货人姓名
        TextView receivePeo = convertView.findViewById(R.id.list_user_order_finish_list_receivePeo);
        receivePeo.setText(address[0]);
        // 绑定并设置收货详细地址
        TextView receiveAdderss = convertView.findViewById(R.id.list_user_order_finish_list_receiveAdderss);
        receiveAdderss.setText(address[2]);
        // 绑定并设置收货人联系电话
        TextView phone = convertView.findViewById(R.id.list_user_order_finish_list_receivePhone);
        phone.setText(address[1]);

        // 绑定订单详情列表：展示当前订单中的商品明细（嵌套RecyclerView）
        RecyclerView listDe = convertView.findViewById(R.id.list_user_order_finish_list_foodList);
        // 从当前订单对象中获取订单明细列表（商品列表）
        List<OrderDetailBean> detailList = tem.getOrderDetailBeanList();
        // 创建订单明细适配器，传入商品明细列表
        OrderNoFinishIstDetailAdapter de = new OrderNoFinishIstDetailAdapter(detailList);
        // 为嵌套RecyclerView设置线性布局管理器（垂直排列，与普通列表一致）
        listDe.setLayoutManager(new LinearLayoutManager(getContext()));
        // 为嵌套RecyclerView设置适配器：如果明细列表为空或null，则设置为null，否则设置为创建好的明细适配器
        listDe.setAdapter(detailList == null || detailList.isEmpty() ? null : de);

        // 绑定并设置订单总金额
        // 修复：改用订单详情适配器的getSumPrice()（OrderBean无此方法）
        TextView sumPrice = convertView.findViewById(R.id.list_user_order_finish_list_sumPrice);
        // 从订单明细适配器中获取计算好的订单总金额，并设置到TextView
        sumPrice.setText(de.getSumPrice());

        // 订单状态与评论按钮控制：根据订单状态展示对应的状态文本，并控制评论按钮的显示/隐藏
        Button commentBtn = convertView.findViewById(R.id.list_user_order_finish_list_comment); // 评论按钮
        TextView sta = convertView.findViewById(R.id.list_user_order_finish_list_sta); // 订单状态文本
        String orderStatus = tem.getOrderStatus(); // 获取当前订单的状态码

        // 多分支判断订单状态码，设置对应的状态文本和评论按钮可见性
        if ("1".equals(orderStatus)) {
            // 状态码1：订单待处理
            sta.setText("订单待处理");
            commentBtn.setVisibility(View.GONE); // 隐藏评论按钮
        } else if ("2".equals(orderStatus)) {
            // 状态码2：订单已取消
            sta.setText("订单已取消");
            commentBtn.setVisibility(View.GONE); // 隐藏评论按钮
        } else if ("3".equals(orderStatus)) {
            // 状态码3：订单已完成（未评论）
            sta.setText("订单已完成");
            commentBtn.setVisibility(View.VISIBLE); // 显示评论按钮，允许用户评论
        } else if ("4".equals(orderStatus)) {
            // 状态码4：订单已完成（已评论）
            sta.setText("订单已完成（已评论）");
            commentBtn.setVisibility(View.GONE); // 隐藏评论按钮，已评论无需再次评论
        } else {
            // 未知状态码：容错处理
            sta.setText("订单状态未知");
            commentBtn.setVisibility(View.GONE); // 隐藏评论按钮
        }

        // 评论按钮点击事件：跳转至用户评论页面，允许用户对已完成订单进行评论
        commentBtn.setOnClickListener(v -> {
            // 创建意图对象，指定跳转的目标页面（ManageUserCommentActivity：用户评论管理页面）
            Intent intent = new Intent(getContext(), ManageUserCommentActivity.class);
            // 传递商家ID参数，用于评论对应商家
            intent.putExtra("businessId", tem.getBusinessId());
            // 关键参数：传递订单ID，用于后续评论提交后更新订单状态（标记为已评论）
            intent.putExtra("orderId", tem.getOrderId());
            // 启动目标页面
            getContext().startActivity(intent);
        });

        // 返回构建完成的当前列表项视图，展示在列表中
        return convertView;
    }
}