package com.haffee.heygay.po;

import java.sql.Timestamp;
import java.util.List;
/**
 * 商品
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class Goods {

	private int good_id; //商品ID
	private int shop_id; //店铺ID
	private int category_id; //分类ID
	private String goods_name; //商品名称
	private int sales_count; // 销量
	private float pre_price; //单价
	private float discount; // 折扣 1代表不打折
	private String img_url; //图片路径
	private Timestamp create_time;// 创建时间
	private String introduction;// 简介
	private int status; // 状态
	private int in_cart_num; //在购物车中商品数量
	private int if_print = 1; //0:否，1是，默认1
	private String print_str;//打印机编号，多个用逗号分隔
	private String print_name_str;
	private GoodsCategory ctg;
	private List<Object> printer_list;
	private String tuijian;

	public int getGood_id() {
		return good_id;
	}

	public void setGood_id(int good_id) {
		this.good_id = good_id;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public int getSales_count() {
		return sales_count;
	}

	public void setSales_count(int sales_count) {
		this.sales_count = sales_count;
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


	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIn_cart_num() {
		return in_cart_num;
	}

	public void setIn_cart_num(int in_cart_num) {
		this.in_cart_num = in_cart_num;
	}

	public int getIf_print() {
		return if_print;
	}

	public void setIf_print(int if_print) {
		this.if_print = if_print;
	}

	public String getPrint_str() {
		return print_str;
	}

	public void setPrint_str(String print_str) {
		this.print_str = print_str;
	}

	public String getPrint_name_str() {
		return print_name_str;
	}

	public void setPrint_name_str(String print_name_str) {
		this.print_name_str = print_name_str;
	}

	public GoodsCategory getCtg() {
		return ctg;
	}

	public void setCtg(GoodsCategory ctg) {
		this.ctg = ctg;
	}

	public List<Object> getPrinter_list() {
		return printer_list;
	}

	public void setPrinter_list(List<Object> printer_list) {
		this.printer_list = printer_list;
	}

	public String getTuijian() {
		return tuijian;
	}

	public void setTuijian(String tuijian) {
		this.tuijian = tuijian;
	}
	
}
