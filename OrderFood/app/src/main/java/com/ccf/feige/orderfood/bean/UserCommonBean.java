package com.ccf.feige.orderfood.bean;

public class UserCommonBean {

    private String sId;
    private String sPwd;
    private String sName;

    private String sSex;
    private String sAddress;
    private String sPhone;

    private String sImg;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

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

    public UserCommonBean() {
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

    public String getsSex() {
        return sSex;
    }

    public void setsSex(String sSex) {
        this.sSex = sSex;
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public UserCommonBean(String sId, String sPwd, String sName, String sSex, String sAddress, String sPhone) {
        this.sId = sId;
        this.sPwd = sPwd;
        this.sName = sName;
        this.sSex = sSex;
        this.sAddress = sAddress;
        this.sPhone = sPhone;
    }



    public String getsImg() {
        return sImg;
    }

    public void setsImg(String sImg) {
        this.sImg = sImg;
    }

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
