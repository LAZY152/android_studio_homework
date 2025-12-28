package com.ccf.feige.orderfood.bean;

import java.io.Serializable;

public class AddressBean implements Serializable {

    private String sId;
    private String sUserId;
    private String sUserName;
    private String sUserAddress;
    private String sUserPhone;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsUserId() {
        return sUserId;
    }

    public void setsUserId(String sUserId) {
        this.sUserId = sUserId;
    }

    public String getsUserName() {
        return sUserName;
    }

    public void setsUserName(String sUserName) {
        this.sUserName = sUserName;
    }

    public String getsUserAddress() {
        return sUserAddress;
    }

    public void setsUserAddress(String sUserAddress) {
        this.sUserAddress = sUserAddress;
    }

    public String getsUserPhone() {
        return sUserPhone;
    }

    public void setsUserPhone(String sUserPhone) {
        this.sUserPhone = sUserPhone;
    }

    public AddressBean(String sId, String sUserId, String sUserName, String sUserAddress, String sUserPhone) {
        this.sId = sId;
        this.sUserId = sUserId;
        this.sUserName = sUserName;
        this.sUserAddress = sUserAddress;
        this.sUserPhone = sUserPhone;
    }

    public AddressBean() {
    }
}
