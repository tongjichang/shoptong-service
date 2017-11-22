package com.haffee.heygay.po;

import java.util.Date;

/**
 * 二级代码表
 * 
 * @author jacktong
 * @date 2017年7月7日
 *
 */
public class AA10 {

	private int id;//自定义，根据code+code_value 格式
	private String code;
	private String phone;
	private String ip;
	private String remark;// 排序
	private Date send_time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getSend_time() {
		return send_time;
	}

	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}


}
