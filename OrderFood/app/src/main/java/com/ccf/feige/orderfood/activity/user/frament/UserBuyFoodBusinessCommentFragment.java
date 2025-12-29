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

/**
 * 用户端-购买食物-商家评论展示碎片（Fragment）
 * 功能：用于展示指定商家的用户评论列表，承载评论数据的展示与适配
 */
public class UserBuyFoodBusinessCommentFragment extends Fragment {

    // 商家ID成员变量，用于存储当前要查询评论的商家唯一标识
    private String businessId;

    /**
     * 带参构造方法
     * 作用：初始化Fragment时传入目标商家ID，用于后续查询该商家的评论数据
     * @param businessId 商家唯一标识ID
     */
    public UserBuyFoodBusinessCommentFragment(String businessId){
        this.businessId=businessId;
    }

    // 碎片的根视图对象，用于缓存加载后的布局视图，避免重复查找控件
    View rootview;

    /**
     * 碎片生命周期方法-创建视图
     * 作用：加载碎片对应的布局文件，初始化控件，获取并绑定评论数据
     * @param inflater 布局填充器，用于将xml布局转换为View对象
     * @param container 碎片的父容器视图组
     * @param savedInstanceState 保存的实例状态，用于恢复碎片数据（此处未使用）
     * @return 返回碎片的根视图对象
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // 加载当前碎片对应的布局文件，生成根视图并赋值
        rootview = inflater.inflate(R.layout.fragment_use_buy_food_comment, container, false);

        // 接下来要写的是适配器
        // 从根视图中查找ListView控件（用于展示评论列表），并获取其实例
        ListView listView = rootview.findViewById(R.id.user_buy_food_comment_listView);

        // 调用CommentDao的数据访问方法，根据商家ID查询该商家对应的所有评论数据，返回评论列表
        List<CommentBean> list = CommentDao.getCommetByBusinessId(businessId);

        // 适配器
        // 初始化评论列表适配器，传入当前上下文（Fragment依附的Activity）和查询到的评论列表数据
        CommentLIstAdapter commentLIstAdapter=new CommentLIstAdapter(getContext(),list);

        // 判空处理：如果评论列表为null或列表中没有数据
        if(list==null||list.size()==0){
            // 给ListView设置空适配器，清空列表展示，避免出现空指针或无效视图
            listView.setAdapter(null);
        }else{
            // 评论列表有有效数据时，给ListView设置初始化好的评论适配器，展示评论数据
            listView.setAdapter(commentLIstAdapter);
        }

        // 返回碎片的根视图，完成视图创建与数据绑定
        return rootview;
    }

}