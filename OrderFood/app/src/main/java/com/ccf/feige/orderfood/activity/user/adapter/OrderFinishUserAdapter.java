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

public class OrderFinishUserAdapter extends ArrayAdapter<OrderBean> {

    private final List<OrderBean> list;

    // 移除冗余的context变量（ArrayAdapter自带getContext()方法）
    public OrderFinishUserAdapter(@NonNull Context context, List<OrderBean> list) {
        super(context, R.layout.list_user_order_finish_list, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_user_order_finish_list, viewGroup, false);
        }

        OrderBean tem = list.get(position);
        String userId = tem.getUserId();
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);

        // 加载用户头像
        ImageView imageView = convertView.findViewById(R.id.list_user_order_finish_list_img);
        imageView.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));

        // 绑定用户信息
        TextView name = convertView.findViewById(R.id.list_user_order_finish_list_name);
        name.setText(commonUser.getsName());
        TextView time = convertView.findViewById(R.id.list_user_order_finish_list_time);
        time.setText(tem.getOrderTime());

        // 解析地址信息
        String address[] = tem.getOrderAddress().split("-");
        TextView receivePeo = convertView.findViewById(R.id.list_user_order_finish_list_receivePeo);
        receivePeo.setText(address[0]);
        TextView receiveAdderss = convertView.findViewById(R.id.list_user_order_finish_list_receiveAdderss);
        receiveAdderss.setText(address[2]);
        TextView phone = convertView.findViewById(R.id.list_user_order_finish_list_receivePhone);
        phone.setText(address[1]);

        // 绑定订单详情列表
        RecyclerView listDe = convertView.findViewById(R.id.list_user_order_finish_list_foodList);
        List<OrderDetailBean> detailList = tem.getOrderDetailBeanList();
        OrderNoFinishIstDetailAdapter de = new OrderNoFinishIstDetailAdapter(detailList);
        listDe.setLayoutManager(new LinearLayoutManager(getContext()));
        listDe.setAdapter(detailList == null || detailList.isEmpty() ? null : de);

        // 修复：改用订单详情适配器的getSumPrice()（OrderBean无此方法）
        TextView sumPrice = convertView.findViewById(R.id.list_user_order_finish_list_sumPrice);
        sumPrice.setText(de.getSumPrice());

        // 订单状态与评论按钮控制
        Button commentBtn = convertView.findViewById(R.id.list_user_order_finish_list_comment);
        TextView sta = convertView.findViewById(R.id.list_user_order_finish_list_sta);
        String orderStatus = tem.getOrderStatus();

        if ("1".equals(orderStatus)) {
            sta.setText("订单待处理");
            commentBtn.setVisibility(View.GONE);
        } else if ("2".equals(orderStatus)) {
            sta.setText("订单已取消");
            commentBtn.setVisibility(View.GONE);
        } else if ("3".equals(orderStatus)) {
            sta.setText("订单已完成");
            commentBtn.setVisibility(View.VISIBLE);
        } else if ("4".equals(orderStatus)) {
            sta.setText("订单已完成（已评论）");
            commentBtn.setVisibility(View.GONE);
        } else {
            sta.setText("订单状态未知");
            commentBtn.setVisibility(View.GONE);
        }

        // 评论按钮跳转（补充订单ID参数）
        commentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ManageUserCommentActivity.class);
            intent.putExtra("businessId", tem.getBusinessId());
            intent.putExtra("orderId", tem.getOrderId()); // 关键参数：用于更新订单状态
            getContext().startActivity(intent);
        });

        return convertView;
    }
}