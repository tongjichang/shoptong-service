package com.haffee.heygay.po;

import java.sql.Timestamp;
import java.util.List;

/**
 * 餐桌
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class TableInfo {
	private int table_id;//桌台ID
	private String table_name;//桌台名称
	private int area_id; // 区域ID
	private int shop_id;//店铺ID
	private int table_status;//桌台状态0:未使用，1:正常使用中，2:顾客预定，3:服务员占用，4:其他
	private int table_book_id;// 预定信息
	private Timestamp create_time;//创建时间
	private int people_count; // 容纳人数
	private List<Object> book_list; //预定信息
	private Waiter waiter;//服务员
	private OrderInfo order_info;//订单信息
	private ShoppingCart cart;//购物车
	private String area_name;

	public int getTable_id() {
		return table_id;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}

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

	public int getTable_status() {
		return table_status;
	}

	public void setTable_status(int table_status) {
		this.table_status = table_status;
	}

	public int getTable_book_id() {
		return table_book_id;
	}

	public void setTable_book_id(int table_book_id) {
		this.table_book_id = table_book_id;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public int getPeople_count() {
		return people_count;
	}

	public void setPeople_count(int people_count) {
		this.people_count = people_count;
	}

	public List<Object> getBook_list() {
		return book_list;
	}

	public void setBook_list(List<Object> book_list) {
		this.book_list = book_list;
	}

	public Waiter getWaiter() {
		return waiter;
	}

	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}

	public OrderInfo getOrder_info() {
		return order_info;
	}

	public void setOrder_info(OrderInfo order_info) {
		this.order_info = order_info;
	}

	public ShoppingCart getCart() {
		return cart;
	}

	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

}
