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

/**
 * 用户中心管理页面（主页面）
 * 功能：承载用户首页、未完成订单、我的页面等Fragment的切换，处理双击退出逻辑，管理用户相关业务跳转
 */
public class ManageUserActivity extends AppCompatActivity {
    // 全局组件：底部导航栏（避免重复findViewById查找，提升性能并减少代码冗余）
    private BottomNavigationView bottomNavigationView;
    // 全局Fragment管理器（避免重复获取getSupportFragmentManager()，统一管理Fragment生命周期）
    private FragmentManager fragmentManager;
    // 缓存Fragment实例，避免重复创建（提升页面切换流畅度，减少内存开销和Fragment重建带来的状态丢失）
    private UserHomeFragment userHomeFragment; // 用户首页Fragment
    private UserNoFinishOrderFragment userNoFinishOrderFragment; // 用户未完成订单Fragment
    private ManageUserMyFragment manageUserMyFragment; // 用户"我的"页面Fragment

    // ====================== 双击返回键相关常量与变量 ======================
    private static final long EXIT_INTERVAL = 2000; // 两次返回键有效间隔时间（2秒），超过则视为首次点击
    private long lastBackPressedTime = 0; // 上次点击返回键的时间戳，用于判断是否为双击
    private Toast exitToast; // 退出提示Toast实例，缓存避免重复创建，方便后续取消
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler，用于延迟重置返回键点击状态
    // Intent参数常量（避免硬编码字符串，提高代码可维护性，减少拼写错误）
    private static final String INTENT_KEY_STA = "sta";

