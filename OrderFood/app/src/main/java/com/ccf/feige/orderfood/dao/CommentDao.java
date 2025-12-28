package com.ccf.feige.orderfood.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ccf.feige.orderfood.bean.CommentBean;
import com.ccf.feige.orderfood.bean.FoodBean;
import com.ccf.feige.orderfood.db.DBUntil;
import com.ccf.feige.orderfood.until.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CommentDao {
    public static SQLiteDatabase db= DBUntil.con;

    public static List<CommentBean> getCommetByBusinessId(String id){
        List<CommentBean> list=new ArrayList<>();
        String data[]={id};
        Cursor cursor=db.rawQuery("select * from d_comments where s_comment_business_id=?",data);
        while (cursor.moveToNext()){
            CommentBean commentBean=new CommentBean();
            String idT=Tools.getResultString(cursor,"s_comment_id");
            commentBean.setCommentId(idT);

            String useridT=Tools.getResultString(cursor,"s_comment_user_id");
            commentBean.setCommentUserId(useridT);

            String businessId=Tools.getResultString(cursor,"s_comment_business_id");
            commentBean.setCommentBusinessId(businessId);

            String con=Tools.getResultString(cursor,"s_comment_con");
            commentBean.setCommentContent(con);

            String tim=Tools.getResultString(cursor,"s_comment_time");
            commentBean.setCommentTime( tim);

            String score=Tools.getResultString(cursor,"s_comment_score");
            commentBean.setCommentScore(score);

            String img=Tools.getResultString(cursor,"s_comment_img");
            commentBean.setCommentImg(img);


            list.add(commentBean);
        }

        return list;
    }


    /**
     * 获取商家的平均分
     * @param account
     * @return
     */
    public static String getAvgScoreBusiness(String account){

        String data[]={account};

        String sql="SELECT avg(s_comment_score) as score FROM d_comments where  s_comment_business_id=?";
        Cursor rs = db.rawQuery(sql, data);
        if(rs.moveToNext()){
            String jg = Tools.getResultString(rs, "score");
            if(jg==null){
                return "0";
            }else{
                return jg;
            }
        }
        return "0";

    }


    /**
     * 评论内容
     * @param account
     * @param businessId
     * @param con
     * @param score
     * @param img
     * @return
     */
    public static int insertComment(String account,String businessId,String con,String score,String img){
        String id= UUID.randomUUID().toString().replace("-","");

        Date date1=new Date();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time=sdf1.format(date1);
        try{
            db.execSQL("INSERT INTO d_comments (s_comment_id,s_comment_user_id, s_comment_business_id" +
                            ",s_comment_con, s_comment_time,s_comment_score,s_comment_img) " +
                            "VALUES (?, ?, ?,?, ?, ?, ?)",
                    new Object[]{id, account, businessId, con, time,score,img});
            return 1;
        }catch (Exception e){
            return 0;
        }

    }

}
