package com.haffee.heygay.po;

import java.sql.Timestamp;

/**
 * 服务员
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class Waiter {

	private int waiter_id;//服务员ID
	private String name;//姓名
	private Timestamp create_time;//创建时间
	private int shop_id;//店铺ID
	private String username;//用户名
	private User user;//用户

	public int getWaiter_id() {
		return waiter_id;
	}

	public void setWaiter_id(int waiter_id) {
		this.waiter_id = waiter_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
