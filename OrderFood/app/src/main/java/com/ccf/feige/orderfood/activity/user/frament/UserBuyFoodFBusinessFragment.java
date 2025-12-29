package com.ccf.feige.orderfood.activity.user.frament;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.adapter.UserBuyFoodLIstAdapter;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.dao.FoodDao;
import java.util.ArrayList;
import java.util.List;

/**
 * 商家商品列表Fragment
 * 修复点：
 * 1. 解决RecyclerView滑动失效（LayoutManager+布局高度+嵌套滑动）
 * 2. 移除月销残留，避免闪退
 * 3. 优化滑动性能（RecycledViewPool）
 * 4. 强化空指针防护，规避生命周期异常
 */
public class UserBuyFoodFBusinessFragment extends Fragment {

    // 根布局视图，用于缓存Fragment加载的布局，后续查找控件、刷新数据时复用
    private View rootview;
    // 商家唯一标识ID，用于查询该商家对应的商品列表
    private String businessId;
    // 上下文对象，用于适配初始化、控件操作等，关联宿主Activity
    private Context mContext;

    /**
     * Fragment默认构造方法
     * 遵循Fragment创建规范，保留无参构造，避免系统重建时出现异常
     */
    public UserBuyFoodFBusinessFragment() {
        super();
    }

    /**
     * Fragment静态创建方法（推荐最佳实践）
     * 用于传递商家ID参数，避免直接通过构造方法传参导致的重建数据丢失问题
     * @param businessId 商家唯一标识ID
     * @return 初始化完成的UserBuyFoodFBusinessFragment实例
     */
    public static UserBuyFoodFBusinessFragment newInstance(String businessId) {
        // 创建当前Fragment实例
        UserBuyFoodFBusinessFragment fragment = new UserBuyFoodFBusinessFragment();
        // 新建Bundle对象，用于存储传递的参数
        Bundle args = new Bundle();
        // 将商家ID存入Bundle，使用常量键"BUSINESS_ID"标识，便于后续取值
        args.putString("BUSINESS_ID", businessId);
        // 将Bundle设置为Fragment的参数集
        fragment.setArguments(args);
        // 返回初始化完成的Fragment实例
        return fragment;
    }

