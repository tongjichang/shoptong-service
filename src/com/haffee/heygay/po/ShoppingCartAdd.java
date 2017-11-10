package com.haffee.heygay.po;

import java.sql.Timestamp;
import java.util.List;

public class ShoppingCartAdd {
	


	private int cart_id;//购物车ID
	private int shop_id;//店铺ID
	private int table_id;//桌台ID
	private int total_num;//商品总数
	private float total_price;//商品总价
	private String comments;//备注
	private Timestamp create_time;//创建时间
	private List<Object> goods_set;//商品集合
	private int status;//状态 0：未提交，1：已提交,2:已结账,3:取消
	private int waiter_id;//服务员ID

	public int getCart_id() {
		return cart_id;
	}

	public void setCart_id(int cart_id) {
		this.cart_id = cart_id;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public int getTable_id() {
		return table_id;
	}

	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}

	

	public int getTotal_num() {
		return total_num;
	}

	public void setTotal_num(int total_num) {
		this.total_num = total_num;
	}

	public float getTotal_price() {
		return total_price;
	}

	public void setTotal_price(float total_price) {
		this.total_price = total_price;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	
	public List<Object> getGoods_set() {
		return goods_set;
	}

	public void setGoods_set(List<Object> goods_set) {
		this.goods_set = goods_set;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWaiter_id() {
		return waiter_id;
	}

	public void setWaiter_id(int waiter_id) {
		this.waiter_id = waiter_id;
	}


}
