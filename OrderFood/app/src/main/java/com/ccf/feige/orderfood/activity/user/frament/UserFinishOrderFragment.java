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
 */
public class UserFinishOrderFragment extends Fragment {
    // 成员变量
    private View rootview;
    private ListView listView;
    private OrderFinishUserAdapter mAdapter;
    private String account; // 当前登录用户账号
    private SearchView searchView;
    private ImageView imgBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 填充布局
        rootview = inflater.inflate(R.layout.fragment_manage_user_order_finish, container, false);

        // 1. 初始化控件
        initView();

        // 2. 初始化用户账号
        account = Tools.getOnAccount(rootview.getContext());

        // 3. 初始化订单列表（首次加载）
        refreshOrderList(null);

        // 4. 初始化返回按钮事件
        initBackBtn();

        // 5. 初始化搜索功能
        initSearchView();

        return rootview;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        listView = rootview.findViewById(R.id.user_my_order_finish_listView);
        searchView = rootview.findViewById(R.id.user_my_order_finish_search);
        imgBack = rootview.findViewById(R.id.user_my_order_finish_back);
    }

    /**
     * 初始化返回按钮事件（保留原有逻辑）
     */
    private void initBackBtn() {
        ManageUserActivity man = (ManageUserActivity) getActivity();
        if (man == null) {
            return;
        }
        imgBack.setOnClickListener(v -> man.showMy());
    }

    /**
     * 初始化搜索功能
     */
    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshOrderList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshOrderList(newText);
                return true;
            }
        });
    }

    /**
     * 刷新订单列表（支持搜索筛选）
     * @param keyword 搜索关键词（null表示不筛选）
     */
    private void refreshOrderList(String keyword) {
        // 1. 查询用户所有订单
        List<OrderBean> originalList = OrderDao.getAllOrdersByStaAndUserFinish(account, "1");

        // 2. 搜索筛选
        List<OrderBean> filteredList = originalList;
        if (keyword != null && !keyword.trim().isEmpty() && originalList != null && !originalList.isEmpty()) {
            filteredList = Tools.filterOrder(originalList, keyword);
        }

        // 3. 更新适配器
        mAdapter = new OrderFinishUserAdapter(getContext(), filteredList);
        if (filteredList == null || filteredList.isEmpty()) {
            listView.setAdapter(null);
        } else {
            listView.setAdapter(mAdapter);
        }
    }

    /**
     * 核心：Fragment恢复可见时刷新列表（评论后返回立即更新按钮状态）
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshOrderList(null);
    }
}