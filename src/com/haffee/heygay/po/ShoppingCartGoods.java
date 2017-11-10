package com.haffee.heygay.po;

public class ShoppingCartGoods {

	private int cart_good_id;//ID
	private int good_id;//菜品ID
	private String good_name;//菜品名称
	private float good_price;//菜品总价
	private int good_num;//菜品数量
	private float good_total_price;//菜品总价
	private int from; //1:顾客，2：服务员
	private int cart_id;//购物车ID
	private float pre_price;//单价
	private float discount;//折扣
	private String img_url;//菜品图片URL
	private int if_up;//是否上菜0：未上，1：已上
	private String printer_no_str;
	private int if_print;//是否打印 0：否，1：是

	public int getCart_good_id() {
		return cart_good_id;
	}

	public void setCart_good_id(int cart_good_id) {
		this.cart_good_id = cart_good_id;
	}

	public int getGood_id() {
		return good_id;
	}

	public void setGood_id(int good_id) {
		this.good_id = good_id;
	}

	public String getGood_name() {
		return good_name;
	}

	public void setGood_name(String good_name) {
		this.good_name = good_name;
	}

	public float getGood_price() {
		return good_price;
	}

	public void setGood_price(float good_price) {
		this.good_price = good_price;
	}

	public int getGood_num() {
		return good_num;
	}

	public void setGood_num(int good_num) {
		this.good_num = good_num;
	}

	public float getGood_total_price() {
		return good_total_price;
	}

	public void setGood_total_price(float good_total_price) {
		this.good_total_price = good_total_price;
	}
/**
	public ShoppingCart getCart() {
		return cart;
	}

	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}
**/
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getCart_id() {
		return cart_id;
	}

	public void setCart_id(int cart_id) {
		this.cart_id = cart_id;
	}

	public float getPre_price() {
		return pre_price;
	}

	public void setPre_price(float pre_price) {
		this.pre_price = pre_price;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public int getIf_up() {
		return if_up;
	}

	public void setIf_up(int if_up) {
		this.if_up = if_up;
	}

	public String getPrinter_no_str() {
		return printer_no_str;
	}

	public void setPrinter_no_str(String printer_no_str) {
		this.printer_no_str = printer_no_str;
	}

	public int getIf_print() {
		return if_print;
	}

	public void setIf_print(int if_print) {
		this.if_print = if_print;
	}

}
