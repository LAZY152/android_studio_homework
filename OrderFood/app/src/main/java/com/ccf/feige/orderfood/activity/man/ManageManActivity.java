package com.ccf.feige.orderfood.activity.man;

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
import com.ccf.feige.orderfood.activity.man.frament.ManageHomeFragment;
import com.ccf.feige.orderfood.activity.man.frament.ManageMyFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * 商家管理主页面
 * 功能：1. 底部导航切换（首页/添加菜品/我的）；2. 双击返回键退出程序（清空所有数据，不返回登录界面）
 */
public class ManageManActivity extends AppCompatActivity {
    // 全局组件：底部导航栏（避免重复查找）
    private BottomNavigationView bottomNav;
    // 全局Fragment：缓存首页和我的Fragment，避免重复创建
    private ManageHomeFragment homeFragment;
    private ManageMyFragment myFragment;
    // Fragment管理器（全局缓存，避免重复获取）
    private FragmentManager fragmentManager;

    // Intent参数常量（避免硬编码，便于维护）
    private static final String INTENT_KEY_STA = "sta";
    // 底部导航ID常量（与布局文件对应，避免魔法值）
    private static final int MENU_ID_HOME = R.id.man_manage_bottom_menu_home;
    private static final int MENU_ID_ADD = R.id.man_manage_bottom_menu_add;
    private static final int MENU_ID_MY = R.id.man_manage_bottom_menu_my;

    // ====================== 双击返回键相关常量与变量 ======================
    private static final long EXIT_INTERVAL = 2000; // 两次返回键间隔（2秒）
    private long lastBackPressedTime = 0; // 上次点击返回键的时间
    private Toast exitToast; // 退出提示Toast，避免重复弹出
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_man);

        // 1. 初始化全局组件与缓存
        initGlobalComponents();

        // 2. 首次加载Fragment（根据Intent参数判断）
        loadFirstFragment();

        // 3. 初始化底部导航栏选中监听
        initBottomNavListener();
    }

    /**
     * 初始化全局组件与缓存，避免重复创建和查找
     */
    private void initGlobalComponents() {
        // 获取Fragment管理器
        fragmentManager = getSupportFragmentManager();

        // 初始化底部导航栏
        bottomNav = findViewById(R.id.man_manage_bottom_menu);

        // 缓存Fragment实例，避免重复创建
        homeFragment = new ManageHomeFragment();
        myFragment = new ManageMyFragment();

        // 初始化退出提示Toast
        exitToast = Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT);
    }

    /**
     * 首次加载Fragment（根据Intent的"sta"参数判断）
     * sta为null → 加载首页Fragment，否则 → 加载我的Fragment
     */
    private void loadFirstFragment() {
        // 获取Intent参数
        Intent intent = getIntent();
        String sta = intent != null ? intent.getStringExtra(INTENT_KEY_STA) : null;

        // 开启Fragment事务（链式调用，简化代码）
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (sta == null || sta.trim().isEmpty()) {
            // 加载首页Fragment
            transaction.replace(R.id.man_manage_frame, homeFragment);
        } else {
            // 加载我的Fragment，并设置底部导航选中“我的”
            transaction.replace(R.id.man_manage_frame, myFragment);
            bottomNav.setSelectedItemId(MENU_ID_MY);
        }
        transaction.commit();
    }

    /**
     * 初始化底部导航栏选中监听，实现Fragment切换和页面跳转
     */
    private void initBottomNavListener() {
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuId = item.getItemId();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // 判断菜单ID，执行对应逻辑
                if (menuId == MENU_ID_HOME) {
                    // 切换到首页Fragment（判断是否已显示，避免重复替换）
                    switchFragment(transaction, homeFragment);
                } else if (menuId == MENU_ID_ADD) {
                    // 跳转到添加菜品页面（含容错处理）
                    jumpToAddFoodActivity();
                    return true; // 直接返回，无需执行后续事务提交
                } else if (menuId == MENU_ID_MY) {
                    // 切换到我的Fragment（判断是否已显示，避免重复替换）
                    switchFragment(transaction, myFragment);
                }

                // 提交Fragment事务
                transaction.commit();
                return true;
            }
        });
    }

    /**
     * 切换Fragment（通用方法，避免重复代码，判断是否已显示）
     * @param transaction Fragment事务
     * @param targetFragment 目标Fragment
     */
    private void switchFragment(FragmentTransaction transaction, Fragment targetFragment) {
        // 获取当前显示的Fragment
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.man_manage_frame);
        // 若当前Fragment与目标Fragment一致，无需替换
        if (currentFragment != null && currentFragment.getClass().equals(targetFragment.getClass())) {
            return;
        }
        // 替换Fragment，保持页面状态
        transaction.replace(R.id.man_manage_frame, targetFragment);
        // 可选：添加到返回栈，支持返回键切换（根据业务需求开启/关闭）
        // transaction.addToBackStack(null); // 注：此处若开启，会导致返回栈存在，需确保关闭
    }

    /**
     * 跳转到添加菜品页面（含容错处理）
     */
    private void jumpToAddFoodActivity() {
        if (this == null || isFinishing() || isDestroyed()) {
            return;
        }
        Intent addFoodIntent = new Intent(ManageManActivity.this, ManageManAddFoodActivity.class);
        startActivity(addFoodIntent);
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
            // 关闭当前任务栈中所有Activity（包括登录界面、当前页面等），直接退出程序
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
        homeFragment = null;
        myFragment = null;

        // 2. 清空Fragment管理器、底部导航栏引用
        fragmentManager = null;
        bottomNav = null;

        // 3. 清空SharedPreferences残留数据（若有历史缓存）
        clearSPResidue();

        // 4. 移除Handler所有未执行回调，避免内存泄漏
        mainHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 清空SharedPreferences残留数据（无持久化需求，彻底清空避免冗余缓存）
     */
    private void clearSPResidue() {
        try {
            getSharedPreferences("BusinessInfoSP", MODE_PRIVATE).edit().clear().apply();
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