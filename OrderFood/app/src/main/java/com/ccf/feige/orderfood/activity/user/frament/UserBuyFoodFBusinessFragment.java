package com.ccf.feige.orderfood.activity.user.frament;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.adapter.UserBuyFoodLIstAdapter;
import com.ccf.feige.orderfood.activity.user.adapter.UserFoodLIstAdapter;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.dao.FoodDao;

import java.util.List;

public class UserBuyFoodFBusinessFragment extends Fragment {

    View rootview;//根石头

    private String businessId;

    private Context context;
    public  UserBuyFoodFBusinessFragment(String businessId,Context context){
        this.businessId=businessId;
        this.context=context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview=inflater.inflate(R.layout.fragment_user_buy_food, container, false);

        //接下来要写的是适配器
        RecyclerView listView = rootview.findViewById(R.id.user_buy_food_listView);
        //通过外面进行传参
        List<FoodBean> list = FoodDao.getAllFoodListByBusinessId(businessId);

        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        UserBuyFoodLIstAdapter adapter = new UserBuyFoodLIstAdapter(list,context);
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(adapter);
        }

        return rootview;
    }


}