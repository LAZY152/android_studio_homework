package com.ccf.feige.orderfood.activity.man;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.adapter.CommentLIstAdapter;
import com.ccf.feige.orderfood.bean.CommentBean;
import com.ccf.feige.orderfood.dao.CommentDao;
import com.ccf.feige.orderfood.until.Tools;

import java.util.List;

/**
 * 这个评论内容
 * 商家端 - 评论管理页面
 * 功能：展示当前商家对应的用户评论列表，提供返回商家主页面的入口
 */
public class ManageManCommentActivity extends AppCompatActivity {

    /**
     * 页面创建生命周期方法
     * 负责初始化页面布局、控件绑定、数据加载和视图渲染
     * @param savedInstanceState 保存的页面状态数据（如屏幕旋转后的恢复数据）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定当前页面的布局文件
        setContentView(R.layout.activity_manage_man_comment);

        // 初始化顶部工具栏并设置点击事件
        Toolbar toolbar = findViewById(R.id.man_my_comment_bar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            /**
             * 工具栏点击事件回调
             * 功能：点击工具栏返回商家主页面，并传递状态参数
             * @param v 被点击的视图（此处为Toolbar）
             */
            @Override
            public void onClick(View v) {
                // 创建跳转到商家主页面的意图
                Intent intent=new Intent(ManageManCommentActivity.this,ManageManActivity.class);
                // 携带状态参数"sta"，值为"1"（用于目标页面判断跳转来源或展示对应状态）
                intent.putExtra("sta","1");
                // 启动目标页面
                startActivity(intent);
            }
        });

        // 绑定评论列表的ListView控件
        ListView listView = findViewById(R.id.man_my_comment_listview);
        // 通过工具类获取当前登录的商家账号
        String account= Tools.getOnAccount(this);
        // 调用数据访问层方法，根据商家账号查询对应的评论列表
        List<CommentBean> list = CommentDao.getCommetByBusinessId(account);

        // 初始化评论列表的适配器（用于绑定数据和视图）
        CommentLIstAdapter commentLIstAdapter=new CommentLIstAdapter(this,list);
        // 判断评论列表是否为空
        if(list==null||list.size()==0){
            // 列表为空时，设置适配器为null，不展示任何内容
            listView.setAdapter(null);
        }else{
            // 列表不为空时，设置适配器，展示评论数据
            listView.setAdapter(commentLIstAdapter);
        }
    }
}