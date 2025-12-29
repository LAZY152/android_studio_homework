package com.ccf.feige.orderfood.activity.user.frament;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.FoodLIstAdapter;
import com.ccf.feige.orderfood.activity.user.adapter.UserFoodLIstAdapter;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.dao.FoodDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.List;

/**
 * 用户首页碎片（Fragment）
 * 功能：展示所有食品列表，并提供食品搜索功能，是用户端查看食品的核心界面
 */
public class UserHomeFragment extends Fragment {

    // 声明Fragment的根视图对象，用于缓存加载后的布局视图，避免重复查找
    View rootview;

    /**
     * 重写Fragment的视图创建方法
     * 负责加载Fragment布局、初始化控件、绑定数据适配器和设置搜索监听
     * @param inflater 布局填充器，用于将xml布局转换为View对象
     * @param container 父容器视图，用于承载当前Fragment的布局
     * @param savedInstanceState 保存的实例状态，用于恢复Fragment的历史数据
     * @return 返回Fragment加载完成后的根视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // 将fragment_user_home.xml布局文件填充为View对象，并赋值给根视图，第三个参数false表示不自动将布局添加到父容器
        rootview=inflater.inflate(R.layout.fragment_user_home, container, false);

        // 从根视图中查找食品列表展示的ListView控件，通过控件ID匹配
        ListView listView = rootview.findViewById(R.id.user_home_food_listView);

        // 调用FoodDao的数据访问方法，获取所有的食品数据列表，返回封装好的FoodBean集合
        List<FoodBean> list = FoodDao.getAllFoodList();
        // 初始化用户端食品列表适配器，传入当前Fragment的上下文（getActivity()获取关联的Activity上下文）和食品数据列表
        UserFoodLIstAdapter adapter = new UserFoodLIstAdapter(getContext(), list);
        // 判空处理：如果食品列表为null或者列表长度为0（无数据）
        if(list==null||list.size()==0){
            // 给ListView设置空适配器，清空列表展示，避免出现空指针异常或无效展示
            listView.setAdapter(null);
        }else{
            // 食品列表有数据时，给ListView设置初始化好的适配器，展示食品数据
            listView.setAdapter(adapter);
        }

        // 从根视图中查找搜索框控件，通过控件ID匹配
        SearchView searchView = rootview.findViewById(R.id.user_home_food_search);
        // 给搜索框设置查询文本监听事件，监听搜索内容的提交和变化
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 搜索文本提交事件（点击搜索按钮、回车键触发）
             * @param query 提交的搜索关键词文本
             * @return 返回false表示不消耗该事件，允许后续事件传递；返回true表示消耗该事件，终止传递
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 根据提交的搜索关键词（食品标题），调用FoodDao的查询方法，获取匹配的食品列表
                List<FoodBean> list = FoodDao.getAllFoodListUser(query);
                // 初始化新的适配器，传入上下文和查询到的匹配食品列表
                UserFoodLIstAdapter adapter = new UserFoodLIstAdapter(getContext(), list);
                // 判空处理：如果匹配结果列表为null或者无数据
                if(list==null||list.size()==0){
                    // 给ListView设置空适配器，清空原有列表，展示无匹配数据的状态
                    listView.setAdapter(null);
                }else{
                    // 有匹配数据时，给ListView设置新适配器，展示搜索结果
                    listView.setAdapter(adapter);
                }

                // 返回false，不消耗该提交事件
                return false;
            }

            /**
             * 搜索文本变化事件（输入框内容实时改变时触发，每输入一个字符都会触发）
             * @param newText 实时变化的搜索文本内容
             * @return 返回false表示不消耗该事件，允许后续事件传递
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                // 根据实时变化的搜索文本，调用FoodDao的查询方法，实时获取匹配的食品列表
                List<FoodBean> list = FoodDao.getAllFoodListUser(newText);
                // 初始化新的适配器，传入上下文和实时匹配的食品列表
                UserFoodLIstAdapter adapter = new UserFoodLIstAdapter(getContext(), list);
                // 判空处理：如果实时匹配结果列表为null或者无数据
                if(list==null||list.size()==0){
                    // 给ListView设置空适配器，清空原有列表
                    listView.setAdapter(null);
                }else{
                    // 有实时匹配数据时，给ListView设置新适配器，实时更新搜索结果展示
                    listView.setAdapter(adapter);
                }
                // 返回false，不消耗该文本变化事件
                return false;
            }
        });

        // 返回Fragment的根视图，完成视图创建和初始化
        return rootview;
    }
}