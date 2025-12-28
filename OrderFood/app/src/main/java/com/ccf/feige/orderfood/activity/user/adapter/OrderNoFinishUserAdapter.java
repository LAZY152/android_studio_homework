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
 */
public class OrderNoFinishUserAdapter extends ArrayAdapter<OrderBean> {


    private List<OrderBean> list;

    private Context context;


    public OrderNoFinishUserAdapter(@NonNull Context context, List<OrderBean> list) {
        super(context, R.layout.list_user_order_no_finish_list,list);
        this.context=context;
        this.list=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.list_user_order_no_finish_list,viewGroup,false);
        }

        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");
        OrderBean tem = list.get(position);
        String userId=tem.getUserId();
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);//普通用户


        ImageView imageView=convertView.findViewById(R.id.list_user_order_no_finish_list_img);
        imageView.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));
        //需要加载用户的头像，但是这读取的数据当中没事用户的头像这就很尴尬


        TextView name=convertView.findViewById(R.id.list_user_order_no_finish_list_name);
        name.setText(commonUser.getsName());
        TextView time=convertView.findViewById(R.id.list_user_order_no_finish_list_time);
        time.setText(tem.getOrderTime());


         String address[]=tem.getOrderAddress().split("-");


        TextView receivePeo=convertView.findViewById(R.id.list_user_order_no_finish_list_receivePeo);
        receivePeo.setText(address[0]);

        TextView receiveAdderss=convertView.findViewById(R.id.list_user_order_no_finish_list_receiveAdderss);
        receiveAdderss.setText(address[2]);

        TextView phone=convertView.findViewById(R.id.list_user_order_no_finish_list_receivePhone);
        phone.setText(address[1]);

        RecyclerView listDe=convertView.findViewById(R.id.list_user_order_no_finish_list_foodList);
        phone.setText(address[1]);

        List<OrderDetailBean> detailList = tem.getOrderDetailBeanList();

        //再加载一个listview
        OrderNoFinishIstDetailAdapter de=new OrderNoFinishIstDetailAdapter(detailList);

        listDe.setLayoutManager(new LinearLayoutManager(getContext()));
        if(detailList==null||detailList.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();
        }



        TextView sumPrice=convertView.findViewById(R.id.list_user_order_no_finish_list_sumPrice);
        sumPrice.setText(de.getSumPrice());


        Button cancel =convertView.findViewById(R.id.list_user_order_no_finish_list_cancelOrder);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将状态改为2
                int a=OrderDao.updateOrderStatus(tem.getOrderId(),"2");
                if(a==1){
                    list.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "取消订单成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "取消订单失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }



}
