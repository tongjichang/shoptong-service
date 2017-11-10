package com.haffee.heygay.po;

import java.sql.Timestamp;

public class ShopPrinter {

	private int id;
	private int shop_id;
	private String printer_no; //打印机编号
	private String printer_name; //打印机名称
	private String type_print; //1:后厨，2：结账
	private Timestamp create_time;// 创建时间

	public String getType_print() {
		return type_print;
	}

	public void setType_print(String typePrint) {
		type_print = typePrint;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getShop_id() {
		return shop_id;
	}

	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
	}

	public String getPrinter_no() {
		return printer_no;
	}

	public void setPrinter_no(String printer_no) {
		this.printer_no = printer_no;
	}

	public String getPrinter_name() {
		return printer_name;
	}

	public void setPrinter_name(String printer_name) {
		this.printer_name = printer_name;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

}
