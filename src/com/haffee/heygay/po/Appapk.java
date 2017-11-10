package com.haffee.heygay.po;

import java.sql.Timestamp;

/**
 * APP
 * 
 * @author WANGJT
 * @date 2017年7月4日
 *
 */
public class Appapk {

	private int id;//ID
	private String url;//下载路径
	private String version;//版本
	private Timestamp time;//时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}


}
