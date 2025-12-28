package com.ccf.feige.orderfood.activity.user.frament;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.CommentLIstAdapter;
import com.ccf.feige.orderfood.activity.user.adapter.UserFoodLIstAdapter;
import com.ccf.feige.orderfood.bean.CommentBean;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.dao.CommentDao;
import com.ccf.feige.orderfood.dao.FoodDao;

import java.util.List;

public class UserBuyFoodBusinessCommentFragment extends Fragment {




    private String businessId;
    public UserBuyFoodBusinessCommentFragment(String businessId){
        this.businessId=businessId;
    }




    View rootview;//根石头

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_use_buy_food_comment, container, false);

        //接下来要写的是适配器
        ListView listView = rootview.findViewById(R.id.user_buy_food_comment_listView);


        List<CommentBean> list = CommentDao.getCommetByBusinessId(businessId);
        //适配器
        CommentLIstAdapter commentLIstAdapter=new CommentLIstAdapter(getContext(),list);
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(commentLIstAdapter);
        }

        return rootview;
    }


}