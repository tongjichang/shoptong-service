package com.haffee.heygay.po;

import java.sql.Timestamp;

/**
 * 店铺
 * 
 * @author jacktong
 * @date 2017年7月4日
 *
 */
public class Shop {

	private int shop_id;//店铺ID
	private String shop_name;//店铺名称
	private String address;//店铺地址
	private String shop_owner_name;//店铺老板
	private String shop_owner_phone;//老板电话
	private String shop_url;//店铺URL地址
	private String introduction;// 店铺简介
	private String notice;// 商家公告
	private Timestamp create_time;// 创建时间
	private String icon; // 图标
	private String begin_time;// 营业开始时间
	private String end_time; // 营业结束时间
	private int shop_status;// 店铺状态1:正常，2:冻结
	private String user_name;
	private String wechat_img; //微信支付图片
	private String alipay_img; //支付宝支付图片
	private int print_way;//后厨打印方式 1：一菜一打，2：一单一打 ，默认1
	

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShop_owner_name() {
		return shop_owner_name;
	}

	public void setShop_owner_name(String shop_owner_name) {
		this.shop_owner_name = shop_owner_name;
	}

	public String getShop_owner_phone() {
		return shop_owner_phone;
	}

	public void setShop_owner_phone(String shop_owner_phone) {
		this.shop_owner_phone = shop_owner_phone;
	}

	public String getShop_url() {
		return shop_url;
	}

	public void setShop_url(String shop_url) {
		this.shop_url = shop_url;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}


	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public int getShop_status() {
		return shop_status;
	}

	public void setShop_status(int shop_status) {
		this.shop_status = shop_status;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getWechat_img() {
		return wechat_img;
	}

	public void setWechat_img(String wechat_img) {
		this.wechat_img = wechat_img;
	}

	public String getAlipay_img() {
		return alipay_img;
	}

	public void setAlipay_img(String alipay_img) {
		this.alipay_img = alipay_img;
	}

	public int getPrint_way() {
		return print_way;
	}

	public void setPrint_way(int print_way) {
		this.print_way = print_way;
	}

}
