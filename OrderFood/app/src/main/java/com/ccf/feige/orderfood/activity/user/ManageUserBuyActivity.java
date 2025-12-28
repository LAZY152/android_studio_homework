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
 */
public class ManageUserBuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_buy);

        //实现一个返回的功能
        Toolbar toolbar = findViewById(R.id.user_buy_bar);
        setSupportActionBar(toolbar);

        //返回有两种，采用跳转和管理
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        Intent intent = getIntent();
        UserBean business =(UserBean) intent.getSerializableExtra("business");
        //实现一个返回的功能
        ImageView businessImg = findViewById(R.id.user_buy_businessTx);
        businessImg.setImageBitmap(BitmapFactory.decodeFile(business.getsImg()));


        TextView businessName = findViewById(R.id.user_buy_businessName);
        businessName.setText(business.getsName());



        TextView businessDes = findViewById(R.id.user_buy_businessDes);
        businessDes.setText("简介: "+business.getsDescribe());

        //实现显示数量
        TextView price =this.findViewById(R.id.user_buy_businessPrice);
        price.setText("0.00");


        TextView foodJson =this.findViewById(R.id.user_buy_businessFood);


        Button buy =this.findViewById(R.id.user_buy_businessBuy_con);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String foodJsonT = foodJson.getText().toString();
                JSONArray jsonArray=JSONArray.parseArray(foodJsonT);

                List<JSONObject> buyFoodList=new ArrayList<>();//存放素有购买商品的列表
                for (Object o : jsonArray) {
                    JSONObject temp = JSONObject.parseObject(o.toString());
                    if(!temp.get("num").equals("0")){
                        buyFoodList.add(temp);
                    }

                }

                if(foodJsonT.isEmpty()){
                    Toast.makeText(ManageUserBuyActivity.this, "未选择商品无法结算", Toast.LENGTH_SHORT).show();
                }else if(buyFoodList.size()==0){
                    Toast.makeText(ManageUserBuyActivity.this, "未选择商品无法结算", Toast.LENGTH_SHORT).show();
                }else{
                    UserBottomDialog userBottomDialog=new UserBottomDialog(ManageUserBuyActivity.this,business.getsId());
                }


            }
        });

        //点击结算按钮之后的事情



        TabLayout tabLayout = findViewById(R.id.user_buy_tab);
        ViewPager2 viewPager = findViewById(R.id.user_buy_pager);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {

                if(position==0){
                    return new UserBuyFoodFBusinessFragment(business.getsId(),ManageUserBuyActivity.this);
                }else{
                    return new UserBuyFoodBusinessCommentFragment(business.getsId());
                }

            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout,viewPager,((tab, position) ->{

                if(position==0){
                    tab.setText("点餐");
                }else{
                    tab.setText("评论");
                }


        } )).attach();




    }
}