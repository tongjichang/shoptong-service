package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.User;

import cn.jiguang.common.utils.StringUtils;

/**
 * 超级用户管理 适用于管理员
 * 
 * @author wangjt
 *
 */
@Controller
public class SuperManageService {

	@Autowired
	private IUserDao dao;

	/**
	 * 查询用户信息
	 * 
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/super/selectsuper", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateShop(String user_name,String type,int pageNo) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		 String sql = "select * from heygay.User where 1=1";
	        StringBuffer sql2 = new StringBuffer();
			List<Object> objs = new LinkedList<Object>();
			sql2.append(sql);
			if (!StringUtils.isEmpty(user_name)) {
				sql2.append(" and user_name = ?");
				objs.add(user_name);
			}
			if (!StringUtils.isEmpty(type)) {
				sql2.append(" and role_id = ?");
				objs.add(type);
			}
		try {
			List<User> list = dao.findBySqlnew(10, pageNo,sql2.toString(),objs.toArray());
			int total = list.size();
			int all_page_no = total%10>0?total/10+1:total/10;
			map.put("DATA", list);
			map.put("totalnum", total);
			map.put("page_no", pageNo);
			map.put("pre_no", pageNo-1>0?pageNo-1:0);
			map.put("post_no", pageNo+1>all_page_no?all_page_no:pageNo+1);
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
	 * 新增店铺
	 * 
	 * @param shop
	 * @return
	 */
	@RequestMapping(value = "/super/addSuper", method = RequestMethod.POST)
	public @ResponseBody Object doAddShop(User userin,HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		User u = (User) dao.getOneObject("from User u where u.user_name='"+ userin.getUser_name()+"'");
		if(null!=u){
			code = "1002";
			message = "用户已经存在";
		}
			// 获得文件：
		try {
			//1.创建User
			User user = new User();
			user.setUser_name(userin.getUser_name());
			user.setPassword(userin.getPassword());//默认6个0
			user.setUser_status(1);
			user.setType(1);
			user.setRole_id(3);
			Date date = new Date();
			user.setCreate_time(new Timestamp(date.getTime()));
			dao.doSaveObject(user);
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
	 * 删除用户
	 * 
	 * @param user_id
	 * @return
	 */
	@RequestMapping(value = "/super/deleteUser", method = RequestMethod.POST)
	public @ResponseBody Object doDeleteWaiter(String user_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			User u = (User) dao.getOneObject("from User u where u.user_id=" + user_id);
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
	@RequestMapping(value = "/super/changuserStatus", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateWaiter(String user_id, String status) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			User u = (User) dao.getOneObject("from User u where u.user_id=" + user_id);
			if (null != u) {
				u.setUser_status(Integer.valueOf(status));
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
	 * 查询建议
	 * @param shop_id
	 * @param page_no
	 * @return
	 */
	@RequestMapping(value="/super/selectSuggest",method=RequestMethod.POST)
	public @ResponseBody Object selectSuggest(int page_no){
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String sql = "from Suggest s order by s.create_time desc";
			List<Object> list = dao.getOnePageResult(10, page_no, sql);
			int total = dao.getAllNumber(sql);
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
	 * 用户销量统计
	 * @param time_flag 1：当天，2：近一周，3：近一月(默认) 
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @return
	 */
	@RequestMapping(value="/statics/userStatics",method = RequestMethod.POST)
	public @ResponseBody Object waitStatics1(String time_flag, String start_time, String end_time,
			String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		
		String sql = "select (SELECT SHOP_NAME FROM SHOP WHERE SHOP_ID = b.SHOP_id) name, b.SHOP_id,count(a.SHOP_id)  zs,sum(a.payment)  ze from orderInfo a,Shop b where  a.shop_id = b.shop_id";
        StringBuffer sql2 = new StringBuffer();
		List<Object> objs = new LinkedList<Object>();
		sql2.append(sql);
		if(start_time != null&&end_time!=null){
			sql2.append(" and  date(a.CREATE_TIME) between ? and ? ");
			objs.add(start_time);
			objs.add(end_time);
		}else{
			if (!StringUtils.isEmpty(time_flag)) {
				if(time_flag.equals("1")){
					sql2.append(" and to_days(a.create_time) = to_days(now())");
				}else if(time_flag.equals("2")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(a.create_time)");
				}else if(time_flag.equals("3")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(a.create_time)");
				}
			}else{
				sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(a.create_time)");
			}
		}
		sql2.append(" GROUP BY  name,b.SHOP_id order by zs desc");
		List<Object> list = dao.findBySqlnew2(10, 1,sql2.toString(),objs.toArray());
		map.put("DATA", list);
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 用户销量统计
	 * @param time_flag 1：当天，2：近一周，3：近一月(默认) 
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @return
	 */
	@RequestMapping(value="/statics/userStatics2",method = RequestMethod.POST)
	public @ResponseBody Object waitStatics2(String time_flag, String start_time, String end_time,
			String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		
		String sql = "select '用户统计' name,(select count(1) from Shop)  zs,count(a.SHOP_id)  ze from orderInfo a,Shop b where  a.shop_id = b.shop_id";
        StringBuffer sql2 = new StringBuffer();
		List<Object> objs = new LinkedList<Object>();
		sql2.append(sql);
//		if(start_time != null&&end_time!=null){
//			sql2.append(" and  date(a.CREATE_TIME) between ? and ? ");
//			objs.add(start_time);
//			objs.add(end_time);
//		}else{
//			if (!StringUtils.isEmpty(time_flag)) {
//				if(time_flag.equals("1")){
//					sql2.append(" and to_days(a.create_time) = to_days(now())");
//				}else if(time_flag.equals("2")){
//					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(a.create_time)");
//				}else if(time_flag.equals("3")){
//					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(a.create_time)");
//				}
//			}else{
//				sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(a.create_time)");
//			}
//		}
//		sql2.append(" GROUP BY  b.SHOP_NAME,b.SHOP_id order by zs desc");
		List<Object> list = dao.findBySqlnew2(10, 1,sql2.toString(),objs.toArray());
		map.put("DATA", list);
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
}
