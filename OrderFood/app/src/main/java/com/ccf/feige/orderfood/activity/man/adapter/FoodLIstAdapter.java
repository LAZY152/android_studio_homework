package com.ccf.feige.orderfood.activity.man.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.ManageManActivity;
import com.ccf.feige.orderfood.activity.man.ManageManAddFoodActivity;
import com.ccf.feige.orderfood.activity.man.ManageManUpdateFoodActivity;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.dao.FoodDao;

import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 */
public class FoodLIstAdapter extends ArrayAdapter<FoodBean> {

    private List<FoodBean> list;

    private Context context;


    public FoodLIstAdapter(@NonNull Context context,List<FoodBean> list) {
        super(context, R.layout.list_man_food_list,list);
        this.context=context;
        this.list=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.list_man_food_list,viewGroup,false);
        }

        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");
        FoodBean tem = list.get(position);

        ImageView img=convertView.findViewById(R.id.man_food_list_foodImg);
        TextView name=convertView.findViewById(R.id.man_food_list_name);
        TextView num=convertView.findViewById(R.id.man_food_list_saleNum);//不在咱们的数据当中
        TextView price=convertView.findViewById(R.id.man_food_list_price);
        TextView des=convertView.findViewById(R.id.man_food_list_des);

        //统计当前月份的销量

        Bitmap bitmap = BitmapFactory.decodeFile(tem.getFoodImg());
        img.setImageBitmap(bitmap);
        name.setText(tem.getFoodName());
        price.setText("价格:"+tem.getFoodPrice());
        des.setText("描述:"+tem.getFoodDes());


        int saleNum = FoodDao.getMouSalesNum(tem.getFoodId());
        num.setText("月销:"+String.valueOf(saleNum));


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ManageManUpdateFoodActivity.class);
                intent.putExtra("food",tem);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }



}
