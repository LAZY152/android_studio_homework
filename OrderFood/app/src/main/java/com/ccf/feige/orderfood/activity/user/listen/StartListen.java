package com.ccf.feige.orderfood.activity.user.listen;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.ManageUserCommentActivity;

public class StartListen implements View.OnClickListener {

    private ManageUserCommentActivity manageUserCommentActivity;
    public StartListen(ManageUserCommentActivity manageUserCommentActivity) {

        this.manageUserCommentActivity=manageUserCommentActivity;

    }

    @Override
    public void onClick(View v) {
        ImageView img= (ImageView) v;


        String conA[]={"非常差","差","一般","满意","非常满意"};//代笔5个内容
        int id=v.getId();
        int icoIdZ[]={R.id.user_comment_one,
                R.id.user_comment_two,
                R.id.user_comment_three,
                R.id.user_comment_four,
                R.id.user_comment_five};

        TextView it = manageUserCommentActivity.findViewById(R.id.user_comment_con);
        int a=0;
        for(int i=0;i<icoIdZ.length;i++){

            if(id==icoIdZ[i]){
                a=1;
                it.setText(conA[i]);
            }
            ImageView imgZ = manageUserCommentActivity.findViewById(icoIdZ[i]);


            if(a==0){

                imgZ.setImageResource(R.drawable.xx);
            }else{
                imgZ.setImageResource(R.drawable.wxx);
            }
            img.setImageResource(R.drawable.xx);



        }



//

    }


}