    /**
     * 页面生命周期方法：创建页面时调用
     * 负责初始化布局、全局组件、Fragment缓存和业务逻辑初始化
     * @param savedInstanceState 页面保存的状态数据（如屏幕旋转时的状态）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置当前页面的布局文件（关联res/layout/activity_manage_user.xml）
        setContentView(R.layout.activity_manage_user);

        // 1. 初始化全局组件、Fragment缓存与返回提示Toast（统一初始化，便于维护）
        initGlobalComponents();

        // 2. 首次加载Fragment（根据上一个页面传递的Intent参数判断，保留原有业务逻辑）
        loadFirstFragment();

        // 3. 初始化底部导航栏选中监听（处理Fragment切换逻辑，保留原有业务流程）
        initBottomNavListener();
    }

    /**
     * 初始化全局组件、Fragment缓存与返回提示，避免重复创建和查找
     * 作用：统一初始化全局资源，减少重复代码，提升页面加载性能
     */
    private void initGlobalComponents() {
        // 获取全局Fragment管理器（支持兼容包，适配低版本Android系统）
        fragmentManager = getSupportFragmentManager();

        // 初始化底部导航栏（通过控件ID查找布局中的BottomNavigationView）
        bottomNavigationView = findViewById(R.id.user_manage_bottom_menu);

        // 缓存Fragment实例，避免重复new创建（提升页面切换性能，减少内存开销，保留Fragment状态）
        userHomeFragment = new UserHomeFragment();
        userNoFinishOrderFragment = new UserNoFinishOrderFragment();
        manageUserMyFragment = new ManageUserMyFragment();

        // 初始化退出提示Toast（缓存实例，避免重复创建，统一提示文案）
        exitToast = Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT);
    }

    /**
     * 首次加载Fragment（保留原有Intent判断逻辑，保证业务连贯性）
     * 作用：根据上一个页面传递的参数，决定首次进入显示的页面
     */
    private void loadFirstFragment() {
        // 获取上一个页面传递的Intent对象
        Intent intent = getIntent();
        // 提取Intent中的"sta"参数值，若Intent为null则参数值为null
        String sta = intent != null ? intent.getStringExtra(INTENT_KEY_STA) : null;

        // 开启Fragment事务（Fragment切换必须通过事务实现）
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 判断参数是否为空，决定加载的首页Fragment
        if (sta == null || sta.trim().isEmpty()) {
            // 参数为空：加载用户首页Fragment，替换布局中的FrameLayout容器
            transaction.replace(R.id.user_manage_frame, userHomeFragment);
        } else {
            // 参数不为空：加载用户"我的"Fragment，替换布局中的FrameLayout容器
            transaction.replace(R.id.user_manage_frame, manageUserMyFragment);
            // 选中底部导航栏的“我的”选项，保持界面与Fragment的一致性
            bottomNavigationView.setSelectedItemId(R.id.user_manage_bottom_menu_my);
        }
        // 提交Fragment事务，执行Fragment切换操作
        transaction.commit();
    }

    /**
     * 初始化底部导航栏选中监听（保留原有Fragment切换逻辑，优化为缓存实例）
     * 作用：监听底部导航栏的选项点击事件，实现对应的Fragment切换
     */
    private void initBottomNavListener() {
        // 为底部导航栏设置选中项监听器（NavigationBarView是BottomNavigationView的父类，兼容性更好）
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            /**
             * 底部导航栏选项被选中时的回调方法
             * @param item 被选中的导航栏选项（包含选项ID、文案、图标等信息）
             * @return boolean 返回true表示消费该点击事件，false表示不消费
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 开启Fragment事务，准备执行Fragment切换
                FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                // 获取被选中选项的唯一ID（用于判断点击的是哪个选项）
                int id = item.getItemId();

                // 判断选项ID，加载对应的缓存Fragment实例
                if (id == R.id.user_manage_bottom_menu_home) {
                    // 点击“首页”：切换到用户首页Fragment
                    transaction1.replace(R.id.user_manage_frame, userHomeFragment);
                }
                if (id == R.id.user_manage_bottom_menu_noFinish) {
                    // 点击“未完成订单”：切换到用户未完成订单Fragment
                    transaction1.replace(R.id.user_manage_frame, userNoFinishOrderFragment);
                }
                if (id == R.id.user_manage_bottom_menu_my) {
                    // 点击“我的”：切换到用户"我的"Fragment
                    transaction1.replace(R.id.user_manage_frame, manageUserMyFragment);
                }

                // 提交Fragment事务，执行切换操作
                transaction1.commit();
                // 返回true，标识该点击事件已被处理
                return true;
            }
        });
    }

    /**
     * 原有业务方法：显示已完成订单
     * 作用：响应业务逻辑跳转，切换到已完成订单Fragment（该Fragment未缓存，每次调用都新建实例）
     */
    public void showOrder() {
        // 开启Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 新建UserFinishOrderFragment实例，替换FrameLayout容器，显示已完成订单页面
        transaction.replace(R.id.user_manage_frame, new UserFinishOrderFragment());
        // 提交事务，执行切换
        transaction.commit();
    }

    /**
     * 原有业务方法：显示我的页面
     * 作用：响应业务逻辑跳转，切换到用户"我的"Fragment（使用缓存实例，保留页面状态）
     */
    public void showMy() {
        // 开启Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 替换为缓存的"我的"Fragment实例，显示我的页面
        transaction.replace(R.id.user_manage_frame, manageUserMyFragment);
        // 提交事务，执行切换
        transaction.commit();
    }

    /**
     * 重写返回键事件：彻底屏蔽默认返回行为，双击才退出，不返回登录界面
     * 关键：不调用 super.onBackPressed()（屏蔽系统默认返回上一个Activity的逻辑）
     */
    @Override
    public void onBackPressed() {
        // 1. 获取当前点击返回键的时间戳（毫秒级）
        long currentTime = System.currentTimeMillis();

        // 2. 判断是否在2秒内二次点击返回键（通过当前时间与上次点击时间的差值判断）
        if (currentTime - lastBackPressedTime < EXIT_INTERVAL) {
            // 二次点击：执行数据清空 + 彻底退出程序（不返回任何上一级界面）
            clearAllData();
            // 关闭当前任务栈中所有Activity（包括登录界面、当前用户页面等），直接退出程序
            finishAffinity();
            // 可选：Android Q及以上版本，额外调用System.exit(0)确保进程退出（优先使用finishAffinity实现优雅退出）
            // System.exit(0);

            // 取消未消失的退出提示Toast，避免界面残留
            if (exitToast != null) {
                exitToast.cancel();
            }
        } else {
            // 首次点击：仅记录点击时间 + 显示退出提示，不执行任何关闭/返回操作
            lastBackPressedTime = currentTime;
            // 显示Toast提示用户二次点击退出
            exitToast.show();

            // 3. 2秒后重置点击时间戳，避免用户长时间不操作后，再次点击误触退出程序
            mainHandler.postDelayed(() -> lastBackPressedTime = 0, EXIT_INTERVAL);
        }

        // 核心：不调用 super.onBackPressed()，完全屏蔽系统默认的“返回上一个Activity”的行为
        // 确保不会返回登录界面或其他上一级页面，仅通过双击实现退出
    }

    /**
     * 清空所有数据（内存缓存、全局变量、可能的残留数据）
     * 作用：退出程序前释放资源，避免内存泄漏和数据残留
     */
    private void clearAllData() {
        // 1. 清空Fragment实例缓存（释放内存，解除Fragment引用）
        userHomeFragment = null;
        userNoFinishOrderFragment = null;
        manageUserMyFragment = null;

        // 2. 清空Fragment管理器、底部导航栏引用（解除组件引用，便于GC回收）
        fragmentManager = null;
        bottomNavigationView = null;

        // 3. 清空SharedPreferences残留数据（若有历史用户缓存，避免下次启动残留旧数据）
        clearSPResidue();

        // 4. 移除Handler所有未执行的回调任务，避免内存泄漏（清空Handler消息队列）
        mainHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 清空SharedPreferences残留数据（彻底清空，无数据残留）
     * 作用：清除用户相关的本地缓存，保证程序退出后无隐私数据残留
     */
    private void clearSPResidue() {
        try {
            // 清空商家相关SP缓存（与商家页面保持一致，避免冗余数据残留）
            getSharedPreferences("BusinessInfoSP", MODE_PRIVATE).edit().clear().apply();
            // 清空用户相关SP缓存（单独的用户信息文件，确保用户数据彻底清除）
            getSharedPreferences("UserInfoSP", MODE_PRIVATE).edit().clear().apply();
            // 备注：apply()为异步提交，不阻塞主线程；commit()为同步提交，适合少量数据
        } catch (Exception e) {
            // 捕获异常，避免清空SP失败导致程序崩溃
            e.printStackTrace();
        }
    }

    /**
     * 页面生命周期方法：页面销毁时调用
     * 作用：释放剩余资源，避免内存泄漏，确保页面销毁后无残留引用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清空Toast实例引用，便于GC回收
        exitToast = null;
        // 清空主线程Handler引用，解除消息队列关联
        mainHandler = null;
    }
}