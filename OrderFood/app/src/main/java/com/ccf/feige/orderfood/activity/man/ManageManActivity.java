package com.ccf.feige.orderfood.activity.man;
// 包声明：该类属于商家管理模块的Activity包，存放商家相关的页面逻辑

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
// 导入相关依赖类：
// 1. Androidx 兼容库：提供Activity、Fragment等核心组件的向下兼容支持
// 2. 系统API：Intent（页面跳转）、Bundle（数据存储）、Handler（线程通信）等
// 3. 自定义类：商家首页/我的页面Fragment、项目资源文件
// 4. Material Design组件：底部导航栏及对应的监听接口

/**
 * 商家管理主页面Activity
 * 所属模块：商家端管理模块
 * 核心功能：
 * 1. 底部导航栏切换（首页Fragment / 我的Fragment）
 * 2. 底部导航跳转添加菜品页面
 * 3. 双击返回键退出程序（彻底清空缓存数据，不返回登录界面，直接退出应用）
 * 设计亮点：
 * - 缓存Fragment实例，避免重复创建造成的性能损耗
 * - 通用Fragment切换方法，减少冗余代码
 * - 完善的资源释放逻辑，避免内存泄漏
 * - 容错处理，提升页面稳定性
 */
public class ManageManActivity extends AppCompatActivity {
    // 全局组件：底部导航栏（缓存引用，避免在多次调用中重复执行findViewById，提升性能）
    private BottomNavigationView bottomNav;
    // 全局Fragment：缓存首页和我的Fragment实例，避免页面切换时重复创建，保持页面状态
    private ManageHomeFragment homeFragment;
    private ManageMyFragment myFragment;
    // Fragment管理器（全局缓存，避免多次调用getSupportFragmentManager()，简化代码调用）
    private FragmentManager fragmentManager;

    // Intent参数常量（定义常量避免硬编码，便于后续维护和参数统一修改，降低出错概率）
    private static final String INTENT_KEY_STA = "sta";
    // 底部导航ID常量（与布局文件中的menu_item id一一对应，消除魔法值，提高代码可读性）
    private static final int MENU_ID_HOME = R.id.man_manage_bottom_menu_home;   // 首页菜单ID
    private static final int MENU_ID_ADD = R.id.man_manage_bottom_menu_add;     // 添加菜品菜单ID
    private static final int MENU_ID_MY = R.id.man_manage_bottom_menu_my;       // 我的菜单ID

    // ====================== 双击返回键退出相关配置 ======================
    private static final long EXIT_INTERVAL = 2000; // 两次返回键点击的有效间隔时间（2000毫秒=2秒）
    private long lastBackPressedTime = 0; // 记录上次点击返回键的时间戳，用于判断是否在有效间隔内
    private Toast exitToast; // 退出提示Toast实例，缓存避免重复创建，防止多次点击弹出多个Toast
    private Handler mainHandler = new Handler(Looper.getMainLooper()); // 主线程Handler，用于延迟重置返回键记录时间

