package com.ccf.feige.orderfood.activity.man;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.OrderFinishIstAdapter;
import com.ccf.feige.orderfood.activity.man.adapter.OrderNoFinishIstAdapter;
import com.ccf.feige.orderfood.bean.OrderBean;
import com.ccf.feige.orderfood.dao.OrderDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.List;

/**
 * 管理员端 - 已完成订单管理页面
 * 负责展示当前登录管理员对应的所有已完成订单，并提供订单搜索功能
 */
public class ManageManOrderFinishActivity extends AppCompatActivity {

    /**
     * 页面创建时执行的初始化方法
     * @param savedInstanceState 保存的页面状态数据（用于页面恢复）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置当前页面的布局文件（关联XML界面）
        setContentView(R.layout.activity_manage_man_order_finish);
        // 查找布局中的Toolbar控件（顶部导航栏）
        Toolbar oks=findViewById(R.id.man_my_order_finish_bar);
        // 为Toolbar设置点击事件监听器
        oks.setOnClickListener(new View.OnClickListener() {
            /**
             * Toolbar被点击时执行的逻辑
             * @param v 被点击的视图对象（此处即Toolbar）
             */
            @Override
            public void onClick(View v) {
                // 创建意图对象，用于跳转到管理员主页面
                Intent intent=new Intent(ManageManOrderFinishActivity.this,ManageManActivity.class);
                // 向意图中添加额外参数，标记页面跳转状态为"1"
                intent.putExtra("sta","1");
                // 启动页面跳转
                startActivity(intent);
            }
        });

        // 查找布局中的ListView控件（用于展示已完成订单列表）
        ListView listView= findViewById(R.id.man_my_order_finish_listView);
        // 通过工具类获取当前登录的账号信息
        String account= Tools.getOnAccount(this);

        // 从订单数据库访问对象中，获取当前账号对应的所有已完成订单列表
        List<OrderBean> list = OrderDao.getAllOrdersFinish(account);
        //List<OrderBean> list = OrderDao.getAllOrders(); // 注释：获取所有订单（未启用）
        // 创建已完成订单列表的适配器，关联当前上下文和订单数据
        OrderFinishIstAdapter orderNoFinishIstAdapter=new OrderFinishIstAdapter(this,list);

        // 判断订单列表是否为空或无数据
        if(list==null||list.size()==0){
            // 若无数据，设置ListView的适配器为null（不展示任何内容）
            listView.setAdapter(null);
        }else{
            // 若有数据，设置ListView的适配器为创建好的订单适配器（展示订单列表）
            listView.setAdapter(orderNoFinishIstAdapter);
        }

        // 查找布局中的SearchView控件（用于订单搜索）
        SearchView searchView=findViewById(R.id.man_my_order_finish_search);
        // 为SearchView设置查询文本监听事件（监听搜索输入和提交）
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 搜索文本提交时（点击搜索按钮/回车）执行的逻辑
             * @param query 提交的搜索关键词
             * @return boolean 返回true表示已处理该事件，不再向上传递
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 重新获取当前账号对应的所有已完成订单列表
                List<OrderBean> list = OrderDao.getAllOrdersFinish(account);
                // 通过工具类过滤订单列表，筛选出包含搜索关键词的订单
                List<OrderBean> list1 = Tools.filterOrder(list, query);
                // 创建新的订单适配器，关联筛选后的订单数据
                OrderFinishIstAdapter orderNoFinishIstAdapter=new OrderFinishIstAdapter(ManageManOrderFinishActivity.this,list1);
                // 判断筛选后的订单列表是否为空或无数据
                if(list==null||list.size()==0){
                    // 若无匹配数据，设置ListView适配器为null
                    listView.setAdapter(null);
                }else{
                    // 若有匹配数据，设置ListView适配器为筛选后的订单适配器（展示搜索结果）
                    listView.setAdapter(orderNoFinishIstAdapter);
                }
                // 返回true表示事件已处理
                return true;
            }

            /**
             * 搜索文本发生变化时（输入/删除字符）实时执行的逻辑
             * @param newText 变化后的搜索输入文本
             * @return boolean 返回true表示已处理该事件，不再向上传递
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                // 重新获取当前账号对应的所有已完成订单列表
                List<OrderBean> list = OrderDao.getAllOrdersFinish(account);
                // 通过工具类实时过滤订单列表，匹配当前输入的文本
                List<OrderBean> list1 = Tools.filterOrder(list, newText);
                // 创建新的订单适配器，关联实时筛选后的订单数据
                OrderFinishIstAdapter orderNoFinishIstAdapter=new OrderFinishIstAdapter(ManageManOrderFinishActivity.this,list1);
                // 判断筛选后的订单列表是否为空或无数据
                if(list==null||list.size()==0){
                    // 若无匹配数据，设置ListView适配器为null
                    listView.setAdapter(null);
                }else{
                    // 若有匹配数据，设置ListView适配器为实时筛选后的订单适配器（展示实时搜索结果）
                    listView.setAdapter(orderNoFinishIstAdapter);
                }
                // 返回true表示事件已处理
                return true;
            }
        });

    }
}