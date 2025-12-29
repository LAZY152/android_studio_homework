package com.ccf.feige.orderfood.activity.man.frament;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.widget.ListView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.FoodLIstAdapter;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.dao.FoodDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.List;

/**
 * 商家端首页Fragment
 * 功能：展示当前商家的所有食品列表，并提供食品搜索功能
 */
public class ManageHomeFragment extends Fragment {

    // Fragment的根视图
    View rootview;

    /**
     * Fragment生命周期方法：创建并返回Fragment的视图
     * @param inflater 布局填充器，用于加载布局文件
     * @param container 父容器，用于承载Fragment的视图
     * @param savedInstanceState 保存的实例状态，用于恢复数据
     * @return Fragment的根视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // 加载当前Fragment对应的布局文件，初始化根视图
        rootview=inflater.inflate(R.layout.fragment_manage_home, container, false);

        // 接下来要写的是适配器
        // 获取布局中的ListView控件，用于展示食品列表
        ListView listView = rootview.findViewById(R.id.man_home_food_listView);

        // 1. 通过工具类获取当前登录商家账号，再从数据库查询该商家的所有食品列表
        List<FoodBean> list = FoodDao.getAllFoodListByBusinessId(Tools.getOnAccount(getContext()));
        // 2. 创建食品列表适配器，绑定上下文和食品数据
        FoodLIstAdapter adapter = new FoodLIstAdapter(getContext(), list);
        // 3. 判空处理：如果列表为空或为null，设置适配器为null，否则设置创建好的适配器
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(adapter);
        }

        // 实现数据账号共享：通过工具类获取当前登录的商家账号
        String account=Tools.getOnAccount(getContext());

        // 获取布局中的SearchView控件，用于食品搜索
        SearchView searchView = rootview.findViewById(R.id.man_home_food_search);
        // 设置SearchView的查询文本监听，处理搜索相关事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 搜索文本提交时触发（点击搜索按钮/回车）
             * @param query 输入的搜索关键词
             * @return 布尔值，是否消耗该事件
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 根据商家账号和搜索关键词，从数据库查询匹配的食品列表
                List<FoodBean> list = FoodDao.getAllFoodList(account,query);
                // 创建新的适配器，绑定查询到的匹配数据
                FoodLIstAdapter adapter = new FoodLIstAdapter(getContext(), list);
                // 判空处理：更新ListView的适配器，展示搜索结果（无结果则清空列表）
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(adapter);
                }

                return false;
            }

            /**
             * 搜索文本变化时实时触发（输入框内容改变）
             * @param newText 变化后的搜索文本
             * @return 布尔值，是否消耗该事件
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                // 实时根据商家账号和当前输入文本，查询匹配的食品列表
                List<FoodBean> list = FoodDao.getAllFoodList(account,newText);
                // 创建新的适配器，绑定实时查询到的数据
                FoodLIstAdapter adapter = new FoodLIstAdapter(getContext(), list);
                // 判空处理：实时更新ListView，展示动态搜索结果
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(adapter);
                }
                return false;
            }
        });

        // 返回Fragment的根视图，完成视图创建
        return rootview;
    }
}