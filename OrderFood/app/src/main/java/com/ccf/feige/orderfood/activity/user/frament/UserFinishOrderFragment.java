package com.ccf.feige.orderfood.activity.user.frament;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.ManageUserActivity;
import com.ccf.feige.orderfood.activity.user.adapter.OrderFinishUserAdapter;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.dao.OrderDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.List;

/**
 * 用户已完成订单列表Fragment（评论后刷新列表，更新按钮状态）
 * 功能说明：展示当前登录用户的所有已完成订单，支持搜索筛选，评论操作后返回该页面自动刷新订单状态
 */
public class UserFinishOrderFragment extends Fragment {
    // 成员变量
    private View rootview; // Fragment的根视图，承载整个布局的所有控件
    private ListView listView; // 订单列表展示控件，用于加载已完成订单数据
    private OrderFinishUserAdapter mAdapter; // 已完成订单列表的适配器，用于绑定数据和视图
    private String account; // 当前登录用户账号，用于查询该用户的专属订单数据
    private SearchView searchView; // 搜索框控件，用于输入关键词筛选订单
    private ImageView imgBack; // 返回按钮控件，用于跳转回上一个页面（我的页面）

    /**
     * Fragment生命周期方法：创建视图
     * 负责加载布局、初始化各项功能，是Fragment视图创建的核心入口
     * @param inflater 布局填充器，用于将xml布局转换为View对象
     * @param container 父容器视图，用于承载Fragment的布局
     * @param savedInstanceState 保存状态的Bundle对象，用于恢复Fragment之前的状态
     * @return Fragment的根视图rootview
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 填充布局：将fragment_manage_user_order_finish.xml布局转换为View对象，并赋值给根视图
        // 第三个参数设为false，表示不自动将该视图添加到父容器中，由FragmentManager统一管理
        rootview = inflater.inflate(R.layout.fragment_manage_user_order_finish, container, false);

        // 1. 初始化控件：绑定布局中的所有控件，建立Java对象与xml控件的关联
        initView();

        // 2. 初始化用户账号：从工具类中获取当前已登录用户的账号，为后续查询订单做准备
        account = Tools.getOnAccount(rootview.getContext());

        // 3. 初始化订单列表（首次加载）：传入null表示不进行搜索筛选，加载该用户所有已完成订单
        refreshOrderList(null);

        // 4. 初始化返回按钮事件：为返回按钮绑定点击事件，实现页面跳转逻辑
        initBackBtn();

        // 5. 初始化搜索功能：为搜索框绑定监听事件，实现关键词筛选订单的功能
        initSearchView();

        // 返回根视图，完成Fragment视图的创建
        return rootview;
    }

    /**
     * 初始化控件
     * 功能：通过根视图的findViewById方法，找到布局中所有需要操作的控件，并赋值给对应的成员变量
     * 注意：必须在布局填充完成后（rootview赋值后）调用，否则会出现空指针异常
     */
    private void initView() {
        // 绑定订单列表ListView控件
        listView = rootview.findViewById(R.id.user_my_order_finish_listView);
        // 绑定订单搜索框SearchView控件
        searchView = rootview.findViewById(R.id.user_my_order_finish_search);
        // 绑定返回按钮ImageView控件
        imgBack = rootview.findViewById(R.id.user_my_order_finish_back);
    }

    /**
     * 初始化返回按钮事件（保留原有逻辑）
     * 功能：为返回按钮设置点击监听，点击后跳转回ManageUserActivity的"我的"页面
     * 注意：通过getActivity()获取依附的Activity，需要进行非空判断避免空指针异常
     */
    private void initBackBtn() {
        // 获取当前Fragment依附的Activity，并强制转换为ManageUserActivity（该Fragment专属的宿主Activity）
        ManageUserActivity man = (ManageUserActivity) getActivity();
        // 非空判断：避免Activity已销毁时出现空指针异常
        if (man == null) {
            return;
        }
        // 为返回按钮设置点击事件监听器
        imgBack.setOnClickListener(v -> {
            // 调用宿主Activity的showMy()方法，跳转到"我的"页面
            man.showMy();
        });
    }

    /**
     * 初始化搜索功能
     * 功能：为搜索框绑定查询文本监听，实现两种搜索场景：
     * 1. 提交搜索（点击搜索按钮/回车）
     * 2. 实时搜索（输入文本变化时）
     * 最终都会触发订单列表的刷新和筛选
     */
    private void initSearchView() {
        // 为SearchView设置查询文本监听器，监听文本提交和文本变化事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 搜索文本提交时触发（点击搜索图标/回车键）
             * @param query 输入的搜索关键词
             * @return true 表示已处理该事件，不再向上传递
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 刷新订单列表，传入提交的关键词进行筛选
                refreshOrderList(query);
                return true;
            }

            /**
             * 搜索文本变化时触发（输入/删除字符时实时触发）
             * @param newText 变化后的搜索文本
             * @return true 表示已处理该事件，不再向上传递
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                // 刷新订单列表，传入变化后的文本进行实时筛选
                refreshOrderList(newText);
                return true;
            }
        });
    }

    /**
     * 刷新订单列表（支持搜索筛选）
     * 核心功能方法：负责查询订单数据、筛选数据、更新列表展示，是整个Fragment数据流转的核心
     * @param keyword 搜索关键词（null表示不筛选，加载全部已完成订单）
     */
    private void refreshOrderList(String keyword) {
        // 1. 查询用户所有已完成订单：通过OrderDao查询当前用户（account）状态为"1"（已完成）的所有订单
        // 返回订单数据列表，用于后续筛选和展示
        List<OrderBean> originalList = OrderDao.getAllOrdersByStaAndUserFinish(account, "1");

        // 2. 搜索筛选：初始化筛选后列表为原始列表，后续根据关键词判断是否需要筛选
        List<OrderBean> filteredList = originalList;
        // 筛选条件判断：关键词非空且非空白、原始列表非空且非空列表，满足条件才进行筛选
        if (keyword != null && !keyword.trim().isEmpty() && originalList != null && !originalList.isEmpty()) {
            // 调用工具类的filterOrder方法，根据关键词筛选订单列表
            filteredList = Tools.filterOrder(originalList, keyword);
        }

        // 3. 更新适配器：绑定筛选后的数据到列表，更新UI展示
        // 初始化适配器，传入当前上下文和筛选后的订单列表
        mAdapter = new OrderFinishUserAdapter(getContext(), filteredList);
        // 判断筛选后列表是否为空，为空则清空ListView的适配器（展示空页面），否则设置适配器（展示订单数据）
        if (filteredList == null || filteredList.isEmpty()) {
            // 列表为空时，设置适配器为null，清空ListView展示
            listView.setAdapter(null);
        } else {
            // 列表非空时，为ListView设置适配器，展示筛选后的订单数据
            listView.setAdapter(mAdapter);
        }
    }

    /**
     * 核心：Fragment恢复可见时刷新列表（评论后返回立即更新按钮状态）
     * Fragment生命周期方法：当Fragment从不可见变为可见（如从评论页面返回）时触发
     * 重写该方法实现自动刷新，保证评论操作后订单的按钮状态（如"评论"→"已评论"）能实时更新
     */
    @Override
    public void onResume() {
        // 调用父类的onResume方法，保证Fragment生命周期的正常流转
        super.onResume();
        // 刷新订单列表，传入null表示加载全部已完成订单，更新最新的订单状态
        refreshOrderList(null);
    }
}