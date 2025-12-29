package com.ccf.feige.orderfood.bean;

import java.io.Serializable;

/**
 * 地址信息实体类
 * 用于封装用户的收货地址相关数据，实现Serializable接口支持序列化（可用于网络传输、本地持久化等场景）
 * 对应订单订餐系统中的用户地址数据模型
 */
public class AddressBean implements Serializable {

    /**
     * 地址唯一标识ID
     * 用于区分不同的地址记录，通常为数据库主键或唯一业务标识
     */
    private String sId;

    /**
     * 关联的用户唯一标识ID
     * 用于绑定地址所属的用户，建立用户与地址的关联关系
     */
    private String sUserId;

    /**
     * 收件人姓名
     * 地址对应的收货联系人姓名
     */
    private String sUserName;

    /**
     * 详细收货地址
     * 包含省、市、区、街道、门牌号等完整收货地址信息
     */
    private String sUserAddress;

    /**
     * 收件人联系电话
     * 地址对应的收货联系人手机号码或固定电话
     */
    private String sUserPhone;

    /**
     * 获取地址唯一标识ID
     * @return 地址唯一标识字符串（sId）
     */
    public String getsId() {
        return sId;
    }

    /**
     * 设置地址唯一标识ID
     * @param sId 地址唯一标识字符串
     */
    public void setsId(String sId) {
        this.sId = sId;
    }

    /**
     * 获取关联的用户唯一标识ID
     * @return 用户唯一标识字符串（sUserId）
     */
    public String getsUserId() {
        return sUserId;
    }

    /**
     * 设置关联的用户唯一标识ID
     * @param sUserId 用户唯一标识字符串
     */
    public void setsUserId(String sUserId) {
        this.sUserId = sUserId;
    }

    /**
     * 获取收件人姓名
     * @return 收件人姓名字符串（sUserName）
     */
    public String getsUserName() {
        return sUserName;
    }

    /**
     * 设置收件人姓名
     * @param sUserName 收件人姓名字符串
     */
    public void setsUserName(String sUserName) {
        this.sUserName = sUserName;
    }

    /**
     * 获取详细收货地址
     * @return 详细收货地址字符串（sUserAddress）
     */
    public String getsUserAddress() {
        return sUserAddress;
    }

    /**
     * 设置详细收货地址
     * @param sUserAddress 详细收货地址字符串
     */
    public void setsUserAddress(String sUserAddress) {
        this.sUserAddress = sUserAddress;
    }

    /**
     * 获取收件人联系电话
     * @return 收件人联系电话字符串（sUserPhone）
     */
    public String getsUserPhone() {
        return sUserPhone;
    }

    /**
     * 设置收件人联系电话
     * @param sUserPhone 收件人联系电话字符串
     */
    public void setsUserPhone(String sUserPhone) {
        this.sUserPhone = sUserPhone;
    }

    /**
     * 全参构造方法
     * 用于创建AddressBean实例时，直接初始化所有地址相关属性
     * @param sId 地址唯一标识ID
     * @param sUserId 关联的用户唯一标识ID
     * @param sUserName 收件人姓名
     * @param sUserAddress 详细收货地址
     * @param sUserPhone 收件人联系电话
     */
    public AddressBean(String sId, String sUserId, String sUserName, String sUserAddress, String sUserPhone) {
        this.sId = sId;
        this.sUserId = sUserId;
        this.sUserName = sUserName;
        this.sUserAddress = sUserAddress;
        this.sUserPhone = sUserPhone;
    }

    /**
     * 无参构造方法
     * 用于创建空的AddressBean实例，后续可通过setter方法逐个初始化属性
     * 兼容框架反射实例化、序列化反序列化等场景
     */
    public AddressBean() {
    }
}