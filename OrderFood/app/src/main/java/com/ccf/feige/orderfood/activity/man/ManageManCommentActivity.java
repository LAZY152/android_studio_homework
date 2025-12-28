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
 */
public class ManageManCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_man_comment);
        //
        Toolbar toolbar = findViewById(R.id.man_my_comment_bar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageManCommentActivity.this,ManageManActivity.class);
                intent.putExtra("sta","1");
                startActivity(intent);
            }
        });


       ListView listView = findViewById(R.id.man_my_comment_listview);
        String account= Tools.getOnAccount(this);
        List<CommentBean> list = CommentDao.getCommetByBusinessId(account);

        //适配器
        CommentLIstAdapter commentLIstAdapter=new CommentLIstAdapter(this,list);
        if(list==null||list.size()==0){
            listView.setAdapter(null);
        }else{
            listView.setAdapter(commentLIstAdapter);
        }



    }
}