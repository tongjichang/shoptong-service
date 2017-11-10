package com.haffee.heygay.po;

import java.sql.Timestamp;
import java.util.List;

public class Area {

	private int area_id; //分区ID
	private int shop_id; //店铺ID
	private String area_name; //分区名称
	private Timestamp create_time;//创建时间
	private List<Object> table_list;//桌台列表

	public int getArea_id() {
		return area_id;
	}

	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public List<Object> getTable_list() {
		return table_list;
	}

	public void setTable_list(List<Object> table_list) {
		this.table_list = table_list;
	}

}
