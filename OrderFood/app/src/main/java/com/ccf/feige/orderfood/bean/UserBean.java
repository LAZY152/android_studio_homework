package com.ccf.feige.orderfood.bean;

import java.io.Serializable;

/**
 * 用户实体类（JavaBean）
 * 作用：映射数据库中用户相关数据表（对应d_business表）的记录，封装用户的所有属性和行为
 * 实现Serializable接口：支持对象的序列化与反序列化，便于在网络传输、持久化存储等场景中使用
 */
public class UserBean implements Serializable {
    // 逻辑删除状态常量（与Dao层保持一致，便于维护，避免魔法值）
    public static final int NOT_DELETED = 0; // 未注销/正常：表示用户账号处于有效可用状态
    public static final int IS_DELETED = 1; // 已注销/逻辑删除：表示用户账号已注销，仅保留数据不物理删除

    // 原有字段：用户核心信息字段，与数据库表原有字段一一对应
    private String sId; // 用户唯一标识ID（主键）
    private String sPwd; // 用户登录密码
    private String sName; // 用户姓名/账号昵称
    private String sDescribe; // 用户个人简介/描述信息
    private String sType; // 用户类型（如普通用户、管理员等，区分不同权限角色）
    private String sImg; // 用户头像图片的存储路径/访问地址

    // 新增：逻辑删除字段（对应数据库表 d_business 的 s_is_delete 字段）
    // 作用：实现逻辑删除，替代物理删除（删除时仅修改该字段状态，不删除整条数据），便于数据恢复和追溯
    private int sIsDelete;

    // 原有无参构造
    // 作用：提供默认的对象实例化方式，兼容反射实例化、框架自动注入等场景
    // 注：默认初始化逻辑删除状态为未注销，与数据库表该字段的默认值保持一致，避免数据不一致问题
    public UserBean() {
        // 默认未注销，与数据库默认值一致
        this.sIsDelete = NOT_DELETED;
    }

    // 原有有参构造（兼容旧代码，默认未注销）
    // 作用：兼容项目中已有的实例化逻辑，无需修改旧代码即可实现逻辑删除字段的默认初始化
    // 参数：用户核心信息的6个字段，不包含逻辑删除字段，保持与旧代码的参数列表一致
    public UserBean(String sId, String sPwd, String sName, String sDescribe, String sType, String sImg) {
        this.sId = sId;
        this.sPwd = sPwd;
        this.sName = sName;
        this.sDescribe = sDescribe;
        this.sType = sType;
        this.sImg = sImg;
        // 默认未注销，与数据库默认值一致：即使调用该构造方法，也确保逻辑删除字段有合法默认值
        this.sIsDelete = NOT_DELETED;
    }

    // 新增：带逻辑删除字段的有参构造（适配Dao层查询/新增，完整接收数据库字段）
    // 作用：适配Dao层（数据访问层）的查询、新增操作，能够完整接收并封装数据库表中的所有字段（包含新增的逻辑删除字段）
    // 参数：包含用户核心信息6个字段+逻辑删除字段，覆盖数据库表全部相关字段，保证数据的完整性
    public UserBean(String sId, String sPwd, String sName, String sDescribe, String sType, String sImg, int sIsDelete) {
        this.sId = sId;
        this.sPwd = sPwd;
        this.sName = sName;
        this.sDescribe = sDescribe;
        this.sType = sType;
        this.sImg = sImg;
        this.sIsDelete = sIsDelete;
    }

    // 原有字段的getter/setter（保持不变，兼容旧代码）
    // getter方法：获取对应私有字段的值，提供字段的外部访问入口（封装性要求：私有字段不可直接访问）
    // setter方法：设置对应私有字段的值，提供字段的外部修改入口，同时可后续扩展字段校验逻辑
    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsPwd() {
        return sPwd;
    }

    public void setsPwd(String sPwd) {
        this.sPwd = sPwd;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsDescribe() {
        return sDescribe;
    }

    public void setsDescribe(String sDescribe) {
        this.sDescribe = sDescribe;
    }

    public String getsType() {
        return sType;
    }

    public void setsType(String sType) {
        this.sType = sType;
    }

    public String getsImg() {
        return sImg;
    }

    public void setsImg(String sImg) {
        this.sImg = sImg;
    }

    // 新增：逻辑删除字段的getter/setter
    // getter方法：获取用户的逻辑删除状态（未注销/已注销）
    public int getsIsDelete() {
        return sIsDelete;
    }

    // setter方法：设置用户的逻辑删除状态，附带合法性校验
    // 校验逻辑：确保只有0（NOT_DELETED）和1（IS_DELETED）两种合法值，非法值默认重置为未注销状态
    // 作用：避免设置无效的逻辑删除状态，保证数据的合法性和一致性
    public void setsIsDelete(int sIsDelete) {
        // 校验字段值，确保只有0/1两种状态，避免非法值
        this.sIsDelete = (sIsDelete == NOT_DELETED || sIsDelete == IS_DELETED) ? sIsDelete : NOT_DELETED;
    }

    // 重写toString()：新增逻辑删除字段，便于调试和日志打印
    // 作用：返回对象的字符串格式化表示，在调试、日志输出时可以直观查看对象的所有字段值
    // 优化：逻辑删除状态不直接输出数字，而是转换为中文描述（未注销/已注销），提升可读性
    @Override
    public String toString() {
        return "UserBean{" +
                "sId='" + sId + '\'' +
                ", sPwd='" + sPwd + '\'' +
                ", sName='" + sName + '\'' +
                ", sDescribe='" + sDescribe + '\'' +
                ", sType='" + sType + '\'' +
                ", sImg='" + sImg + '\'' +
                ", sIsDelete=" + (sIsDelete == NOT_DELETED ? "未注销" : "已注销") +
                '}';
    }
}