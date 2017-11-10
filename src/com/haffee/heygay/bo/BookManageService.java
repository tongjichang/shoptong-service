package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.BookInfo;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.util.TaskUtil;

import cn.jiguang.common.utils.StringUtils;

/**
 * 超级用户管理 适用于管理员
 * 
 * @author wangjt
 *
 */
@Controller
public class BookManageService {

	@Autowired
	private IUserDao dao;
	@Autowired
	private ScheduleUtils Utils;

	/**
	 * 查询订单--分页 店铺
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/book/selectbooks", method = RequestMethod.POST)
	public @ResponseBody Object selectOrders(String shop_id,String name,int page_no) {
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String sql = "from BookInfo b where b.shop_id = " + shop_id+" order by b.create_time desc";
	        StringBuffer sql2 = new StringBuffer();
			List<Object> objs = new LinkedList<Object>();
			sql2.append(sql);
			if (!StringUtils.isEmpty(name)) {
				sql2.append(" and b.name ="+name);
			}
			List<Object> list = dao.getOnePageResult(10, page_no, sql2.toString());
			int total = dao.getAllNumber(sql2.toString());
			int all_page_no = total % 10 > 0 ? total / 10 + 1 : total / 10;
			map.put("DATA", list);
			map.put("totalnum", total);
			map.put("page_no", page_no);
			map.put("pre_no", page_no - 1 > 0 ? page_no - 1 : 0);
			map.put("post_no", page_no + 1 > all_page_no ? all_page_no : page_no + 1);
		} catch (Exception e) {
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}

	/**
	 * 新增预定
	 * 
	 * @param shop
	 * @return
	 */
	@RequestMapping(value = "/book/addBook", method = RequestMethod.POST)
	public @ResponseBody Object doAddShop(BookInfo bookInfo,HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + bookInfo.getTable_id());
		Date date = new Date(); 
		int h = date.getHours();
		if(Integer.parseInt(bookInfo.getUse_time().split(":")[0])<h){
			code = "1001";
			message = "预定时间不能小于当前时间";
		}else{
			if (null == t) {
				code = "1001";
				message = "未查到数据";
			} else {
				// 获得文件：
				try {
					//1.创建User
					BookInfo book = new BookInfo();
					book.setCreate_time(new Timestamp(date.getTime()));
					book.setName(bookInfo.getName());
					book.setPeople_num(bookInfo.getPeople_num());
					book.setPhone(bookInfo.getPhone());
					book.setPre_fee(bookInfo.getPre_fee());
					book.setShop_id(bookInfo.getShop_id());
					book.setTable_id(bookInfo.getTable_id());
					book.setTable_name(t.getTable_name());
					book.setUse_time(bookInfo.getUse_time());
					book.setStatus(1);
					BookInfo b0 = (BookInfo) dao.getOneObject("from BookInfo t where t.table_id=" + bookInfo.getTable_id()+" and shop_id ="+bookInfo.getShop_id()+" and status = 1");
					if (null != b0) {
						code = "1001";
						message = "该桌位已经被预定";
					}else{
						dao.doSaveObject(book);
						if(t.getTable_status()==0){
							t.setTable_status(2);
							dao.doUpdateObject(t);
						}
						BookInfo book_id = (BookInfo)dao.getOneObject("from BookInfo b where b.table_id="+bookInfo.getTable_id()+" and b.shop_id="+bookInfo.getShop_id()+" and b.phone="+bookInfo.getPhone());
						Calendar calendar = Calendar.getInstance();  
				        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(book.getUse_time().split(":")[0]));  
				        calendar.set(Calendar.MINUTE, Integer.valueOf(book.getUse_time().split(":")[1]));  
				        calendar.set(Calendar.SECOND, 0);  
				        Date time = calendar.getTime();  
				        Timer timer = new Timer();  
				        timer.schedule(new TaskUtil(book.getTable_id()+"",book_id.getBook_id()+""), time);
				        
					}


				} catch (Exception e) {
					code = "1002";
					message = "系统异常";
					e.printStackTrace();
				}
			}
		}
		
		
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 删除用户
	 * 
	 * @param user_id
	 * @return
	 */
	@RequestMapping(value = "/book/deleteBook", method = RequestMethod.POST)
	public @ResponseBody Object doDeleteWaiter(String book_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BookInfo u = (BookInfo) dao.getOneObject("from BookInfo u where u.book_id=" + book_id);
			if(null!=u){
				dao.doDeleteObject(u);
			}
		} catch (Exception e) {
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	/**
	 * 冻结、解冻服务员账号
	 * 
	 * @param waiter_id
	 * @return
	 */
	@RequestMapping(value = "/book/changuserStatus", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateWaiter(String book_id, String status) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BookInfo u = (BookInfo) dao.getOneObject("from BookInfo u where u.book_id=" + book_id);
			if (null != u) {
				u.setStatus(Integer.valueOf(status));
				dao.doUpdateObject(u);
			} else {
				code = "1001";
				message = "未查询到数据";
			}
		} catch (Exception e) {
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	/**
	 * 查询所有分区信息
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/book/getArea", method = RequestMethod.POST)
	public @ResponseBody Object selectArea(String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> area_list = dao.getAllObject("from Area a where a.shop_id=" + shop_id);
			if (null != area_list && area_list.size() > 0) {
				map.put("DATA", area_list);
			} else {
				code = "1001";
				message = "未查到数据";
			}
		} catch (Exception e) {
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	/**
	 * 查询所有table信息
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/book/getTable", method = RequestMethod.POST)
	public @ResponseBody Object selectArea(String shop_id,String area_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getAllObject("from TableInfo t where t.area_id = " + area_id);
			if (null != list && list.size() > 0) {
				map.put("DATA", list);
			} else {
				code = "1001";
				message = "未查到数据";
			}
		} catch (Exception e) {
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 查询单个预定信息
	 * @param book_id
	 * @return
	 */
	@RequestMapping(value="/book/getOneBookInfo",method=RequestMethod.POST)
	public @ResponseBody Object selectOneBookInfo(String book_id){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BookInfo book = (BookInfo)dao.getOneObject("from BookInfo b where b.book_id="+book_id);
			if(null!=book){
				TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+book.getTable_id());
				if(null!=table){
					map.put("AREA_ID", table.getArea_id());
				}
			}
			if(null!=book){
				map.put("DATA", book);
			}else{
				code = "1001";
				message = "未查到数据";
			}
		} catch (Exception e) {
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 更新预定信息
	 * @param book
	 * @return
	 */
	@RequestMapping(value="/book/updateBookInfo",method=RequestMethod.POST)
	public @ResponseBody Object doUpdateBookInfo(BookInfo book){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			BookInfo book_db = (BookInfo)dao.getOneObject("from BookInfo b where b.book_id="+book.getBook_id());
			if(null!=book_db){
				TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+book.getTable_id());
				book_db.setName(book.getName());
				book_db.setTable_id(book.getTable_id());
				if(null!=table){
					book_db.setTable_name(table.getTable_name());
				}
				book_db.setPre_fee(book.getPre_fee());
				book_db.setUse_time(book.getUse_time());
				book_db.setRemark(book.getRemark());
				book_db.setPeople_num(book.getPeople_num());
				book_db.setPhone(book.getPhone());
				dao.doUpdateObject(book_db);
				
				Calendar calendar = Calendar.getInstance();  
		        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(book_db.getUse_time().split(":")[0]));  
		        calendar.set(Calendar.MINUTE, Integer.valueOf(book_db.getUse_time().split(":")[1]));  
		        calendar.set(Calendar.SECOND, 0);  
		        Date time = calendar.getTime();  
		        Timer timer = new Timer();  
		        timer.schedule(new TaskUtil(book_db.getTable_id()+"",book_db.getBook_id()+""), time);
			}else{
				code = "1002";
				message = "未查询到预定信息";
			}
		} catch (Exception e) {
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
  
	
	
}
