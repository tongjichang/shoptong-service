package com.haffee.heygay.po;

/**
 * 店铺扩展-包括招聘信息、促销信息等
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class ShopExt {

	private int shop_ext_id;
	private int shop_id;
	private String shop_ext_code;
	private String content;

	public int getShop_ext_id() {
		return shop_ext_id;
	}

	public void setShop_ext_id(int shop_ext_id) {
		this.shop_ext_id = shop_ext_id;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public String getShop_ext_code() {
		return shop_ext_code;
	}

	public void setShop_ext_code(String shop_ext_code) {
		this.shop_ext_code = shop_ext_code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
