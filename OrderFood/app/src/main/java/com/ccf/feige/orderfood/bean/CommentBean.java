package com.ccf.feige.orderfood.bean;

/**
 * 订单评论实体类
 * 用于封装用户对商家/订单的评论相关数据，对应评论相关的业务模型和数据存储结构
 */
public class CommentBean {
    /**
     * 评论唯一标识ID
     * 用于区分不同的评论记录，通常为数据库自增主键或唯一UUID
     */
    private String commentId;

    /**
     * 评论用户ID
     * 关联发表评论的用户唯一标识，用于绑定评论所属用户
     */
    private String commentUserId;

    /**
     * 被评论商家ID
     * 关联被评论的商家唯一标识，用于绑定评论所属商家
     */
    private String commentBusinessId;

    /**
     * 评论内容
     * 用户发表的具体评论文字信息
     */
    private String commentContent;

    /**
     * 评论时间
     * 用户发表评论的时间，通常为格式化的字符串（如yyyy-MM-dd HH:mm:ss）
     */
    private String commentTime;

    /**
     * 评论评分
     * 用户对商家/订单的评分，以字符串形式存储（可转换为数字类型进行计算）
     */
    private String commentScore;

    /**
     * 评论图片
     * 评论附带的图片路径/图片Base64编码，多个图片可使用分隔符拼接存储
     */
    private String commentImg;

    /**
     * 无参构造方法
     * 用于反射实例化、JSON反序列化等场景（如MyBatis、Gson等框架使用）
     */
    public CommentBean() {}

    /**
     * 全参构造方法
     * 用于一次性初始化评论对象的所有属性，快速创建完整的评论实例
     * @param commentId 评论唯一标识ID
     * @param commentUserId 评论用户ID
     * @param commentBusinessId 被评论商家ID
     * @param commentContent 评论内容
     * @param commentTime 评论时间
     * @param commentScore 评论评分
     * @param commentImg 评论图片
     */
    public CommentBean(String commentId, String commentUserId, String commentBusinessId,
                       String commentContent, String commentTime, String commentScore, String commentImg) {
        this.commentId = commentId;
        this.commentUserId = commentUserId;
        this.commentBusinessId = commentBusinessId;
        this.commentContent = commentContent;
        this.commentTime = commentTime;
        this.commentScore = commentScore;
        this.commentImg = commentImg;
    }

    // Getters and Setters

    /**
     * 获取评论唯一标识ID
     * @return 评论ID
     */
    public String getCommentId() {
        return commentId;
    }

    /**
     * 设置评论唯一标识ID
     * @param commentId 评论ID
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    /**
     * 获取评论用户ID
     * @return 评论用户ID
     */
    public String getCommentUserId() {
        return commentUserId;
    }

    /**
     * 设置评论用户ID
     * @param commentUserId 评论用户ID
     */
    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    /**
     * 获取被评论商家ID
     * @return 被评论商家ID
     */
    public String getCommentBusinessId() {
        return commentBusinessId;
    }

    /**
     * 设置被评论商家ID
     * @param commentBusinessId 被评论商家ID
     */
    public void setCommentBusinessId(String commentBusinessId) {
        this.commentBusinessId = commentBusinessId;
    }

    /**
     * 获取评论内容
     * @return 评论内容
     */
    public String getCommentContent() {
        return commentContent;
    }

    /**
     * 设置评论内容
     * @param commentContent 评论内容
     */
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    /**
     * 获取评论时间
     * @return 评论时间
     */
    public String getCommentTime() {
        return commentTime;
    }

    /**
     * 设置评论时间
     * @param commentTime 评论时间
     */
    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    /**
     * 获取评论评分
     * @return 评论评分
     */
    public String getCommentScore() {
        return commentScore;
    }

    /**
     * 设置评论评分
     * @param commentScore 评论评分
     */
    public void setCommentScore(String commentScore) {
        this.commentScore = commentScore;
    }

    /**
     * 获取评论图片
     * @return 评论图片（路径/Base64编码）
     */
    public String getCommentImg() {
        return commentImg;
    }

    /**
     * 设置评论图片
     * @param commentImg 评论图片（路径/Base64编码）
     */
    public void setCommentImg(String commentImg) {
        this.commentImg = commentImg;
    }
}