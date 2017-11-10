package com.haffee.heygay.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.BookInfo;
import com.haffee.heygay.po.TableInfo;

public class TaskUtil extends java.util.TimerTask{
	@Autowired
	private static IUserDao dao;
	
	private String table_id;
	
	private String book_id;
	
	public TaskUtil(){
		
	}
	
	public TaskUtil(String table_id,String book_id){
		this.table_id = table_id;
		this.book_id = book_id;
		if(null==dao){
			dao = (IUserDao)SpringContextUtils.getBean("dao");
		}
	}
	
    public void run(){     
    	changeTableStatus(table_id,book_id);
    	System.out.println("预定信息，更新桌台ID："+table_id+" 时间："+new Date());
    }  
    
    /**
	 * 更新桌台信息
	 * @param book_id
	 * @return
	 */
	public boolean changeTableStatus(String table_id,String book_id){
		boolean isSuccess = false;
		TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+table_id);
		if(null!=table){
			if(table.getTable_status()==2){
				table.setTable_status(0);
				dao.doUpdateObject(table);
				isSuccess = true;
			}
		}
		BookInfo book = (BookInfo)dao.getOneObject("from BookInfo b where b.book_id="+book_id);
		if(null!=book){
			book.setStatus(3);
			dao.doUpdateObject(book);
		}
		return isSuccess;
	}
}
