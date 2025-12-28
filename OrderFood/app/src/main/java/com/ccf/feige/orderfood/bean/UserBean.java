package com.ccf.feige.orderfood.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    // 逻辑删除状态常量（与Dao层保持一致，便于维护，避免魔法值）
    public static final int NOT_DELETED = 0; // 未注销/正常
    public static final int IS_DELETED = 1; // 已注销/逻辑删除

    // 原有字段
    private String sId;
    private String sPwd;
    private String sName;
    private String sDescribe;
    private String sType;
    private String sImg;

    // 新增：逻辑删除字段（对应数据库表 d_business 的 s_is_delete 字段）
    private int sIsDelete;

    // 原有无参构造
    public UserBean() {
        // 默认未注销，与数据库默认值一致
        this.sIsDelete = NOT_DELETED;
    }

    // 原有有参构造（兼容旧代码，默认未注销）
    public UserBean(String sId, String sPwd, String sName, String sDescribe, String sType, String sImg) {
        this.sId = sId;
        this.sPwd = sPwd;
        this.sName = sName;
        this.sDescribe = sDescribe;
        this.sType = sType;
        this.sImg = sImg;
        // 默认未注销，与数据库默认值一致
        this.sIsDelete = NOT_DELETED;
    }

    // 新增：带逻辑删除字段的有参构造（适配Dao层查询/新增，完整接收数据库字段）
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
    public int getsIsDelete() {
        return sIsDelete;
    }

    public void setsIsDelete(int sIsDelete) {
        // 校验字段值，确保只有0/1两种状态，避免非法值
        this.sIsDelete = (sIsDelete == NOT_DELETED || sIsDelete == IS_DELETED) ? sIsDelete : NOT_DELETED;
    }

    // 重写toString()：新增逻辑删除字段，便于调试和日志打印
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