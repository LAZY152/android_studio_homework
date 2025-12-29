package com.ccf.feige.orderfood.activity.user.frament;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.ManageManActivity;
import com.ccf.feige.orderfood.activity.man.ManageManOrderNoFinishActivity;
import com.ccf.feige.orderfood.activity.man.adapter.OrderNoFinishIstAdapter;
import com.ccf.feige.orderfood.activity.user.adapter.OrderNoFinishUserAdapter;
import com.ccf.feige.orderfood.activity.user.adapter.UserFoodLIstAdapter;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.dao.FoodDao;
import com.ccf.feige.orderfood.dao.OrderDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.List;

/**
 * 用户未完成订单碎片页面
 * 功能：展示当前登录用户的所有未完成（状态为1）订单，并提供订单搜索筛选功能
 */
public class UserNoFinishOrderFragment extends Fragment {

    // 碎片的根视图，用于承载整个布局的所有控件
    View rootview;//根石头（注：此处为原文注释保留，推测应为“根视图”）

    /**
     * 创建碎片视图的核心方法
     * 负责加载布局、初始化控件、获取数据并绑定适配器、设置搜索监听
     * @param inflater 布局填充器，用于将xml布局转换为View对象
     * @param container 碎片的容器视图，即承载该碎片的父布局
     * @param savedInstanceState 保存的实例状态，用于恢复碎片数据（如屏幕旋转后的数据恢复）
     * @return 碎片的根视图rootview
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // 加载当前碎片对应的布局文件，绑定到根视图，false表示不立即将该视图添加到container容器中
        rootview=inflater.inflate(R.layout.fragment_manage_user_order_no_finish, container, false);

        // 1. 初始化ListView控件，用于展示未完成订单列表
        ListView listView= rootview.findViewById(R.id.user_my_order_no_finish_listView);
        // 2. 获取当前登录用户的账号（通过Tools工具类的静态方法获取）
        String account= Tools.getOnAccount(rootview.getContext());
        // 3. 定义订单状态为"1"，代表未完成状态（该状态值与业务逻辑中订单状态定义对应）
        String sta="1";
        // 4. 从订单数据库中查询当前用户、未完成状态的所有订单列表
        List<OrderBean> list = OrderDao.getAllOrdersByStaAndUser(account,sta);
        //List<OrderBean> list = OrderDao.getAllOrders();// 注：此处为备用查询所有订单的代码，已注释保留
        // 5. 初始化订单列表适配器，将上下文和查询到的订单列表传入适配器
        OrderNoFinishUserAdapter orderNoFinishIstAdapter=new OrderNoFinishUserAdapter(rootview.getContext(),list);

        // 6. 判断订单列表是否为空，进行适配器绑定处理
        if(list==null||list.size()==0){
            // 若列表为空或为null，设置ListView适配器为null，不展示任何数据
            listView.setAdapter(null);
        }else{
            // 若列表有数据，将初始化好的适配器绑定到ListView，展示订单数据
            listView.setAdapter(orderNoFinishIstAdapter);

        }

        // 7. 初始化SearchView搜索控件，用于筛选订单
        SearchView searchView=rootview.findViewById(R.id.user_my_order_no_finish_search);
        // 8. 为SearchView设置查询文本监听事件，处理搜索提交和文本变化事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 搜索提交事件（点击搜索按钮/回车键时触发）
             * @param query 搜索输入的文本内容
             * @return true 表示已处理该事件，不再向上传递
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 重新查询当前用户未完成状态的所有订单
                List<OrderBean> list = OrderDao.getAllOrdersByStaAndUser(account,sta);
                // 调用Tools工具类的筛选方法，根据搜索文本过滤订单列表
                List<OrderBean> list1 = Tools.filterOrder(list, query);
                // 重新初始化适配器，传入过滤后的订单列表
                OrderNoFinishUserAdapter orderNoFinishIstAdapter=new OrderNoFinishUserAdapter(rootview.getContext(),list1);
                // 再次判断列表是否为空，更新ListView的适配器
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }

            /**
             * 搜索文本变化事件（输入框文本每改变一次就触发，实时筛选）
             * @param newText 输入框中最新的文本内容
             * @return true 表示已处理该事件，不再向上传递
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                // 重新查询当前用户未完成状态的所有订单
                List<OrderBean> list = OrderDao.getAllOrdersByStaAndUser(account,sta);
                // 调用Tools工具类的筛选方法，根据最新输入文本过滤订单列表
                List<OrderBean> list1 = Tools.filterOrder(list, newText);
                // 重新初始化适配器，传入过滤后的订单列表
                OrderNoFinishUserAdapter orderNoFinishIstAdapter=new OrderNoFinishUserAdapter(rootview.getContext(),list1);
                // 判断列表是否为空，更新ListView的适配器，实现实时刷新筛选结果
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }
        });

        // 返回碎片的根视图，完成视图创建
        return rootview;
    }
}