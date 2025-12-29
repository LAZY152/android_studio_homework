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
import com.ccf.feige.orderfood.bean.AddressBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 * （注：实际功能为收货地址列表展示适配器，用于RecyclerView加载和展示用户的收货地址数据）
 */
public class AddressListAdapter extends RecyclerView.Adapter< AddressListAdapter.AddressViewHolder> {

    // 存储收货地址数据的列表，数据源核心
    private List<AddressBean> list;

    // 外部父视图，用于获取收货信息展示的目标TextView
    private View parent;

    /**
     * 适配器构造方法
     * @param parent 外部父视图，用于查找需要更新的收货信息控件
     * @param list 收货地址数据源列表
     */
    public AddressListAdapter(View parent ,List<AddressBean> list) {
        this.list=list;
        this.parent=parent;
    }

    /**
     * 创建ViewHolder，加载地址列表项布局并初始化ViewHolder
     * @param parent 父容器ViewGroup
     * @param viewType 视图类型（当前适配器无多视图类型，默认值）
     * @return 封装了列表项视图的AddressViewHolder
     */
    @NonNull
    @Override
    public AddressListAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取布局加载器，从父容器上下文获取
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        // 加载地址列表项布局，绑定到父容器（false表示不立即添加到父容器，由RecyclerView管理）
        View convertView = inflater.inflate(R.layout.list_user_address_list, parent, false);

        // 返回新创建的ViewHolder，持有列表项视图
        return new AddressViewHolder(convertView);
    }

    /**
     * 绑定ViewHolder，为列表项设置数据，并设置点击事件
     * @param holder 要绑定数据的AddressViewHolder
     * @param position 当前列表项的位置索引
     */
    @Override
    public void onBindViewHolder(@NonNull AddressListAdapter.AddressViewHolder holder, int position) {
        // 根据位置获取对应的收货地址数据对象
        AddressBean tem = list.get(position);

        // 为ViewHolder中的控件设置对应数据（收货人姓名、收货地址、联系电话）
        holder.peo.setText(tem.getsUserName());
        holder.address.setText(tem.getsUserAddress());
        holder.phone.setText(tem.getsUserPhone());

        // 从父视图中查找收货信息展示的目标TextView（确认订单等场景展示选中的收货信息）
        TextView receivePeo=parent.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePeo);
        TextView receiveAddress=parent.findViewById(R.id.user_buy_food_bottom_meu_dialog_receiveAddress);
        TextView receivePhone=parent.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePhone);

        // 为当前列表项设置点击事件，点击后更新父视图中的收货信息
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将当前选中的地址数据更新到父视图的对应TextView中
                receivePeo.setText(tem.getsUserName());
                receiveAddress.setText(tem.getsUserAddress());
                receivePhone.setText(tem.getsUserPhone());
            }
        });

    }

    /**
     * 获取列表项总数，即数据源列表的大小
     * @return 收货地址列表的条目数量
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 地址列表项的ViewHolder，用于缓存列表项中的控件，提升RecyclerView性能
     * 静态内部类，避免持有外部Adapter的引用，防止内存泄漏
     */
    static class  AddressViewHolder extends  RecyclerView.ViewHolder{

        // 收货人姓名展示控件
        TextView peo;
        // 收货地址展示控件
        TextView address;
        // 联系电话展示控件
        TextView phone;
        // 列表项根视图
        View itemView;

        /**
         * ViewHolder构造方法，初始化控件引用
         * @param itemView 列表项根视图
         */
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            // 从列表项视图中查找并绑定控件
            peo=itemView.findViewById(R.id.list_user_address_receivePeo);
            address=itemView.findViewById(R.id.list_user_address_receiveAddress);
            phone=itemView.findViewById(R.id.list_user_address_receivePhone);
            this.itemView=itemView;
        }
    }
}