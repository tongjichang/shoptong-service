package com.haffee.heygay.po;

/**
 * 二级代码表
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class AA10 {

	private String id;//自定义，根据code+code_value 格式
	private String code;
	private String code_name;
	private String code_value;
	private int order_num;// 排序

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode_name() {
		return code_name;
	}

	public void setCode_name(String code_name) {
		this.code_name = code_name;
	}

	public String getCode_value() {
		return code_value;
	}

	public void setCode_value(String code_value) {
		this.code_value = code_value;
	}

	public int getOrder_num() {
		return order_num;
	}

	public void setOrder_num(int order_num) {
		this.order_num = order_num;
	}

}
