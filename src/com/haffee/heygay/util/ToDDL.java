package com.haffee.heygay.util;



import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class ToDDL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Configuration configure = new Configuration();
		configure.configure("hibernate.cfg.xml");
		SchemaExport sc = new SchemaExport(configure);
		sc.create(true, true);
		
	}

}
