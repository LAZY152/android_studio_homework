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
 */
public class AddressListAdapter extends RecyclerView.Adapter< AddressListAdapter.AddressViewHolder> {


    private List<AddressBean> list;

    private View parent;

    public AddressListAdapter(View parent ,List<AddressBean> list) {
        this.list=list;
        this.parent=parent;
    }

    @NonNull
    @Override
    public AddressListAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.list_user_address_list, parent, false);



        return new AddressViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressListAdapter.AddressViewHolder holder, int position) {
        AddressBean tem = list.get(position);

        holder.peo.setText(tem.getsUserName());
        holder.address.setText(tem.getsUserAddress());
        holder.phone.setText(tem.getsUserPhone());


        //加载收货信息
        TextView receivePeo=parent.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePeo);


        TextView receiveAddress=parent.findViewById(R.id.user_buy_food_bottom_meu_dialog_receiveAddress);


        TextView receivePhone=parent.findViewById(R.id.user_buy_food_bottom_meu_dialog_receivePhone);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receivePeo.setText(tem.getsUserName());
                receiveAddress.setText(tem.getsUserAddress());
                receivePhone.setText(tem.getsUserPhone());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }




    static class  AddressViewHolder extends  RecyclerView.ViewHolder{

        TextView peo;
        TextView address;
        TextView phone;

        View itemView;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            peo=itemView.findViewById(R.id.list_user_address_receivePeo);
            address=itemView.findViewById(R.id.list_user_address_receiveAddress);
            phone=itemView.findViewById(R.id.list_user_address_receivePhone);
            this.itemView=itemView;

        }
    }





}
