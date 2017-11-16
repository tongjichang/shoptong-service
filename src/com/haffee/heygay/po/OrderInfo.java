package com.haffee.heygay.po;

import java.sql.Timestamp;

/**
 * 订单
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class OrderInfo {

	private String order_id;//订单ID
	private String user_id; //用户ID
	private String user_phone; //用户电话
	private String order_num; //订单编号
	private int shop_id;//店铺ID
	private int area_id;//分区ID
	private int table_id;//桌台ID
	private float payment; // 应收金额
	private float discount_payment; // 折扣后金额
	private int goods_num;//商品数量
	private float discount;// 折扣
	private float real_payment;// 实际收用户金额
	private float minus_money;// 抹零金额
	private String shopping_cart_id;//购物车ID
	private int waiter_id;//服务员ID
	private Timestamp create_time;//创建时间
	private String comments;// 备注
	private int status;// 订单状态0:未处理，1:已结账，2:取消,-1:顾客提交,服务员未确认
	private ShoppingCart cart;//购物车
	private Timestamp order_time;//结账时间
	private int people_count;//就餐人数
	private Area area;//分区
	private Waiter waiter;//服务员
	private TableInfo table;//桌台
	private int way;  //1:堂食，2：打包
	private int go_goods_way;//上菜方式 1.做好即上，2：等待叫起
	private String address;//送货地址

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public String getOrder_num() {
		return order_num;
	}

	public void setOrder_num(String order_num) {
		this.order_num = order_num;
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

	public float getPayment() {
		return payment;
	}

	public void setPayment(float payment) {
		this.payment = payment;
	}

	public float getDiscount_payment() {
		return discount_payment;
	}

	public void setDiscount_payment(float discount_payment) {
		this.discount_payment = discount_payment;
	}

	public int getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(int goods_num) {
		this.goods_num = goods_num;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getReal_payment() {
		return real_payment;
	}

	public void setReal_payment(float real_payment) {
		this.real_payment = real_payment;
	}

	public float getMinus_money() {
		return minus_money;
	}

	public void setMinus_money(float minus_money) {
		this.minus_money = minus_money;
	}

	public String getShopping_cart_id() {
		return shopping_cart_id;
	}

	public void setShopping_cart_id(String shopping_cart_id) {
		this.shopping_cart_id = shopping_cart_id;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ShoppingCart getCart() {
		return cart;
	}

	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}

	public int getArea_id() {
		return area_id;
	}

	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}

	public Timestamp getOrder_time() {
		return order_time;
	}

	public void setOrder_time(Timestamp order_time) {
		this.order_time = order_time;
	}

	public int getPeople_count() {
		return people_count;
	}

	public void setPeople_count(int people_count) {
		this.people_count = people_count;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Waiter getWaiter() {
		return waiter;
	}

	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}

	public TableInfo getTable() {
		return table;
	}

	public void setTable(TableInfo table) {
		this.table = table;
	}

	public int getWay() {
		return way;
	}

	public void setWay(int way) {
		this.way = way;
	}

	public int getGo_goods_way() {
		return go_goods_way;
	}

	public void setGo_goods_way(int go_goods_way) {
		this.go_goods_way = go_goods_way;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
