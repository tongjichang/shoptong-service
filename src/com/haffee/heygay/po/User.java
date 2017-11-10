package com.haffee.heygay.po;

import java.sql.Timestamp;
/**
 * 用户
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class User {

	private int user_id;//用户ID
	private int role_id;//角色ID
	private String user_name;//用户名
	private String password;//密码
	private int user_status;//用户状态
	private int type;//类型
	private Timestamp create_time;//创建时间
	private int business_id;//如果服务员 存服务员ID，如果是管理员，存管理员ID
	private String login_key;//登录
	private Timestamp key_update_time;//上次操作时间
	private Waiter waiter;
	private String alias;//app 注册别名

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getRole_id() {
		return role_id;
	}

	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUser_status() {
		return user_status;
	}

	public void setUser_status(int user_status) {
		this.user_status = user_status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public int getBusiness_id() {
		return business_id;
	}

	public void setBusiness_id(int business_id) {
		this.business_id = business_id;
	}

	public String getLogin_key() {
		return login_key;
	}

	public void setLogin_key(String login_key) {
		this.login_key = login_key;
	}

	public Timestamp getKey_update_time() {
		return key_update_time;
	}

	public void setKey_update_time(Timestamp key_updat_time) {
		this.key_update_time = key_updat_time;
	}

	public Waiter getWaiter() {
		return waiter;
	}

	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
