package com.ccf.feige.orderfood.activity.man;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.OrderNoFinishIstAdapter;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.bean.OrderDetailBean;
import com.ccf.feige.orderfood.dao.OrderDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 显示所有的订单，未完成的订单
 */
public class ManageManOrderNoFinishActivity extends AppCompatActivity {

    /**
     * 页面创建时执行的初始化方法，完成视图绑定、数据加载、事件监听设置
     * @param savedInstanceState 保存的页面状态数据，用于页面重建时恢复状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定当前页面对应的布局文件
        setContentView(R.layout.activity_manage_man_order_no_finish);

        // 初始化ListView控件，用于展示未完成订单列表
        ListView listView= findViewById(R.id.man_my_order_no_finish_listView);
        // 获取当前登录用户的账号信息
        String account=Tools.getOnAccount(this);
        // 定义订单状态标识，"1"代表未完成状态
        String sta="1";
        // 从数据库中查询当前用户的所有未完成订单
        List<OrderBean> list = OrderDao.getAllOrdersBySta(account,sta);
        //List<OrderBean> list = OrderDao.getAllOrders(); // 注释：查询所有订单的备用方法
        // 初始化订单列表适配器，将上下文和订单数据传入
        OrderNoFinishIstAdapter orderNoFinishIstAdapter=new OrderNoFinishIstAdapter(this,list);

        // 判断订单列表是否为空，为空则设置ListView无适配器（不展示数据），否则设置适配器展示订单
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(orderNoFinishIstAdapter);

        }

        // 初始化搜索框控件，用于订单搜索筛选
        SearchView searchView=findViewById(R.id.man_my_order_no_finish_search);
        // 为搜索框设置查询文本监听事件，处理搜索提交和文本变化事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 搜索文本提交时触发（点击搜索按钮/回车）
             * @param query 输入的搜索关键词
             * @return boolean 返回true表示已处理该事件
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 重新从数据库获取当前用户的所有未完成订单
                List<OrderBean> list = OrderDao.getAllOrdersBySta(account,sta);
                // 根据搜索关键词筛选订单列表
                List<OrderBean> list1 = Tools.filterOrder(list, query);
                // 用筛选后的订单数据初始化新的适配器
                OrderNoFinishIstAdapter orderNoFinishIstAdapter=new OrderNoFinishIstAdapter(ManageManOrderNoFinishActivity.this,list1);
                // 根据筛选后的列表是否为空，设置ListView的适配器展示对应数据
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }

            /**
             * 搜索框文本内容变化时实时触发（输入/删除字符时）
             * @param newText 变化后的搜索文本内容
             * @return boolean 返回true表示已处理该事件
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                // 重新从数据库获取当前用户的所有未完成订单
                List<OrderBean> list = OrderDao.getAllOrdersBySta(account,sta);
                // 根据实时变化的文本筛选订单列表
                List<OrderBean> list1 = Tools.filterOrder(list, newText);
                // 用筛选后的订单数据初始化新的适配器
                OrderNoFinishIstAdapter orderNoFinishIstAdapter=new OrderNoFinishIstAdapter(ManageManOrderNoFinishActivity.this,list1);
                // 根据筛选后的列表是否为空，设置ListView的适配器展示对应数据
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }
        });

        // 初始化工具栏控件，作为页面返回/跳转入口
        Toolbar oks=findViewById(R.id.man_my_order_no_finish_bar);
        // 为工具栏设置点击事件监听，实现页面跳转
        oks.setOnClickListener(new View.OnClickListener() {
            /**
             * 工具栏点击时触发
             * @param v 被点击的视图控件（此处为Toolbar）
             */
            @Override
            public void onClick(View v) {
                // 创建意图对象，指定从当前页面跳转到管理主页面
                Intent intent=new Intent(ManageManOrderNoFinishActivity.this,ManageManActivity.class);
                // 携带订单状态参数"1"传递到目标页面
                intent.putExtra("sta","1");
                // 执行页面跳转
                startActivity(intent);
            }
        });
    }
}