package com.haffee.heygay.po;

import java.sql.Timestamp;

public class BookInfo {

	private int book_id; //预定ID
	private int shop_id;//店铺ID
	private int table_id;//桌台ID
	private String table_name;//桌台ID
	private String name;//姓名
	private String phone;//店铺
	private int people_num;//就餐人数
	private String use_time;//用餐时间
	private float pre_fee; // 押金
	private Timestamp create_time;//创建时间
	private String remark;//备注
	private int status;//状态1：正常，2：取消，3：到期

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String tableName) {
		table_name = tableName;
	}

	public int getBook_id() {
		return book_id;
	}

	public void setBook_id(int book_id) {
		this.book_id = book_id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getPeople_num() {
		return people_num;
	}

	public void setPeople_num(int people_num) {
		this.people_num = people_num;
	}

	public String getUse_time() {
		return use_time;
	}

	public void setUse_time(String use_time) {
		this.use_time = use_time;
	}

	public float getPre_fee() {
		return pre_fee;
	}

	public void setPre_fee(float pre_fee) {
		this.pre_fee = pre_fee;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
