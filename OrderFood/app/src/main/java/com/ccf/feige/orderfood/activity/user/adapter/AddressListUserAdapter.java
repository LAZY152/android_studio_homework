package com.ccf.feige.orderfood.activity.user.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.ManageUserCommentActivity;
import com.ccf.feige.orderfood.activity.user.ManageUserUpdateAddressActivity;
import com.ccf.feige.orderfood.bean.AddressBean;

import java.util.List;

/**
 * 修正注释：这个是用来显示用户收货地址的一个adapter（原注释标注为商家商品，需修正）
 * 功能说明：该类是RecyclerView的适配器，负责将用户收货地址数据（AddressBean）与UI布局进行绑定，
 * 实现收货地址列表的展示、数据更新以及地址编辑的跳转功能，是用户地址列表页面的核心数据适配类。
 */
public class AddressListUserAdapter extends RecyclerView.Adapter< AddressListUserAdapter.AddressViewHolder> {

    // 成员变量：存储用户收货地址数据的列表，作为适配器的数据源
    private List<AddressBean> list;

    /**
     * 适配器构造方法
     * 功能：初始化适配器，传入用户收货地址列表作为初始数据源
     * @param list 初始的用户收货地址列表（List<AddressBean>），包含待展示的地址数据
     */
    public AddressListUserAdapter( List<AddressBean> list) {
        this.list=list;
    }

    // 关键修改1：添加公共数据更新方法，供外部调用刷新列表
    /**
     * 公共数据更新方法
     * 功能：供外部页面调用，实现适配器数据源的更新和RecyclerView列表的UI刷新
     * 解决问题：避免数据叠加重复、处理新列表为空的场景，防止空指针异常，保证列表刷新的有效性
     * @param newList 最新的用户收货地址列表，用于替换适配器原有数据源
     */
    public void updateData(List<AddressBean> newList) {
        // 1. 清空原有数据（避免数据叠加、重复）：先判断原有列表不为null，再执行清空操作
        if (this.list != null) {
            this.list.clear();
        }
        // 2. 更新为最新数据（处理新列表为null的场景，避免空指针）：将新列表赋值给适配器成员变量数据源
        this.list = newList;
        // 3. 通知RecyclerView数据已变更，刷新UI（核心刷新方法）：通知RecyclerView重新绑定数据并绘制列表
        notifyDataSetChanged();
    }

    /**
     * 创建ViewHolder方法（RecyclerView适配器重写方法）
     * 功能：加载地址列表的单个item布局，创建ViewHolder实例并返回，用于承载单个地址的UI控件
     * @param parent 父容器（RecyclerView本身），用于获取布局加载的上下文环境
     * @param viewType item视图类型（当前适配器仅一种布局，该参数暂未使用）
     * @return AddressViewHolder 自定义的ViewHolder实例，持有单个地址item的UI控件引用
     */
    @NonNull
    @Override
    public AddressListUserAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. 获取布局加载器：从父容器的上下文环境中获取LayoutInflater，用于加载布局文件
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        // 2. 加载item布局：将地址列表的单个item布局（list_user_address_user_list）加载为View实例，
        //    第三个参数设为false，表示不立即将该View添加到父容器，由RecyclerView统一管理
        View convertView = inflater.inflate(R.layout.list_user_address_user_list, parent, false);
        // 3. 创建并返回ViewHolder实例：将加载好的item布局传入ViewHolder，完成控件绑定准备
        return new AddressViewHolder(convertView);
    }

    /**
     * 绑定ViewHolder方法（RecyclerView适配器重写方法）
     * 功能：将数据源中指定位置的地址数据，绑定到ViewHolder对应的UI控件上，实现数据与UI的映射，
     * 同时为编辑按钮设置点击事件，实现编辑跳转功能
     * @param holder 已创建的ViewHolder实例，持有单个地址item的UI控件引用
     * @param position 当前item在数据源列表中的索引位置，用于获取对应位置的地址数据
     */
    @Override
    public void onBindViewHolder(@NonNull AddressListUserAdapter.AddressViewHolder holder, int position) {
        // 1. 获取当前位置的地址数据：从数据源列表中根据索引position获取对应的AddressBean实例
        AddressBean tem = list.get(position);

        // 2. 数据与UI控件绑定：将AddressBean中的数据分别设置到对应的TextView上，展示收货人、收货地址、联系电话
        holder.peo.setText(tem.getsUserName());// 绑定收货人姓名
        holder.address.setText(tem.getsUserAddress());// 绑定详细收货地址
        holder.phone.setText(tem.getsUserPhone());// 绑定联系电话

        //实现编辑功能：为编辑按钮设置点击事件监听器，点击后跳转到地址编辑页面
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 创建意图对象：指定跳转的上下文（当前item的上下文）和目标页面（地址编辑页面）
                Intent intent=new Intent(holder.itemView.getContext(), ManageUserUpdateAddressActivity.class);
                // 2. 携带地址数据：将当前点击的地址对象（tem）通过Extra传入目标页面，供编辑页面回显原有数据
                intent.putExtra("address",tem);
                // 3. 启动页面跳转：执行页面跳转，进入地址编辑页面
                holder.itemView.getContext(). startActivity(intent);
            }
        });
    }

    /**
     * 获取列表项总数方法（RecyclerView适配器重写方法）
     * 功能：返回适配器数据源的总条数，供RecyclerView计算列表的总长度和可滚动范围
     * 优化说明：处理list为null的场景，避免空指针异常，当list为null时返回0，表示无数据
     * @return 数据源列表的总条数，有数据返回list.size()，无数据（list为null）返回0
     */
    @Override
    public int getItemCount() {
        // 关键优化：处理list为null的场景，避免空指针异常
        return (list == null) ? 0 : list.size();
    }

    /**
     * 自定义ViewHolder内部类（继承RecyclerView.ViewHolder）
     * 功能：持有地址列表单个item的所有UI控件引用，避免重复查找控件，提高列表绘制的效率，
     * 是RecyclerView优化的核心内部类，与item布局一一对应。
     * 说明：使用static修饰，避免内部类持有外部适配器实例的引用，防止内存泄漏。
     */
    static class  AddressViewHolder extends  RecyclerView.ViewHolder{
        // ViewHolder成员变量：对应item布局中的UI控件，分别承载收货人、地址、电话和编辑按钮
        TextView peo;         // 收货人姓名展示控件
        TextView address;     // 详细收货地址展示控件
        TextView phone;       // 联系电话展示控件
        View itemView;        // 单个item的根布局View
        ImageView edit;       // 地址编辑按钮控件

        /**
         * ViewHolder构造方法
         * 功能：初始化ViewHolder，绑定item布局中的所有UI控件，通过findViewById获取控件引用
         * @param itemView 地址列表的单个item根布局View，用于查找布局内的子控件
         */
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            // 绑定收货人姓名控件：根据控件id查找并赋值
            peo=itemView.findViewById(R.id.list_user_my_address_receivePeo);
            // 绑定详细收货地址控件：根据控件id查找并赋值
            address=itemView.findViewById(R.id.list_user_my_address_receiveAddress);
            // 绑定联系电话控件：根据控件id查找并赋值
            phone=itemView.findViewById(R.id.list_user_my_address_receivePhone);
            // 绑定编辑按钮控件：根据控件id查找并赋值
            edit=itemView.findViewById(R.id.list_user_my_address_edit);
            // 赋值item根布局View
            this.itemView=itemView;
        }
    }
}