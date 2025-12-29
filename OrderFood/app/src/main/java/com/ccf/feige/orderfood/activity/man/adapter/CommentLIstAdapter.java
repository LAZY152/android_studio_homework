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
 * 商家端评论列表适配器
 * 继承自ArrayAdapter，用于将评论数据（CommentBean）与评论列表项布局进行绑定，
 * 实现商家端评论列表的展示，包括用户信息、评分、评论内容、评论图片等信息的渲染
 */
public class CommentLIstAdapter extends ArrayAdapter<CommentBean> {

    /** 存储评论数据的集合，保存所有需要展示的评论信息 */
    private List<CommentBean> list;

    /** 上下文对象，用于加载布局、获取资源等操作 */
    private Context context;

    /**
     * 适配器构造方法，用于初始化适配器实例
     * @param context 上下文对象，通常为对应的Activity或Fragment
     * @param list 评论数据集合，包含所有需要展示的CommentBean实体
     */
    public CommentLIstAdapter(@NonNull Context context, List<CommentBean> list) {
        // 调用父类构造方法，指定列表项布局文件和数据集合
        super(context, R.layout.list_man_comment_list,list);
        this.context=context;
        this.list=list;
    }

    /**
     * 获取指定位置的列表项视图，完成数据绑定和视图初始化，支持视图复用优化性能
     * @param position 当前列表项的位置索引
     * @param convertView 可复用的旧视图，用于减少视图创建开销，提升列表滑动性能
     * @param viewGroup 列表项视图的父容器（ListView/GridView等）
     * @return 绑定好数据的当前列表项视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        // 视图复用判断：若没有可复用的旧视图，创建新视图
        if(convertView==null){
            // 获取布局加载器，用于加载列表项布局文件
            LayoutInflater inflater=LayoutInflater.from(getContext());
            // 加载列表项布局，生成视图实例，不自动附加到父容器
            convertView=inflater.inflate(R.layout.list_man_comment_list,viewGroup,false);
        }

        //TextView a=convertView.findViewById(R.id.xz);
        //a.setText("AAA");

        // 获取当前位置对应的评论数据实体
        CommentBean tem = list.get(position);

        // 从评论实体中获取评论用户的ID
        String userId=tem.getCommentUserId();
        // 根据用户ID查询对应的普通用户信息
        UserCommonBean commonUser = AdminDao.getCommonUser(userId);

        // 获取用户头像ImageView控件
        ImageView img=convertView.findViewById(R.id.man_comment_tx);
        // 从本地文件路径解码位图，设置用户头像
        img.setImageBitmap(BitmapFactory.decodeFile(commonUser.getsImg()));

        // 获取用户名TextView控件
        TextView name=convertView.findViewById(R.id.man_comment_name);
        // 设置用户名文本
        name.setText(commonUser.getsName());

        // 获取评论等级描述TextView控件（对应评分的文字说明）
        TextView c=convertView.findViewById(R.id.man_comment_con);

        // 获取评论时间TextView控件
        TextView time=convertView.findViewById(R.id.man_comment_time);
        // 设置评论时间文本
        time.setText(tem.getCommentTime());

        // 清空列表项的点击事件（若无需点击交互，避免遗留旧事件）
        convertView.setOnClickListener(null);

        // 定义5个评分星星ImageView的控件ID数组，对应五星评分的五个星星
        int icoIdZ[]={R.id.man_comment_one,
                R.id.man_comment_two,
                R.id.man_comment_three,
                R.id.man_comment_four,
                R.id.man_comment_five};

        // 将评论评分字符串转换为整数类型
        int scoreZ=Integer.valueOf(tem.getCommentScore());
        // 定义评分对应的等级描述文本数组，与评分1-5一一对应
        String conA[]={"非常差","差","一般","满意","非常满意"};//代笔5个内容

        // 设置评分对应的等级描述文本
        c.setText(conA[scoreZ-1]);

        // 循环处理未达到评分的星星，设置为空心星（未选中状态）
        for(int i=scoreZ;i<5;i++){
            ImageView temp = convertView.findViewById(icoIdZ[i]);
            temp.setImageResource(R.drawable.wxx);
        }

        // 循环处理达到评分的星星，设置为实心星（选中状态）
        for(int i=0;i<scoreZ;i++){
            ImageView temp = convertView.findViewById(icoIdZ[i]);
            temp.setImageResource(R.drawable.xx);
        }
        //上面内容是显示评分的内容

        // 获取用户具体评论内容TextView控件
        TextView userCon=convertView.findViewById(R.id.man_comment_userCon);
        // 设置用户评论内容文本
        userCon.setText(tem.getCommentContent());

        // 获取评论附带图片ImageView控件
        ImageView imgZ=convertView.findViewById(R.id.man_comment_userImg  );
        // 判断评论是否附带图片：若图片路径为空，隐藏图片控件
        if(tem.getCommentImg().equals("")){
            imgZ.setVisibility(View.GONE);
        }else{
            // 若有图片，从本地文件路径解码位图并设置显示
            imgZ.setImageBitmap(BitmapFactory.decodeFile(tem.getCommentImg()));
        }

        // 返回绑定好所有数据的当前列表项视图
        return convertView;
    }
}