    /**
     * Fragment生命周期方法：创建时调用
     * 主要用于初始化数据、获取传递的参数、初始化上下文等
     * @param savedInstanceState 保存的Fragment状态，用于重建时恢复数据
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // 调用父类的onCreate方法，遵循生命周期规范
        super.onCreate(savedInstanceState);
        // 判空防护：避免getArguments()返回null导致空指针异常
        if (getArguments() != null) {
            // 从Bundle参数集中获取商家ID，赋值给成员变量businessId
            businessId = getArguments().getString("BUSINESS_ID");
        }
        // 初始化上下文对象，关联当前Fragment的宿主Activity
        mContext = getActivity();
    }

    /**
     * Fragment生命周期方法：创建视图时调用
     * 主要用于加载布局、查找控件、初始化RecyclerView、绑定数据等
     * @param inflater 布局填充器，用于将xml布局转换为View对象
     * @param container 父容器，用于承载Fragment的布局
     * @param savedInstanceState 保存的Fragment状态，用于重建时恢复视图
     * @return Fragment的根布局视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载Fragment对应的布局文件，转换为View对象并赋值给根布局变量
        // 第三个参数为false，表示不自动将布局添加到父容器，由FragmentManager统一管理
        rootview = inflater.inflate(R.layout.fragment_user_buy_food, container, false);

        // 从根布局中查找RecyclerView控件，用于展示商家商品列表
        RecyclerView recyclerView = rootview.findViewById(R.id.user_buy_food_listView);
        // 空指针防护：如果RecyclerView查找失败（控件id错误或布局未加载），直接返回根布局，避免后续操作崩溃
        if (recyclerView == null) {
            return rootview;
        }

        // ========== 核心新增：强制开启嵌套滑动（ViewPager2嵌套必加） ==========
        // 开启RecyclerView的嵌套滑动功能，解决在ViewPager2（横向滑动容器）中嵌套时，RecyclerView（纵向滑动）的滑动冲突问题
        recyclerView.setNestedScrollingEnabled(true);
        // 显示垂直滚动条，用于直观验证RecyclerView是否有可滚动空间（调试辅助）
        recyclerView.setVerticalScrollBarEnabled(true);
        // 为RecyclerView设置触摸事件监听器，解决与父布局（ViewPager2）的滑动事件拦截冲突
        recyclerView.setOnTouchListener((v, event) -> {
            // 关键：告诉父布局（ViewPager2）不要拦截当前RecyclerView的触摸事件，保证纵向滑动事件能被RecyclerView消费
            v.getParent().requestDisallowInterceptTouchEvent(true);
            // 按Android触摸事件的正常流程处理当前事件
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    // 手指抬起时，恢复父布局的事件拦截能力，避免影响ViewPager2的横向滑动切换功能
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            // 返回false，表示不消费触摸事件，让RecyclerView的默认滑动逻辑正常执行
            return false;
        });

        // 配置RecyclerView的布局管理器：LinearLayoutManager（线性布局管理器）
        // 用于控制RecyclerView的子项排列方式，此处关联上下文对象初始化
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        // 设置布局管理器的排列方向为垂直方向，实现商品列表的纵向排列
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // 将配置好的布局管理器设置给RecyclerView，RecyclerView必须设置布局管理器才能正常显示子项
        recyclerView.setLayoutManager(layoutManager);

        // 性能优化：使用RecycledViewPool复用RecyclerView的子项View，减少View的创建和销毁，提升滑动流畅度
        // 创建View复用池实例
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        // 设置复用池的最大复用数量：第1个参数0表示默认视图类型，第2个参数20表示最多缓存20个该类型的子项View
        viewPool.setMaxRecycledViews(0, 20);
        // 将View复用池设置给RecyclerView，启用子项View复用功能
        recyclerView.setRecycledViewPool(viewPool);

        // 数据查询+日志（验证商品数量）
        // 调用FoodDao的数据访问方法，根据商家ID查询该商家对应的所有商品列表
        List<FoodBean> foodList = FoodDao.getAllFoodListByBusinessId(businessId);
        // 空指针防护：如果查询结果为null（数据库查询失败或无数据），初始化一个空的ArrayList，避免后续适配器初始化崩溃
        if (foodList == null) {
            foodList = new ArrayList<>();
        }
        // 新增日志：打印商家ID和对应的商品数量，用于调试排查（在Logcat中搜索标签"FOOD_SCROLL"即可查看）
        Log.d("FOOD_SCROLL", "商家ID：" + businessId + "，商品数量：" + foodList.size());

        // 绑定适配器：创建商品列表适配器实例，传入商品数据列表和上下文对象
        UserBuyFoodLIstAdapter adapter = new UserBuyFoodLIstAdapter(foodList, mContext);
        // 将适配器设置给RecyclerView，完成数据与视图的绑定，RecyclerView将通过适配器展示商品数据
        recyclerView.setAdapter(adapter);

        // 返回Fragment的根布局视图，完成视图创建
        return rootview;
    }

    /**
     * 商品列表刷新方法
     * 用于外部调用（如商品数据变更、购物车数量更新后），刷新当前Fragment的商品列表展示
     */
    public void refreshFoodList() {
        // 多层空指针防护1：根布局或上下文对象为null（Fragment未创建或已销毁），直接返回，避免操作崩溃
        if (rootview == null || mContext == null) {
            return;
        }
        // 从根布局中重新查找RecyclerView控件
        RecyclerView recyclerView = rootview.findViewById(R.id.user_buy_food_listView);
        // 多层空指针防护2：RecyclerView查找失败，直接返回，避免后续操作崩溃
        if (recyclerView == null) {
            return;
        }
        // 重新查询最新的商品列表数据，保证展示的数据为最新状态
        List<FoodBean> newFoodList = FoodDao.getAllFoodListByBusinessId(businessId);
        // 空指针防护：查询结果为null时，初始化空的ArrayList，避免适配器初始化崩溃
        if (newFoodList == null) {
            newFoodList = new ArrayList<>();
        }
        // 创建新的适配器实例，传入最新的商品数据
        UserBuyFoodLIstAdapter adapter = new UserBuyFoodLIstAdapter(newFoodList, mContext);
        // 重新设置适配器，刷新RecyclerView的展示内容
        recyclerView.setAdapter(adapter);
    }
}