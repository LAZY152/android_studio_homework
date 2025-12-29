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
 * 商家食品列表适配器
 * 作用：继承自ArrayAdapter，用于将食品数据集合（List<FoodBean>）与列表视图进行绑定，
 * 实现商家端食品列表的展示，每个列表项对应一个食品信息
 */
public class FoodLIstAdapter extends ArrayAdapter<FoodBean> {

    /** 存储商家食品数据的集合，承载所有需要展示的食品信息 */
    private List<FoodBean> list;

    /** 上下文对象，用于加载布局、启动页面等Android组件交互操作 */
    private Context context;

    /**
     * 适配器构造方法，用于初始化适配器并传递核心参数
     * @param context 上下文对象，通常为对应的Activity或Fragment
     * @param list 食品数据集合，包含所有需要展示的食品实体信息
     */
    public FoodLIstAdapter(@NonNull Context context,List<FoodBean> list) {
        // 调用父类ArrayAdapter的构造方法，指定列表项布局和数据集合
        super(context, R.layout.list_man_food_list,list);
        // 赋值上下文对象，供后续方法使用
        this.context=context;
        // 赋值食品数据集合，供后续视图绑定使用
        this.list=list;
    }

    /**
     * 重写ArrayAdapter的getView方法，核心作用是创建并绑定列表项的视图，实现数据与视图的映射
     * @param position 当前列表项的位置（索引），从0开始
     * @param convertView 复用的视图对象，用于优化列表性能，避免重复创建视图
     * @param viewGroup 列表视图的父容器（ListView/GridView等）
     * @return 绑定好数据的当前列表项视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        // 视图复用判断：如果convertView为null，说明没有可复用的视图，需要重新加载布局
        if(convertView==null){
            // 获取布局加载器，用于将xml布局转换为Java视图对象
            LayoutInflater inflater=LayoutInflater.from(getContext());
            // 加载列表项布局（list_man_food_list），并绑定到父容器，不自动添加到父视图
            convertView=inflater.inflate(R.layout.list_man_food_list,viewGroup,false);
        }

        // 注释：原代码预留的测试代码，未启用
        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");

        // 根据当前列表项位置，从数据集合中获取对应的食品实体对象
        FoodBean tem = list.get(position);

        // 从列表项视图中获取对应的控件对象，用于后续设置控件内容
        // 食品图片展示控件
        ImageView img=convertView.findViewById(R.id.man_food_list_foodImg);
        // 食品名称展示控件
        TextView name=convertView.findViewById(R.id.man_food_list_name);
        // 食品价格展示控件
        TextView price=convertView.findViewById(R.id.man_food_list_price);
        // 食品描述展示控件
        TextView des=convertView.findViewById(R.id.man_food_list_des);

        // 从食品实体中获取图片路径，解析为Bitmap位图对象
        Bitmap bitmap = BitmapFactory.decodeFile(tem.getFoodImg());
        // 将解析后的位图设置到图片控件中，展示食品图片
        img.setImageBitmap(bitmap);
        // 给名称控件设置当前食品的名称
        name.setText(tem.getFoodName());
        // 给价格控件设置格式化后的食品价格（前缀+具体价格）
        price.setText("价格:"+tem.getFoodPrice());
        // 给描述控件设置格式化后的食品描述（前缀+具体描述内容）
        des.setText("描述:"+tem.getFoodDes());

        // 给当前列表项设置点击事件监听
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击后创建意图，跳转到食品修改页面（ManageManUpdateFoodActivity）
                Intent intent=new Intent(getContext(), ManageManUpdateFoodActivity.class);
                // 将当前食品实体对象通过Intent传递到修改页面，供修改页面获取原有食品信息
                intent.putExtra("food",tem);
                // 启动修改页面，执行页面跳转
                getContext().startActivity(intent);
            }
        });

        // 返回绑定好数据的列表项视图，展示在列表中
        return convertView;
    }
}