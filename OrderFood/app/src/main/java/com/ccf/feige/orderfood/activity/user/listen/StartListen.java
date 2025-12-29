package com.ccf.feige.orderfood.activity.user.listen;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.ManageUserCommentActivity;

/**
 * 评分点击事件监听器类
 * 用于处理用户评价页面的星级/满意度点击选择逻辑，实现View.OnClickListener接口处理点击事件
 */
public class StartListen implements View.OnClickListener {

    // 持有对应的活动实例，用于获取页面中的控件
    private ManageUserCommentActivity manageUserCommentActivity;


    /**
     * 构造方法
     * @param manageUserCommentActivity 用户评价管理活动实例，用于后续查找页面控件
     */
    public StartListen(ManageUserCommentActivity manageUserCommentActivity) {

        this.manageUserCommentActivity=manageUserCommentActivity;

    }

    /**
     * 点击事件处理核心方法
     * 当用户点击评分对应的ImageView时，触发此方法进行逻辑处理
     * @param v 被点击的View实例（此处为评分对应的ImageView）
     */
    @Override
    public void onClick(View v) {
        ImageView img= (ImageView) v;


        String conA[]={"非常差","差","一般","满意","非常满意"};//5个内容
        // 获取被点击View的唯一标识ID，用于判断点击的是哪个评分项
        int id=v.getId();
        // 定义5个评分ImageView的控件ID数组，与满意度文本数组一一对应
        int icoIdZ[]={R.id.user_comment_one,
                R.id.user_comment_two,
                R.id.user_comment_three,
                R.id.user_comment_four,
                R.id.user_comment_five};
        // 从活动实例中查找显示满意度文本的TextView控件
        TextView it = manageUserCommentActivity.findViewById(R.id.user_comment_con);
        int a=0;
        // 遍历所有评分ImageView的控件ID，处理每个评分项的显示状态
        for(int i=0;i<icoIdZ.length;i++){

            if(id==icoIdZ[i]){
                // 找到被点击项，将标记变量置为1
                a=1;
                // 将对应的满意度文本设置到TextView中显示
                it.setText(conA[i]);
            }
            // 根据当前遍历的控件ID，从活动中查找对应的ImageView评分控件
            ImageView imgZ = manageUserCommentActivity.findViewById(icoIdZ[i]);

            // 根据标记变量判断，设置评分控件的显示图片
            if(a==0){

                imgZ.setImageResource(R.drawable.xx);
            }else{
                imgZ.setImageResource(R.drawable.wxx);
            }
            img.setImageResource(R.drawable.xx);



        }
    }
}
