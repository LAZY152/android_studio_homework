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
 */
public class AddressListUserAdapter extends RecyclerView.Adapter< AddressListUserAdapter.AddressViewHolder> {

    private List<AddressBean> list;

    public AddressListUserAdapter( List<AddressBean> list) {
        this.list=list;
    }

    // 关键修改1：添加公共数据更新方法，供外部调用刷新列表
    public void updateData(List<AddressBean> newList) {
        // 1. 清空原有数据（避免数据叠加、重复）
        if (this.list != null) {
            this.list.clear();
        }
        // 2. 更新为最新数据（处理新列表为null的场景，避免空指针）
        this.list = newList;
        // 3. 通知RecyclerView数据已变更，刷新UI（核心刷新方法）
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressListUserAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.list_user_address_user_list, parent, false);
        return new AddressViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressListUserAdapter.AddressViewHolder holder, int position) {
        AddressBean tem = list.get(position);

        holder.peo.setText(tem.getsUserName());
        holder.address.setText(tem.getsUserAddress());
        holder.phone.setText(tem.getsUserPhone());

        //实现编辑功能
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), ManageUserUpdateAddressActivity.class);
                intent.putExtra("address",tem);
                holder.itemView.getContext(). startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // 关键优化：处理list为null的场景，避免空指针异常
        return (list == null) ? 0 : list.size();
    }

    static class  AddressViewHolder extends  RecyclerView.ViewHolder{
        TextView peo;
        TextView address;
        TextView phone;
        View itemView;
        ImageView edit;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            peo=itemView.findViewById(R.id.list_user_my_address_receivePeo);
            address=itemView.findViewById(R.id.list_user_my_address_receiveAddress);
            phone=itemView.findViewById(R.id.list_user_my_address_receivePhone);
            edit=itemView.findViewById(R.id.list_user_my_address_edit);
            this.itemView=itemView;
        }
    }
}