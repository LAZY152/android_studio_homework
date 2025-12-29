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
 * 作用：为Android列表控件（如ListView）提供数据绑定和视图展示，专门展示商家的商品信息列表
 */
public class UserFoodLIstAdapter extends ArrayAdapter<FoodBean> {

    // 保存商品数据列表，用于适配展示
    private List<FoodBean> list;

    // 保存上下文对象，用于获取布局、启动页面等操作
    private Context context;

    /**
     * 构造方法：初始化用户商品列表适配器
     * @param context 上下文对象（通常是Activity或Fragment）
     * @param list 商品数据列表，包含需要展示的所有FoodBean对象
     */
    public UserFoodLIstAdapter(@NonNull Context context, List<FoodBean> list) {
        // 调用父类构造方法，指定默认的列表项布局和数据列表
        super(context, R.layout.list_user_food_list,list);
        // 赋值当前上下文，供后续方法使用
        this.context=context;
        // 赋值商品数据列表，供后续视图绑定使用
        this.list=list;
    }

    /**
     * 重写父类方法：获取并绑定列表项的视图（核心方法，用于展示每个列表项的内容）
     * @param position 当前列表项的位置（从0开始）
     * @param convertView 可复用的视图对象（用于优化列表性能，避免重复创建视图）
     * @param viewGroup 视图容器（当前列表项的父容器，即ListView）
     * @return 绑定好数据的列表项视图，用于在列表中展示
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        // 判空处理：如果没有可复用的视图，就创建新的视图
        if(convertView==null){
            // 获取布局填充器，用于将xml布局转换为Java视图对象
            LayoutInflater inflater=LayoutInflater.from(getContext());
            // 填充列表项布局（list_user_food_list），并赋值给convertView，同时指定父容器
            convertView=inflater.inflate(R.layout.list_user_food_list,viewGroup,false);
        }

        // 注释掉的代码：用于测试或临时设置文本内容（当前未启用）
        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");

        // 根据当前列表项位置，从数据列表中获取对应的商品对象
        FoodBean tem = list.get(position);

        // 查找商品相关的视图控件（从convertView中获取，对应布局中的控件id）
        ImageView img=convertView.findViewById(R.id.user_food_list_foodImg); // 商品图片控件
        TextView name=convertView.findViewById(R.id.user_food_list_name);   // 商品名称控件
        TextView price=convertView.findViewById(R.id.user_food_list_price); // 商品价格控件
        TextView des=convertView.findViewById(R.id.user_food_list_des);     // 商品描述控件

        // 查找商家相关的视图控件（从convertView中获取，对应布局中的控件id）
        ImageView bImg=convertView.findViewById(R.id.user_food_list_businessTx);  // 商家头像控件
        TextView bName=convertView.findViewById(R.id.user_food_list_businessName);// 商家名称控件
        TextView bPf=convertView.findViewById(R.id.user_food_list_businessPf);    // 商家评分控件

        // 从当前商品对象中获取商家账号（用于关联查询商家信息）
        String businessId=tem.getBusinessId();//商家的账号
        // 调用AdminDao的方法，根据商家账号查询获取商家的完整信息
        UserBean businessUser = AdminDao.getBusinessUser(businessId);//获取商家信息
        // 解析商家头像文件路径，转换为Bitmap对象，并设置到商家头像控件中展示
        bImg.setImageBitmap(BitmapFactory.decodeFile(businessUser.getsImg()));

        // 将商家名称设置到对应的文本控件中展示
        bName.setText(businessUser.getsName());
        // 评分需要计算 获取所有订单分数计算平均分
        // 调用CommentDao的方法，查询该商家的平均评分
        String pfZ=CommentDao.getAvgScoreBusiness(businessId);
        // 将商家平均评分拼接成字符串，设置到对应的文本控件中展示
        bPf.setText(pfZ+" 分");

        // 解析商品图片文件路径，转换为Bitmap对象
        Bitmap bitmap = BitmapFactory.decodeFile(tem.getFoodImg());
        // 将商品图片设置到对应的图片控件中展示
        img.setImageBitmap(bitmap);
        // 将商品名称设置到对应的文本控件中展示
        name.setText(tem.getFoodName());
        // 将商品价格拼接成字符串，设置到对应的文本控件中展示
        price.setText("价格:"+tem.getFoodPrice());
        // 将商品描述拼接成字符串，设置到对应的文本控件中展示
        des.setText("描述:"+tem.getFoodDes());

        // 为当前列表项视图设置点击事件监听器
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时创建意图对象，用于跳转到用户购买商品页面（ManageUserBuyActivity）
                Intent intent=new Intent(getContext(), ManageUserBuyActivity.class);
                // 携带商家信息对象到目标页面（通过Extra传递序列化/可序列化对象）
                intent.putExtra("business",businessUser);
                // 启动目标页面，执行页面跳转
                getContext().startActivity(intent);
            }
        });

        // 返回绑定好所有数据的列表项视图，展示在列表中
        return convertView;
    }
}