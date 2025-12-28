package com.ccf.feige.orderfood.activity.man.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.bean.CommentBean;
import com.ccf.feige.orderfood.bean.UserCommonBean;
import com.ccf.feige.orderfood.dao.AdminDao;

import java.util.List;

/**
 * 这个是用来显示商家商品的一个adapter
 */
public class CommentLIstAdapter extends ArrayAdapter<CommentBean> {

    private List<CommentBean> list;

    private Context context;


    public CommentLIstAdapter(@NonNull Context context, List<CommentBean> list) {
        super(context, R.layout.list_man_comment_list,list);
        this.context=context;
        this.list=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.list_man_comment_list,viewGroup,false);
        }

        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");
        CommentBean tem = list.get(position);

        String userId=tem.getCommentUserId();
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);
        ImageView img=convertView.findViewById(R.id.man_comment_tx);
        img.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));

        TextView name=convertView.findViewById(R.id.man_comment_name);
        name.setText(commonUser.getsName());

        TextView c=convertView.findViewById(R.id.man_comment_con);


        TextView time=convertView.findViewById(R.id.man_comment_time);
        time.setText(tem.getCommentTime());

        convertView.setOnClickListener(null);

        int icoIdZ[]={R.id.man_comment_one,
                R.id.man_comment_two,
                R.id.man_comment_three,
                R.id.man_comment_four,
                R.id.man_comment_five};
        int scoreZ=Integer.valueOf(tem.getCommentScore());
        String conA[]={"非常差","差","一般","满意","非常满意"};//代笔5个内容
        c.setText(conA[scoreZ-1]);
        for(int i=scoreZ;i<5;i++){
            ImageView temp = convertView.findViewById(icoIdZ[i]);
            temp.setImageResource(R.drawable.wxx);
        }

        for(int i=0;i<scoreZ;i++){
            ImageView temp = convertView.findViewById(icoIdZ[i]);
            temp.setImageResource(R.drawable.xx);
        }
        //上面内容是显示评分的内容


        TextView userCon=convertView.findViewById(R.id.man_comment_userCon);
        userCon.setText(tem.getCommentContent());


        ImageView imgZ=convertView.findViewById(R.id.man_comment_userImg  );
        if(tem.getCommentImg().equals("")){
            imgZ.setVisibility(View.GONE);
        }else{
            imgZ.setImageBitmap(BitmapFactory.decodeFile(tem.getCommentImg()));
        }



        return convertView;
    }



}
