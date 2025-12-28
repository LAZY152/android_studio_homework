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

public class UserNoFinishOrderFragment extends Fragment {

    View rootview;//根石头

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview=inflater.inflate(R.layout.fragment_manage_user_order_no_finish, container, false);

        ListView listView= rootview.findViewById(R.id.user_my_order_no_finish_listView);
        String account= Tools.getOnAccount(rootview.getContext());
        String sta="1";
        List<OrderBean> list = OrderDao.getAllOrdersByStaAndUser(account,sta);
        //List<OrderBean> list = OrderDao.getAllOrders();
        OrderNoFinishUserAdapter orderNoFinishIstAdapter=new OrderNoFinishUserAdapter(rootview.getContext(),list);

        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(orderNoFinishIstAdapter);

        }

        SearchView searchView=rootview.findViewById(R.id.user_my_order_no_finish_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                List<OrderBean> list = OrderDao.getAllOrdersByStaAndUser(account,sta);

                List<OrderBean> list1 = Tools.filterOrder(list, query);

                OrderNoFinishUserAdapter orderNoFinishIstAdapter=new OrderNoFinishUserAdapter(rootview.getContext(),list1);
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<OrderBean> list = OrderDao.getAllOrdersByStaAndUser(account,sta);
                List<OrderBean> list1 = Tools.filterOrder(list, newText);
                OrderNoFinishUserAdapter orderNoFinishIstAdapter=new OrderNoFinishUserAdapter(rootview.getContext(),list1);

                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(orderNoFinishIstAdapter);

                }
                return true;
            }
        });


        return rootview;
    }


}