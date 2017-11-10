package com.haffee.heygay.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.dao.IUserDaoImpl;
import com.haffee.heygay.po.BookInfo;
import com.haffee.heygay.po.Goods;
import com.haffee.heygay.po.OrderInfo;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.po.Waiter;

/**
 * 定时任务请求申报结果
 * 
 * @author wangjt
 *
 */
@Controller
public class ScheduledTasks extends TimerTask {
	
	private int bookid; 
	
	public int getBookid() {
		return bookid;
	}
	public void setBookid(int bookid) {
		this.bookid = bookid;
	}
	// 构造函数  
    public ScheduledTasks(int bookid) {  
        this.bookid = bookid;     
    } 
 // 构造函数  
    public ScheduledTasks() {   
    } 
    @Autowired
	private IUserDao dao;
     public void run() {
    	 BookInfo b = (BookInfo) dao.getOneObject("from BookInfo a where a.book_id=" + bookid);
		 System.out.println("满足条件的预约,更新状态为到期：姓名为" + b.getName());
		 if(b.getStatus()==1){
			 b.setStatus(3);
			 dao.doUpdateObject(b);
			 TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + b.getTable_id());
			 if(t.getTable_status()==0){
				 System.out.println("预约的桌台为空闲则更新为预约状态：桌台名为：" + t.getTable_name());
				 t.setTable_status(2);
				 dao.doUpdateObject(t);
			 } 
		 }
     }
     private SimpleDateFormat dateFormat() {
         return new SimpleDateFormat("HH:mm:ss");
     }


}
