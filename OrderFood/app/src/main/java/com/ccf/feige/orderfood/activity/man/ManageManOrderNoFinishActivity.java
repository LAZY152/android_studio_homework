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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_man_order_no_finish);


       ListView listView= findViewById(R.id.man_my_order_no_finish_listView);
       String account=Tools.getOnAccount(this);
       String sta="1";
        List<OrderBean> list = OrderDao.getAllOrdersBySta(account,sta);
        //List<OrderBean> list = OrderDao.getAllOrders();
        OrderNoFinishIstAdapter orderNoFinishIstAdapter=new OrderNoFinishIstAdapter(this,list);

        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(orderNoFinishIstAdapter);

        }

        SearchView searchView=findViewById(R.id.man_my_order_no_finish_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                List<OrderBean> list = OrderDao.getAllOrdersBySta(account,sta);

                List<OrderBean> list1 = Tools.filterOrder(list, query);

                OrderNoFinishIstAdapter orderNoFinishIstAdapter=new OrderNoFinishIstAdapter(ManageManOrderNoFinishActivity.this,list1);
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<OrderBean> list = OrderDao.getAllOrdersBySta(account,sta);
                List<OrderBean> list1 = Tools.filterOrder(list, newText);
                OrderNoFinishIstAdapter orderNoFinishIstAdapter=new OrderNoFinishIstAdapter(ManageManOrderNoFinishActivity.this,list1);

                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }
        });

        Toolbar oks=findViewById(R.id.man_my_order_no_finish_bar);
        oks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageManOrderNoFinishActivity.this,ManageManActivity.class);
                intent.putExtra("sta","1");
                startActivity(intent);
            }
        });



    }
}