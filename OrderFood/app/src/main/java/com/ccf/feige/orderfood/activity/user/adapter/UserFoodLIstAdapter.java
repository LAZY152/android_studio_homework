package com.ccf.feige.orderfood.activity.user.adapter;

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
import com.ccf.feige.orderfood.activity.man.ManageManUpdateFoodActivity;
import com.ccf.feige.orderfood.activity.user.ManageUserActivity;
import com.ccf.feige.orderfood.activity.user.ManageUserBuyActivity;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.bean.UserBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.dao.CommentDao;
import com.ccf.feige.orderfood.dao.FoodDao;

import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 */
public class UserFoodLIstAdapter extends ArrayAdapter<FoodBean> {

    private List<FoodBean> list;

    private Context context;


    public UserFoodLIstAdapter(@NonNull Context context, List<FoodBean> list) {
        super(context, R.layout.list_user_food_list,list);
        this.context=context;
        this.list=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.list_user_food_list,viewGroup,false);
        }

        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");
        FoodBean tem = list.get(position);

        ImageView img=convertView.findViewById(R.id.user_food_list_foodImg);
        TextView name=convertView.findViewById(R.id.user_food_list_name);
        TextView num=convertView.findViewById(R.id.user_food_list_saleNum);//不在咱们的数据当中
        TextView price=convertView.findViewById(R.id.user_food_list_price);
        TextView des=convertView.findViewById(R.id.user_food_list_des);


        ImageView bImg=convertView.findViewById(R.id.user_food_list_businessTx);
        TextView bName=convertView.findViewById(R.id.user_food_list_businessName);
        TextView bPf=convertView.findViewById(R.id.user_food_list_businessPf);


        String businessId=tem.getBusinessId();//商家的账号
        UserBean businessUser = AdminDao.getBusinessUser(businessId);//获取商家信息
        bImg.setImageBitmap(BitmapFactory.decodeFile(businessUser.getsImg()));

        bName.setText(businessUser.getsName());
        //评分需要计算 获取所有订单分数计算平局分
        String pfZ=CommentDao.getAvgScoreBusiness(businessId);
        bPf.setText(pfZ+" 分");


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
                Intent intent=new Intent(getContext(), ManageUserBuyActivity.class);
                intent.putExtra("business",businessUser);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }



}