    /**
     * Activity生命周期：创建阶段（首次启动时执行）
     * 核心职责：初始化页面布局、全局组件、首次加载Fragment、绑定导航监听
     * @param savedInstanceState 页面保存的状态数据（如屏幕旋转、后台回收后的恢复数据）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载当前Activity对应的布局文件（布局文件路径：res/layout/activity_manage_man.xml）
        setContentView(R.layout.activity_manage_man);

        // 1. 初始化全局组件与缓存实例，避免重复查找和创建
        initGlobalComponents();

        // 2. 根据Intent传递的参数，加载首次显示的Fragment（首页/我的）
        loadFirstFragment();

        // 3. 初始化底部导航栏的选中事件监听，实现Fragment切换和页面跳转逻辑
        initBottomNavListener();
    }

    /**
     * 初始化全局组件与缓存实例
     * 设计目的：集中管理全局资源的初始化，提高代码可读性，避免在多个方法中重复创建和查找组件
     */
    private void initGlobalComponents() {
        // 获取Fragment管理器（支持Androidx的Fragment，与getFragmentManager()区分）
        fragmentManager = getSupportFragmentManager();

        // 查找底部导航栏组件（通过布局id获取，缓存为全局变量，后续无需重复findViewById）
        bottomNav = findViewById(R.id.man_manage_bottom_menu);

        // 初始化并缓存Fragment实例，避免页面切换时重复创建，减少内存开销和初始化耗时
        homeFragment = new ManageHomeFragment();
        myFragment = new ManageMyFragment();

        // 初始化退出提示Toast，设置提示文案和显示时长，缓存实例避免重复创建
        exitToast = Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT);
    }

    /**
     * 首次加载Fragment（页面启动时的默认显示内容）
     * 逻辑依据：根据Intent中的"sta"参数判断，无参数显示首页，有参数显示“我的”页面
     * 设计目的：支持从其他页面跳转时指定默认显示的Fragment，提升用户体验
     */
    private void loadFirstFragment() {
        // 获取启动当前Activity的Intent对象，进行容错处理（避免Intent为null导致空指针异常）
        Intent intent = getIntent();
        String sta = intent != null ? intent.getStringExtra(INTENT_KEY_STA) : null;

        // 开启Fragment事务（Fragment的添加/替换/删除必须在事务中执行，保证操作的原子性）
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (sta == null || sta.trim().isEmpty()) {
            // 情况1：无有效"sta"参数，替换容器为首页Fragment
            transaction.replace(R.id.man_manage_frame, homeFragment);
        } else {
            // 情况2：有有效"sta"参数，替换容器为“我的”Fragment
            transaction.replace(R.id.man_manage_frame, myFragment);
            // 同步设置底部导航栏选中“我的”，保持导航状态与显示内容一致
            bottomNav.setSelectedItemId(MENU_ID_MY);
        }
        // 提交Fragment事务，执行上述替换操作（事务必须提交才会生效）
        transaction.commit();
    }

    /**
     * 初始化底部导航栏选中事件监听
     * 核心功能：处理底部导航的三个菜单点击事件，分别实现“首页”“添加菜品”“我的”的对应逻辑
     */
    private void initBottomNavListener() {
        // 给底部导航栏设置选中项监听（NavigationBarView.OnItemSelectedListener为Material Design的监听接口）
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            /**
             * 底部导航菜单项被选中时触发
             * @param item 被选中的菜单项（包含菜单id、标题、图标等信息）
             * @return boolean 返回true表示消费该事件，保持选中状态；返回false表示不消费，恢复原有选中状态
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 获取被选中菜单项的ID，用于判断执行哪种逻辑
                int menuId = item.getItemId();
                // 开启Fragment事务，为后续Fragment切换做准备
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // 分支判断：根据菜单ID执行对应业务逻辑
                if (menuId == MENU_ID_HOME) {
                    // 逻辑1：选中“首页”，切换到首页Fragment（调用通用切换方法，避免重复代码）
                    switchFragment(transaction, homeFragment);
                } else if (menuId == MENU_ID_ADD) {
                    // 逻辑2：选中“添加菜品”，跳转到添加菜品Activity（单独封装跳转方法，便于维护）
                    jumpToAddFoodActivity();
                    return true; // 直接返回true，无需执行后续的事务提交（无Fragment切换操作）
                } else if (menuId == MENU_ID_MY) {
                    // 逻辑3：选中“我的”，切换到我的Fragment（调用通用切换方法，避免重复代码）
                    switchFragment(transaction, myFragment);
                }

                // 提交Fragment事务，执行Fragment切换操作（仅当有Fragment切换时生效）
                transaction.commit();
                return true; // 消费该事件，保持当前菜单项的选中状态
            }
        });
    }

    /**
     * Fragment通用切换方法（封装重复逻辑，提高代码复用性）
     * 优化点：判断当前显示的Fragment是否与目标Fragment一致，避免重复替换造成的性能损耗和页面闪烁
     * @param transaction 已开启的Fragment事务（外部传入，保证事务操作的连贯性）
     * @param targetFragment 目标Fragment（需要切换到的Fragment实例）
     */
    private void switchFragment(FragmentTransaction transaction, Fragment targetFragment) {
        // 获取当前Fragment容器中显示的Fragment实例（通过容器id查找）
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.man_manage_frame);
        // 容错判断 + 一致性判断：若当前Fragment与目标Fragment是同一类，无需执行替换操作
        if (currentFragment != null && currentFragment.getClass().equals(targetFragment.getClass())) {
            return;
        }
        // 替换Fragment容器中的内容为目标Fragment（R.id.man_manage_frame为布局中的Fragment容器id）
        transaction.replace(R.id.man_manage_frame, targetFragment);
    }

    /**
     * 跳转到添加菜品页面（ManageManAddFoodActivity）
     * 设计亮点：添加Activity有效性判断，避免在Activity销毁过程中跳转导致的异常
     */
    private void jumpToAddFoodActivity() {
        // 容错处理：判断当前Activity是否有效（避免已销毁/正在销毁时执行跳转，导致崩溃）
        if (this == null || isFinishing() || isDestroyed()) {
            return;
        }
        // 构建跳转Intent，指定当前Activity为上下文，目标Activity为ManageManAddFoodActivity
        Intent addFoodIntent = new Intent(ManageManActivity.this, ManageManAddFoodActivity.class);
        // 启动目标Activity，执行页面跳转
        startActivity(addFoodIntent);
    }

    /**
     * 重写返回键事件（覆盖系统默认行为）
     * 核心需求：1. 屏蔽默认的“返回上一个Activity”（不返回登录界面）；2. 双击返回键彻底退出程序；3. 退出前清空所有缓存数据
     * 设计要点：不调用super.onBackPressed()，完全自定义返回键逻辑
     */
    @Override
    public void onBackPressed() {
        // 1. 获取当前点击返回键的时间戳（毫秒级）
        long currentTime = System.currentTimeMillis();

        // 2. 判断两次点击返回键的时间间隔是否在有效范围内（小于2秒）
        if (currentTime - lastBackPressedTime < EXIT_INTERVAL) {
            // 情况1：双击返回（有效间隔内二次点击），执行退出程序逻辑
            clearAllData(); // 退出前清空所有缓存数据，避免内存泄漏和数据残留
            finishAffinity(); // 关闭当前应用的所有Activity（清空任务栈），不返回任何历史页面
            // 可选：Android Q及以上版本，System.exit(0)可强制结束进程，与finishAffinity()配合使用更稳妥（注释保留供参考）
            // System.exit(0);

            // 取消未消失的退出提示Toast，避免退出后仍显示提示
            if (exitToast != null) {
                exitToast.cancel();
            }
        } else {
            // 情况2：首次点击（或超出有效间隔的点击），仅记录时间并显示提示
            lastBackPressedTime = currentTime; // 更新上次点击返回键的时间戳
            exitToast.show(); // 显示“再按一次返回键退出程序”提示

            // 3. 延迟2秒重置上次点击时间戳，避免用户长时间不操作后误触双击退出
            mainHandler.postDelayed(() -> lastBackPressedTime = 0, EXIT_INTERVAL);
        }

        // 核心：不调用super.onBackPressed()，彻底屏蔽系统默认的返回上一个Activity的行为
    }

    /**
     * 清空所有缓存数据和资源引用（退出程序前执行）
     * 设计目的：释放内存，避免内存泄漏，清除无持久化需求的残留数据，保证程序退出的干净性
     */
    private void clearAllData() {
        // 1. 清空Fragment实例缓存，释放Fragment占用的内存
        homeFragment = null;
        myFragment = null;

        // 2. 清空全局组件引用，断开与Activity的绑定，避免内存泄漏
        fragmentManager = null;
        bottomNav = null;

        // 3. 清空SharedPreferences中的商家信息缓存（无持久化需求，退出即清空）
        clearSPResidue();

        // 4. 移除Handler中所有未执行的回调和消息，避免后台回调导致的内存泄漏
        mainHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 清空SharedPreferences中的残留数据（商家信息缓存）
     * 设计要点：使用try-catch捕获异常，避免清空失败导致程序崩溃；使用apply()异步提交，不阻塞主线程
     */
    private void clearSPResidue() {
        try {
            // 获取名为"BusinessInfoSP"的SharedPreferences实例（私有模式，仅当前应用可访问）
            // 调用edit()获取编辑器，clear()清空所有数据，apply()异步提交修改（比commit()更高效，不阻塞主线程）
            getSharedPreferences("BusinessInfoSP", MODE_PRIVATE).edit().clear().apply();
        } catch (Exception e) {
            // 捕获异常并打印堆栈信息，避免清空失败导致后续退出逻辑中断
            e.printStackTrace();
        }
    }

    /**
     * Activity生命周期：销毁阶段（页面关闭时执行）
     * 核心职责：释放最后剩余的资源引用，彻底避免内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy(); // 调用父类的onDestroy()，保证Activity生命周期的完整性
        // 清空Toast和Handler的引用，断开与Activity的绑定
        exitToast = null;
        mainHandler = null;
    }
}