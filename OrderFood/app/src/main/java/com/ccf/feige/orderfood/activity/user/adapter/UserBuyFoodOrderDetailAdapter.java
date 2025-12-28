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
import com.ccf.feige.orderfood.bean.OrderDetailBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 */
public class UserBuyFoodOrderDetailAdapter extends RecyclerView.Adapter<UserBuyFoodOrderDetailAdapter.OrderViewHolder> {


    private List<OrderDetailBean> list;

    public UserBuyFoodOrderDetailAdapter(List<OrderDetailBean> list) {
        //super(context, R.layout.list_man_order_no_finish_detail_food_list,list);
        this.list=list;
    }

    @NonNull
    @Override
    public UserBuyFoodOrderDetailAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.list_user_buy_food_order_detail_food_list, parent, false);



        return new OrderViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserBuyFoodOrderDetailAdapter.OrderViewHolder holder, int position) {
        OrderDetailBean tem = list.get(position);

        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(tem.getFoodImage()));
        holder.name.setText(tem.getFoodName());
        holder.num.setText(tem.getFoodQuantity());



        BigDecimal priceZ=new BigDecimal(tem.getFoodPrice());
        BigDecimal numZ=new BigDecimal(tem.getFoodQuantity());

        String jg = priceZ.multiply(numZ).toString();//价格使用的是总价
        holder.price.setText(jg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    /**
     * 获取商品总结
     * @return
     */
    public String getSumPrice(){
        //所有商品数量*价格的总行
        BigDecimal total=new BigDecimal(0);
        for(OrderDetailBean orderDetailBean:list){
            BigDecimal priceZ=new BigDecimal(orderDetailBean.getFoodPrice());
            BigDecimal numZ=new BigDecimal(orderDetailBean.getFoodQuantity());
            BigDecimal dj = priceZ.multiply(numZ);//价格使用的是总价  DJ=priceZ*numZ
            total=total.add(dj);
            //toal=total+d"
        }
        return  total.toString();

    }




    static class  OrderViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;
        TextView num;
        TextView price;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_img);
            name=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_name);
            name=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_name);
            num=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_num);
            price=itemView.findViewById(R.id.list_user_buy_food_order_detail_food_list_price);

        }
    }





}
