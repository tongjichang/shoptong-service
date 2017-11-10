package com.haffee.heygay.po;

import java.sql.Timestamp;
import java.util.List;

/**
 * 商品分类
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class GoodsCategory {

	private int category_id;//分类ID
	private String category_name;//分类名称
	private int shop_id;//店铺ID
	private Timestamp create_time;//创建时间
	private int category_status;//分类状态1:正常，2:下架<
	private List<Object> goods_list;//菜品列表

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}


	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public int getCategory_status() {
		return category_status;
	}

	public void setCategory_status(int category_status) {
		this.category_status = category_status;
	}

	public List<Object> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Object> goods_list) {
		this.goods_list = goods_list;
	}

}
