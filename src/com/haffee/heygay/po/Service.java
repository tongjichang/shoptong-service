package com.haffee.heygay.po;

import java.sql.Timestamp;

/**
 * 每桌服务
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class Service {
	private int service_id;
	private int shop_id;
	private int table_id;
	private int service_type;
	private String service_content;
	private int waiter_id;
	private Timestamp create_time;
	private Timestamp receive_time;
	private int status;
	private String if_notice;
	private Waiter waiter;
	private Area area;
	private TableInfo table;

	public int getService_id() {
		return service_id;
	}

	public void setService_id(int service_id) {
		this.service_id = service_id;
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

	public int getService_type() {
		return service_type;
	}

	public void setService_type(int service_type) {
		this.service_type = service_type;
	}

	public String getService_content() {
		return service_content;
	}

	public void setService_content(String service_content) {
		this.service_content = service_content;
	}

	public int getWaiter_id() {
		return waiter_id;
	}

	public void setWaiter_id(int waiter_id) {
		this.waiter_id = waiter_id;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public Timestamp getReceive_time() {
		return receive_time;
	}

	public void setReceive_time(Timestamp receive_time) {
		this.receive_time = receive_time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Waiter getWaiter() {
		return waiter;
	}

	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public TableInfo getTable() {
		return table;
	}

	public void setTable(TableInfo table) {
		this.table = table;
	}

	public String getIf_notice() {
		return if_notice;
	}

	public void setIf_notice(String if_notice) {
		this.if_notice = if_notice;
	}


}
