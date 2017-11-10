package com.haffee.heygay.po;

import java.sql.Timestamp;

public class Suggest {

	private int s_id;
	private String content;
	private int shop_id;
	private String shop_name;
	private int waiter_id;
	private String waiter_name;
	private Timestamp create_time;// 创建时间

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

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

	public int getWaiter_id() {
		return waiter_id;
	}

	public void setWaiter_id(int waiter_id) {
		this.waiter_id = waiter_id;
	}

	public String getWaiter_name() {
		return waiter_name;
	}

	public void setWaiter_name(String waiter_name) {
		this.waiter_name = waiter_name;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

}
