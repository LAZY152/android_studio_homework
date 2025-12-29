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

/**
 * 评论数据访问对象（DAO）
 * 负责处理与评论相关的数据库操作，包括查询评论、获取商家平均评分、插入新评论
 */
public class CommentDao {
    /**
     * 全局SQLite数据库连接对象，从DBUntil工具类中获取已初始化的数据库连接
     */
    public static SQLiteDatabase db= DBUntil.con;

    /**
     * 根据商家ID查询该商家下的所有评论列表
     * @param id 商家唯一标识ID（对应数据库表中的s_comment_business_id字段）
     * @return 包含该商家所有评论的CommentBean列表，若无评论则返回空列表
     */
    public static List<CommentBean> getCommetByBusinessId(String id){
        // 初始化评论列表，用于存储查询结果
        List<CommentBean> list=new ArrayList<>();
        // 封装SQL查询的参数，避免SQL注入风险
        String data[]={id};
        // 执行原生SQL查询，查询d_comments表中指定商家ID的所有评论记录
        Cursor cursor=db.rawQuery("select * from d_comments where s_comment_business_id=?",data);
        // 遍历查询结果游标，逐个解析每条评论记录
        while (cursor.moveToNext()){
            // 初始化单个评论对象
            CommentBean commentBean=new CommentBean();
            // 从游标中获取评论ID，并设置到CommentBean对象中
            String idT=Tools.getResultString(cursor,"s_comment_id");
            commentBean.setCommentId(idT);

            // 从游标中获取评论用户ID，并设置到CommentBean对象中
            String useridT=Tools.getResultString(cursor,"s_comment_user_id");
            commentBean.setCommentUserId(useridT);

            // 从游标中获取商家ID，并设置到CommentBean对象中
            String businessId=Tools.getResultString(cursor,"s_comment_business_id");
            commentBean.setCommentBusinessId(businessId);

            // 从游标中获取评论内容，并设置到CommentBean对象中
            String con=Tools.getResultString(cursor,"s_comment_con");
            commentBean.setCommentContent(con);

            // 从游标中获取评论时间，并设置到CommentBean对象中
            String tim=Tools.getResultString(cursor,"s_comment_time");
            commentBean.setCommentTime( tim);

            // 从游标中获取评论评分，并设置到CommentBean对象中
            String score=Tools.getResultString(cursor,"s_comment_score");
            commentBean.setCommentScore(score);

            // 从游标中获取评论图片路径，并设置到CommentBean对象中
            String img=Tools.getResultString(cursor,"s_comment_img");
            commentBean.setCommentImg(img);

            // 将解析完成的单个评论对象添加到评论列表中
            list.add(commentBean);
        }

        // 返回查询到的评论列表
        return list;
    }


    /**
     * 获取指定商家的平均评分
     * @param account 商家唯一标识（对应数据库表中的s_comment_business_id字段，此处参数名account与商家ID对应）
     * @return 商家的平均评分字符串，若无评论则返回"0"
     */
    public static String getAvgScoreBusiness(String account){
        // 封装SQL查询的参数，指定要查询的商家
        String data[]={account};

        // 构建SQL查询语句，使用avg()函数计算s_comment_score字段的平均值，并给结果起别名score
        String sql="SELECT avg(s_comment_score) as score FROM d_comments where  s_comment_business_id=?";
        // 执行原生SQL查询，获取平均评分结果
        Cursor rs = db.rawQuery(sql, data);
        // 判断游标是否有查询结果（即是否存在该商家的评论）
        if(rs.moveToNext()){
            // 从游标中获取平均评分结果
            String jg = Tools.getResultString(rs, "score");
            // 若平均评分结果为null（无评论时avg()函数返回null），返回"0"
            if(jg==null){
                return "0";
            }else{
                // 若有评分结果，返回该平均评分字符串
                return jg;
            }
        }
        // 若游标无结果（无评论），返回"0"
        return "0";

    }


    /**
     * 插入一条新的用户评论到数据库中
     * @param account 评论用户的账号/唯一标识（对应s_comment_user_id字段）
     * @param businessId 被评论商家的唯一标识（对应s_comment_business_id字段）
     * @param con 评论的具体内容（对应s_comment_con字段）
     * @param score 评论的评分（对应s_comment_score字段）
     * @param img 评论附带图片的路径（对应s_comment_img字段，若无图片则传入空字符串）
     * @return 插入操作结果：1表示插入成功，0表示插入失败（捕获到异常）
     */
    public static int insertComment(String account,String businessId,String con,String score,String img){
        // 生成唯一的评论ID（去除UUID中的横杠），作为评论的主键
        String id= UUID.randomUUID().toString().replace("-","");

        // 获取当前系统时间，并格式化为"yyyy-MM-dd HH:mm"格式的字符串，作为评论时间
        Date date1=new Date();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time=sdf1.format(date1);

        try{
            // 执行原生SQL插入语句，将评论数据插入到d_comments表中
            db.execSQL("INSERT INTO d_comments (s_comment_id,s_comment_user_id, s_comment_business_id" +
                            ",s_comment_con, s_comment_time,s_comment_score,s_comment_img) " +
                            "VALUES (?, ?, ?,?, ?, ?, ?)",
                    new Object[]{id, account, businessId, con, time,score,img});
            // 插入成功，返回1
            return 1;
        }catch (Exception e){
            // 捕获插入过程中的异常（如数据库连接异常、字段不匹配等），返回0
            return 0;
        }

    }

}