package com.ccf.feige.orderfood.bean;

/**
 * 用户公共信息实体类
 * 用于封装订单美食项目中用户的基础核心信息，承载用户的身份、认证及个人资料数据
 */
public class UserCommonBean {

    /**
     * 用户唯一标识ID
     */
    private String sId;
    /**
     * 用户登录密码
     */
    private String sPwd;
    /**
     * 用户姓名
     */
    private String sName;

    /**
     * 用户性别
     */
    private String sSex;
    /**
     * 用户联系地址
     */
    private String sAddress;
    /**
     * 用户手机号码
     */
    private String sPhone;

    /**
     * 用户头像图片路径/标识
     */
    private String sImg;

    /**
     * 获取用户唯一标识ID
     * @return 用户唯一标识ID字符串
     */
    public String getsId() {
        return sId;
    }

    /**
     * 设置用户唯一标识ID
     * @param sId 用户唯一标识ID字符串
     */
    public void setsId(String sId) {
        this.sId = sId;
    }

    /**
     * 重写toString方法，用于打印/日志输出用户对象的核心属性信息
     * 便于调试和查看用户对象的关键数据
     * @return 拼接了用户核心属性的字符串
     */
    @Override
    public String toString() {
        return "UserCommonBean{" +
                "sId='" + sId + '\'' +
                ", sPwd='" + sPwd + '\'' +
                ", sName='" + sName + '\'' +
                ", sSex='" + sSex + '\'' +
                ", sAddress='" + sAddress + '\'' +
                ", sPhone='" + sPhone + '\'' +
                '}';
    }

    /**
     * 无参构造方法
     * 用于反射实例化对象（如JSON反序列化、ORM框架映射等场景）
     */
    public UserCommonBean() {
    }

    /**
     * 获取用户登录密码
     * @return 用户登录密码字符串
     */
    public String getsPwd() {
        return sPwd;
    }

    /**
     * 设置用户登录密码
     * @param sPwd 用户登录密码字符串
     */
    public void setsPwd(String sPwd) {
        this.sPwd = sPwd;
    }

    /**
     * 获取用户姓名
     * @return 用户姓名字符串
     */
    public String getsName() {
        return sName;
    }

    /**
     * 设置用户姓名
     * @param sName 用户姓名字符串
     */
    public void setsName(String sName) {
        this.sName = sName;
    }

    /**
     * 获取用户性别
     * @return 用户性别字符串
     */
    public String getsSex() {
        return sSex;
    }

    /**
     * 设置用户性别
     * @param sSex 用户性别字符串
     */
    public void setsSex(String sSex) {
        this.sSex = sSex;
    }

    /**
     * 获取用户联系地址
     * @return 用户联系地址字符串
     */
    public String getsAddress() {
        return sAddress;
    }

    /**
     * 设置用户联系地址
     * @param sAddress 用户联系地址字符串
     */
    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    /**
     * 获取用户手机号码
     * @return 用户手机号码字符串
     */
    public String getsPhone() {
        return sPhone;
    }

    /**
     * 设置用户手机号码
     * @param sPhone 用户手机号码字符串
     */
    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    /**
     * 有参构造方法（不含用户头像）
     * 用于快速实例化包含核心个人信息的用户对象
     * @param sId 用户唯一标识ID
     * @param sPwd 用户登录密码
     * @param sName 用户姓名
     * @param sSex 用户性别
     * @param sAddress 用户联系地址
     * @param sPhone 用户手机号码
     */
    public UserCommonBean(String sId, String sPwd, String sName, String sSex, String sAddress, String sPhone) {
        this.sId = sId;
        this.sPwd = sPwd;
        this.sName = sName;
        this.sSex = sSex;
        this.sAddress = sAddress;
        this.sPhone = sPhone;
    }

    /**
     * 获取用户头像图片路径/标识
     * @return 用户头像图片路径/标识字符串
     */
    public String getsImg() {
        return sImg;
    }

    /**
     * 设置用户头像图片路径/标识
     * @param sImg 用户头像图片路径/标识字符串
     */
    public void setsImg(String sImg) {
        this.sImg = sImg;
    }

    /**
     * 有参构造方法（包含用户头像）
     * 用于快速实例化包含完整个人信息（含头像）的用户对象
     * @param sId 用户唯一标识ID
     * @param sPwd 用户登录密码
     * @param sName 用户姓名
     * @param sSex 用户性别
     * @param sAddress 用户联系地址
     * @param sPhone 用户手机号码
     * @param sImg 用户头像图片路径/标识
     */
    public UserCommonBean(String sId, String sPwd, String sName, String sSex, String sAddress, String sPhone, String sImg) {
        this.sId = sId;
        this.sPwd = sPwd;
        this.sName = sName;
        this.sSex = sSex;
        this.sAddress = sAddress;
        this.sPhone = sPhone;
        this.sImg = sImg;
    }
}