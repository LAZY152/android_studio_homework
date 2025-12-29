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

/**
 * 用户地址管理页面
 * 功能：展示用户所有收货地址、跳转到添加地址页面、返回用户中心页面
 */
public class ManageUserAddressActivity extends AppCompatActivity {
    // 关键修改1：定义成员变量，方便多个方法访问（原局部变量提升为成员变量）
    private RecyclerView mAddressRecyclerView; // 用于展示地址列表的RecyclerView控件
    private String mAccount; // 当前登录用户的账号/唯一标识
    private AddressListUserAdapter mAddressListUserAdapter; // 地址列表的RecyclerView适配器

    /**
     * 页面创建时的初始化方法
     * @param savedInstanceState 保存页面状态的Bundle对象，用于页面重建时恢复数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前页面的布局文件
        setContentView(R.layout.activity_manage_user_address);

        //实现一个返回的功能：初始化顶部工具栏并设置返回逻辑
        Toolbar toolbar = findViewById(R.id.user_address_bar); // 绑定布局中的工具栏控件
        setSupportActionBar(toolbar); // 将工具栏设置为当前页面的ActionBar

        //返回有两种，采用跳转和管理：设置工具栏左侧返回按钮的点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回按钮，跳转到用户中心页面（ManageUserActivity）
                Intent intent=new Intent(ManageUserAddressActivity.this, ManageUserActivity.class);
                intent.putExtra("sta","1"); // 携带参数"sta"，值为"1"（用于目标页面判断跳转来源）
                startActivity(intent); // 启动目标页面
            }
        });

        // 关键修改2：初始化成员变量
        mAddressRecyclerView = findViewById(R.id.user_address_list); // 绑定布局中的地址列表RecyclerView控件
        // 为RecyclerView设置线性布局管理器（垂直排列，类似ListView效果）
        mAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAccount = Tools.getOnAccount(this); // 获取当前登录用户的账号（通过工具类Tools获取）

        // 关键修改3：调用抽离的加载列表方法，完成首次加载
        loadAddressList(); // 加载并展示用户的地址列表
    }

    // 关键修改4：抽离独立的列表加载/刷新方法，复用逻辑
    /**
     * 加载/刷新用户地址列表的核心方法
     * 功能：从数据库查询最新地址数据、初始化/更新适配器、绑定到RecyclerView
     */
    private void loadAddressList() {
        // 重新查询最新的地址数据（从数据库获取，保证数据最新）
        // 通过AddressDao数据访问层，根据当前用户账号查询该用户的所有收货地址
        List<AddressBean> list = AddressDao.getAllAddressByUserId(mAccount);

        // 初始化或更新Adapter：创建地址列表适配器，传入查询到的地址数据列表
        mAddressListUserAdapter = new AddressListUserAdapter(list);

        // 为空判断，设置Adapter（保持你的原有逻辑）：处理地址列表为空的情况
        if(list==null||list.size()==0){
            // 若数据为空或为null，设置RecyclerView的适配器为null（不展示任何内容）
            mAddressRecyclerView.setAdapter(null);
        }else{
            // 若数据不为空，将适配器绑定到RecyclerView，展示地址列表
            mAddressRecyclerView.setAdapter(mAddressListUserAdapter);
        }
    }

    // 关键修改5：重写onResume()方法，页面回到前台时刷新列表
    /**
     * 页面从后台回到前台时调用的方法
     * 场景：用户从添加地址页面（ManageUserAddAddressActivity）返回当前页面时
     */
    @Override
    protected void onResume() {
        super.onResume(); // 调用父类的onResume方法，保证页面生命周期正常执行
        // 页面从后台返回前台（添加地址完成后），重新加载列表，实现实时刷新
        loadAddressList(); // 刷新地址列表，展示最新的地址数据
    }

    /**
     * 初始化页面顶部菜单（右侧添加地址按钮）
     * @param menu 菜单对象，用于加载菜单布局
     * @return 返回true表示菜单初始化成功
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载菜单布局文件（R.menu.user_address_add_menu），该布局中定义了添加地址的菜单按钮
        getMenuInflater().inflate(R.menu.user_address_add_menu, menu);
        return super.onCreateOptionsMenu(menu); // 调用父类方法，完成菜单初始化
    }

    /**
     * 顶部菜单按钮的点击事件处理
     * @param item 被点击的菜单选项对象
     * @return 返回true表示处理了该点击事件
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 获取被点击菜单选项的唯一标识（id）
        int a=item.getItemId();
        // 判断是否点击了"添加地址"菜单按钮（R.id.user_manage_addressAdd）
        if(a==R.id.user_manage_addressAdd) {
            // 点击添加地址按钮，跳转到添加地址页面（ManageUserAddAddressActivity）
            Intent intent=new Intent(this, ManageUserAddAddressActivity.class);
            startActivity(intent); // 启动添加地址页面
        }
        return super.onOptionsItemSelected(item); // 调用父类方法，处理其他未捕获的菜单点击事件
    }
}