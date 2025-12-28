package com.ccf.feige.orderfood.activity.man.adapter;

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
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.OrderDao;

import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 */
public class OrderFinishIstAdapter extends ArrayAdapter<OrderBean> {


    private List<OrderBean> list;

    private Context context;


    public OrderFinishIstAdapter(@NonNull Context context, List<OrderBean> list) {
        super(context, R.layout.list_man_order_finish_list,list);
        this.context=context;
        this.list=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.list_man_order_finish_list,viewGroup,false);
        }

        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");
        OrderBean tem = list.get(position);
        String userId=tem.getUserId();
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);//普通用户


        ImageView imageView=convertView.findViewById(R.id.list_man_order_finish_list_img);
        imageView.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));
        //需要加载用户的头像，但是这读取的数据当中没事用户的头像这就很尴尬


        TextView name=convertView.findViewById(R.id.list_man_order_finish_list_name);
        name.setText(commonUser.getsName());
        TextView time=convertView.findViewById(R.id.list_man_order_finish_list_time);
        time.setText(tem.getOrderTime());


         String address[]=tem.getOrderAddress().split("-");


        TextView receivePeo=convertView.findViewById(R.id.list_man_order_finish_list_receivePeo);
        receivePeo.setText(address[0]);

        TextView receiveAdderss=convertView.findViewById(R.id.list_man_order_finish_list_receiveAddress);
        receiveAdderss.setText(address[2]);

        TextView phone=convertView.findViewById(R.id.list_man_order_finish_list_receivePhone);
        phone.setText(address[1]);

        RecyclerView listDe=convertView.findViewById(R.id.list_man_order_finish_list_foodList);
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



        TextView sumPrice=convertView.findViewById(R.id.list_man_order_finish_list_sumPrice);
        sumPrice.setText(de.getSumPrice());


        TextView sta=convertView.findViewById(R.id.list_man_order_finish_list_sta);
        if(tem.getOrderStatus().equals("2")){
            sta.setText("订单已取消");
        }else{
            sta.setText("订单已完成");
        }








        return convertView;
    }



}
