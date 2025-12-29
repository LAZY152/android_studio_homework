package com.ccf.feige.orderfood.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.frament.ManageHomeFragment;
import com.ccf.feige.orderfood.activity.man.frament.ManageMyFragment;
import com.ccf.feige.orderfood.activity.user.dialog.UserBottomDialog;
import com.ccf.feige.orderfood.activity.user.frament.UserBuyFoodBusinessCommentFragment;
import com.ccf.feige.orderfood.activity.user.frament.UserBuyFoodFBusinessFragment;
import com.ccf.feige.orderfood.activity.user.frament.UserHomeFragment;
import com.ccf.feige.orderfood.bean.UserBean;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户购买界面
 * 该Activity对应商家的用户点餐页面，包含商家信息展示、点餐/评论切换、商品结算等功能
 */
public class ManageUserBuyActivity extends AppCompatActivity {

    /**
     * 页面创建时的初始化方法，完成视图绑定、数据初始化、事件绑定等核心操作
     * @param savedInstanceState 保存的页面状态信息，用于页面重建时恢复数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前Activity对应的布局文件
        setContentView(R.layout.activity_manage_user_buy);

        //实现一个返回的功能
        // 绑定页面顶部的Toolbar控件，用于实现自定义导航栏
        Toolbar toolbar = findViewById(R.id.user_buy_bar);
        // 将当前Toolbar设置为Activity的ActionBar，替代系统默认导航栏
        setSupportActionBar(toolbar);

        //返回有两种，采用跳转和管理
        // 给Toolbar的返回按钮设置点击事件，实现返回上一级页面的功能
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用系统默认的返回按键逻辑，返回上一个Activity
                onBackPressed();
            }
        });

        // 获取从上一个页面传递过来的Intent对象，用于接收携带的商家数据
        Intent intent = getIntent();
        // 从Intent中获取序列化的商家信息对象（UserBean），key为"business"
        UserBean business =(UserBean) intent.getSerializableExtra("business");

        //绑定商家头像ImageView并设置图片
        // 绑定商家头像展示的ImageView控件
        ImageView businessImg = findViewById(R.id.user_buy_businessTx);
        // 根据商家对象中的图片路径，解码本地图片文件并设置为ImageView的显示内容
        businessImg.setImageBitmap(BitmapFactory.decodeFile(business.getsImg()));

        // 绑定商家名称展示的TextView控件
        TextView businessName = findViewById(R.id.user_buy_businessName);
        // 将商家名称设置到TextView中进行展示
        businessName.setText(business.getsName());

        // 绑定商家简介展示的TextView控件
        TextView businessDes = findViewById(R.id.user_buy_businessDes);
        // 拼接简介前缀和商家实际简介内容，设置到TextView中展示
        businessDes.setText("简介: "+business.getsDescribe());

        //实现显示数量（注：此处注释与功能不匹配，实际为展示订单总价）
        // 绑定订单总价展示的TextView控件
        TextView price =this.findViewById(R.id.user_buy_businessPrice);
        // 初始化订单总价为0.00
        price.setText("0.00");

        // 绑定用于存储已选商品JSON数据的TextView控件（该控件通常为不可见，仅用于数据缓存）
        TextView foodJson =this.findViewById(R.id.user_buy_businessFood);

        // 绑定结算按钮控件，并设置点击事件监听器
        Button buy =this.findViewById(R.id.user_buy_businessBuy_con);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 获取缓存的已选商品JSON字符串
                String foodJsonT = foodJson.getText().toString();
                // 2. 使用fastjson2将JSON字符串解析为JSONArray数组
                JSONArray jsonArray=JSONArray.parseArray(foodJsonT);

                // 3. 创建一个列表，用于存放实际选中（数量>0）的商品信息
                List<JSONObject> buyFoodList=new ArrayList<>();//存放所有购买商品的列表
                // 4. 遍历JSONArray中的所有商品对象
                for (Object o : jsonArray) {
                    // 将遍历到的对象转换为JSONObject，方便获取属性值
                    JSONObject temp = JSONObject.parseObject(o.toString());
                    // 5. 判断商品数量是否不等于"0"，筛选出真正被选中的商品
                    if(!temp.get("num").equals("0")){
                        // 6. 将选中的商品添加到购买列表中
                        buyFoodList.add(temp);
                    }
                }

                // 7. 校验是否可以进行结算
                // 第一种情况：商品JSON字符串为空（未选择任何商品）
                if(foodJsonT.isEmpty()){
                    // 弹出Toast提示，告知用户无法结算
                    Toast.makeText(ManageUserBuyActivity.this, "未选择商品无法结算", Toast.LENGTH_SHORT).show();
                }else if(buyFoodList.size()==0){
                    // 第二种情况：商品JSON字符串非空，但所有商品数量均为0（未真正选中商品）
                    Toast.makeText(ManageUserBuyActivity.this, "未选择商品无法结算", Toast.LENGTH_SHORT).show();
                }else{
                    // 8. 校验通过，创建并显示底部结算弹窗，传入当前商家ID
                    UserBottomDialog userBottomDialog=new UserBottomDialog(ManageUserBuyActivity.this,business.getsId());
                }
            }
        });

        //点击结算按钮之后的事情（注：此处为注释占位，实际逻辑已在上述点击事件中实现）

        // 绑定页面中的TabLayout控件，用于实现页面标签切换
        TabLayout tabLayout = findViewById(R.id.user_buy_tab);
        // 绑定页面中的ViewPager2控件，用于实现碎片（Fragment）的左右滑动切换
        ViewPager2 viewPager = findViewById(R.id.user_buy_pager);

        // 给ViewPager2设置适配器，用于管理Fragment的创建和销毁
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            /**
             * 根据页面位置创建对应的Fragment实例
             * @param position 页面索引位置（从0开始）
             * @return 对应位置的Fragment实例
             */
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // 0号位置：创建点餐页面Fragment，并传入商家ID初始化
                if(position==0){
                    return UserBuyFoodFBusinessFragment.newInstance(business.getsId());
                }else{
                    // 1号位置：创建评论页面Fragment，并传入商家ID初始化
                    return new UserBuyFoodBusinessCommentFragment(business.getsId());
                }
            }

            /**
             * 获取ViewPager2的页面总数（即Fragment的数量）
             * @return 页面总数，此处为2（点餐、评论两个页面）
             */
            @Override
            public int getItemCount() {
                return 2;
            }
        });

        // 建立TabLayout和ViewPager2的关联，实现标签和页面的联动切换
        new TabLayoutMediator(tabLayout,viewPager,((tab, position) ->{
            // 根据页面位置设置Tab的显示文本
            if(position==0){
                // 0号位置Tab显示"点餐"
                tab.setText("点餐");
            }else{
                // 1号位置Tab显示"评论"
                tab.setText("评论");
            }
        } )).attach(); // 完成关联绑定，使TabLayout和ViewPager2联动生效
    }
}