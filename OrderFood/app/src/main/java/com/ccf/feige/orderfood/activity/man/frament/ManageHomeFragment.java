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

public class ManageHomeFragment extends Fragment {

    View rootview;//根石头

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview=inflater.inflate(R.layout.fragment_manage_home, container, false);


        //接下来要写的是适配器
        ListView listView = rootview.findViewById(R.id.man_home_food_listView);



        List<FoodBean> list = FoodDao.getAllFoodListByBusinessId(Tools.getOnAccount(getContext()));
        FoodLIstAdapter adapter = new FoodLIstAdapter(getContext(), list);
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(adapter);
        }

        //实现数据账号共享
        String account=Tools.getOnAccount(getContext());


        SearchView searchView = rootview.findViewById(R.id.man_home_food_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //根本 标题来进行查找商品
                List<FoodBean> list = FoodDao.getAllFoodList(account,query);
                FoodLIstAdapter adapter = new FoodLIstAdapter(getContext(), list);
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(adapter);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<FoodBean> list = FoodDao.getAllFoodList(account,newText);
                FoodLIstAdapter adapter = new FoodLIstAdapter(getContext(), list);
                if(list==null||list.size()==0){
                    listView.setAdapter(null);
                }else{
                    listView.setAdapter(adapter);
                }
                return false;
            }
        });

        return rootview;
    }


}