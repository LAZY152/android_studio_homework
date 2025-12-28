package com.ccf.feige.orderfood.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.ManageManActivity;
import com.ccf.feige.orderfood.activity.man.ManageManOrderFinishActivity;
import com.ccf.feige.orderfood.activity.user.adapter.AddressListUserAdapter;
import com.ccf.feige.orderfood.bean.AddressBean;
import com.ccf.feige.orderfood.bean.CommentBean;
import com.ccf.feige.orderfood.dao.AddressDao;
import com.ccf.feige.orderfood.dao.CommentDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.List;

public class ManageUserAddressActivity extends AppCompatActivity {

    // 关键修改1：定义成员变量，方便多个方法访问（原局部变量提升为成员变量）
    private RecyclerView mAddressRecyclerView;
    private String mAccount;
    private AddressListUserAdapter mAddressListUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_address);

        //实现一个返回的功能
        Toolbar toolbar = findViewById(R.id.user_address_bar);
        setSupportActionBar(toolbar);

        //返回有两种，采用跳转和管理
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageUserAddressActivity.this, ManageUserActivity.class);
                intent.putExtra("sta","1");
                startActivity(intent);
            }
        });

        // 关键修改2：初始化成员变量
        mAddressRecyclerView = findViewById(R.id.user_address_list);
        mAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAccount = Tools.getOnAccount(this);

        // 关键修改3：调用抽离的加载列表方法，完成首次加载
        loadAddressList();
    }

    // 关键修改4：抽离独立的列表加载/刷新方法，复用逻辑
    private void loadAddressList() {
        // 重新查询最新的地址数据（从数据库获取，保证数据最新）
        List<AddressBean> list = AddressDao.getAllAddressByUserId(mAccount);

        // 初始化或更新Adapter
        mAddressListUserAdapter = new AddressListUserAdapter(list);

        // 为空判断，设置Adapter（保持你的原有逻辑）
        if(list==null||list.size()==0){
            mAddressRecyclerView.setAdapter(null);
        }else{
            mAddressRecyclerView.setAdapter(mAddressListUserAdapter);
        }
    }

    // 关键修改5：重写onResume()方法，页面回到前台时刷新列表
    @Override
    protected void onResume() {
        super.onResume();
        // 页面从后台返回前台（添加地址完成后），重新加载列表，实现实时刷新
        loadAddressList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_address_add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int a=item.getItemId();
        if(a==R.id.user_manage_addressAdd) {
            Intent intent=new Intent(this, ManageUserAddAddressActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}