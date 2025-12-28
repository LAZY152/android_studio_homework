package com.ccf.feige.orderfood.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.frament.ManageUserMyFragment;
import com.ccf.feige.orderfood.activity.user.frament.UserFinishOrderFragment;
import com.ccf.feige.orderfood.activity.user.frament.UserHomeFragment;
import com.ccf.feige.orderfood.activity.user.frament.UserNoFinishOrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ManageUserActivity extends AppCompatActivity {
    // 全局组件：底部导航栏（避免重复查找）
    private BottomNavigationView bottomNavigationView;
    // 全局Fragment管理器（避免重复获取）
    private FragmentManager fragmentManager;
    // 缓存Fragment实例，避免重复创建（提升性能）
    private UserHomeFragment userHomeFragment;
    private UserNoFinishOrderFragment userNoFinishOrderFragment;
    private ManageUserMyFragment manageUserMyFragment;

    // ====================== 双击返回键相关常量与变量 ======================
    private static final long EXIT_INTERVAL = 2000; // 两次返回键间隔（2秒）
    private long lastBackPressedTime = 0; // 上次点击返回键的时间
    private Toast exitToast; // 退出提示Toast，避免重复弹出
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler
    // Intent参数常量（避免硬编码）
    private static final String INTENT_KEY_STA = "sta";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        // 1. 初始化全局组件、Fragment缓存与返回提示
        initGlobalComponents();

        // 2. 首次加载Fragment（根据Intent参数判断，保留原有业务逻辑）
        loadFirstFragment();

        // 3. 初始化底部导航栏选中监听（保留原有业务逻辑）
        initBottomNavListener();
    }

    /**
     * 初始化全局组件、Fragment缓存与返回提示，避免重复创建和查找
     */
    private void initGlobalComponents() {
        // 获取全局Fragment管理器
        fragmentManager = getSupportFragmentManager();

        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.user_manage_bottom_menu);

        // 缓存Fragment实例，避免重复new（提升性能，减少内存开销）
        userHomeFragment = new UserHomeFragment();
        userNoFinishOrderFragment = new UserNoFinishOrderFragment();
        manageUserMyFragment = new ManageUserMyFragment();

        // 初始化退出提示Toast
        exitToast = Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT);
    }

    /**
     * 首次加载Fragment（保留原有Intent判断逻辑）
     */
    private void loadFirstFragment() {
        Intent intent = getIntent();
        String sta = intent != null ? intent.getStringExtra(INTENT_KEY_STA) : null;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (sta == null || sta.trim().isEmpty()) {
            transaction.replace(R.id.user_manage_frame, userHomeFragment);
        } else {
            transaction.replace(R.id.user_manage_frame, manageUserMyFragment);
            // 选中“我的”导航项，保持界面一致性
            bottomNavigationView.setSelectedItemId(R.id.user_manage_bottom_menu_my);
        }
        transaction.commit();
    }

    /**
     * 初始化底部导航栏选中监听（保留原有Fragment切换逻辑，优化为缓存实例）
     */
    private void initBottomNavListener() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                int id = item.getItemId();

                if (id == R.id.user_manage_bottom_menu_home) {
                    transaction1.replace(R.id.user_manage_frame, userHomeFragment);
                }
                if (id == R.id.user_manage_bottom_menu_noFinish) {
                    transaction1.replace(R.id.user_manage_frame, userNoFinishOrderFragment);
                }
                if (id == R.id.user_manage_bottom_menu_my) {
                    transaction1.replace(R.id.user_manage_frame, manageUserMyFragment);
                }

                transaction1.commit();
                return true;
            }
        });
    }

    /**
     * 原有业务方法：显示已完成订单
     */
    public void showOrder() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.user_manage_frame, new UserFinishOrderFragment());
        transaction.commit();
    }

    /**
     * 原有业务方法：显示我的页面
     */
    public void showMy() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.user_manage_frame, manageUserMyFragment);
        transaction.commit();
    }

    /**
     * 重写返回键事件：彻底屏蔽默认返回行为，双击才退出，不返回登录界面
     * 关键：不调用 super.onBackPressed()（屏蔽系统默认返回逻辑）
     */
    @Override
    public void onBackPressed() {
        // 1. 获取当前点击时间
        long currentTime = System.currentTimeMillis();

        // 2. 判断是否在2秒内二次点击
        if (currentTime - lastBackPressedTime < EXIT_INTERVAL) {
            // 二次点击：清空所有数据 + 彻底退出程序（不返回任何界面）
            clearAllData();
            // 关闭当前任务栈中所有Activity（包括登录界面、当前用户页面等），直接退出程序
            finishAffinity();
            // 可选：Android Q及以上，额外确保进程退出（优雅退出优先用finishAffinity）
            // System.exit(0);

            // 取消未消失的提示Toast
            if (exitToast != null) {
                exitToast.cancel();
            }
        } else {
            // 首次点击：仅记录时间 + 显示提示，不执行任何关闭/返回操作
            lastBackPressedTime = currentTime;
            exitToast.show();

            // 3. 2秒后重置点击时间，避免用户长时间不操作后误触退出
            mainHandler.postDelayed(() -> lastBackPressedTime = 0, EXIT_INTERVAL);
        }

        // 核心：不调用 super.onBackPressed()，完全屏蔽系统默认的“返回上一个Activity”行为
    }

    /**
     * 清空所有数据（内存缓存、全局变量、可能的残留数据）
     */
    private void clearAllData() {
        // 1. 清空Fragment实例缓存（释放内存）
        userHomeFragment = null;
        userNoFinishOrderFragment = null;
        manageUserMyFragment = null;

        // 2. 清空Fragment管理器、底部导航栏引用
        fragmentManager = null;
        bottomNavigationView = null;

        // 3. 清空SharedPreferences残留数据（若有历史用户缓存）
        clearSPResidue();

        // 4. 移除Handler所有未执行回调，避免内存泄漏
        mainHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 清空SharedPreferences残留数据（彻底清空，无数据残留）
     */
    private void clearSPResidue() {
        try {
            // 清空用户相关SP缓存（与商家页面保持一致，避免冗余数据）
            getSharedPreferences("BusinessInfoSP", MODE_PRIVATE).edit().clear().apply();
            // 若有单独的用户SP文件，也一并清空
            getSharedPreferences("UserInfoSP", MODE_PRIVATE).edit().clear().apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 页面销毁时释放资源，避免内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清空剩余引用
        exitToast = null;
        mainHandler = null;
    }
}