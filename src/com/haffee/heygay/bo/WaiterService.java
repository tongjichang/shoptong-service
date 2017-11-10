package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Shop;
import com.haffee.heygay.po.Suggest;
import com.haffee.heygay.po.User;
import com.haffee.heygay.po.Waiter;

/**
 * 服务员管理 适用于店铺管理员
 * 
 * @author jacktong
 *
 */
@Controller
public class WaiterService {

	@Autowired
	private IUserDao dao;

	/**
	 * 新增服务员
	 * 
	 * @param w
	 * @return
	 */
	@RequestMapping(value = "/waiter/addWaiter", method = RequestMethod.POST)
	public @ResponseBody Object doAddWaiter(Waiter w) {
		String message = "成功";
		String code = "1000";
		int roleid = w.getWaiter_id();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//查询是否重名
			User user = (User)dao.getOneObject("from User u where u.user_name='"+w.getUsername()+"' and type=2");
			if(null!=user){
				code = "1002";
				message = "用户名重复，请更换后保存";
			}else{
				Date date = new Date();
				w.setCreate_time(new Timestamp(date.getTime()));
				dao.doSaveObject(w);
				Waiter waiter = (Waiter) dao
						.getOneObject("from Waiter w where w.shop_id=" + w.getShop_id() + " and w.username='" + w.getUsername()+"'");
				User u = new User();
				u.setBusiness_id(waiter.getWaiter_id());
				u.setCreate_time(new Timestamp(date.getTime()));
				u.setPassword(waiter.getUsername());
				u.setRole_id(roleid);// 暂时不用
				u.setType(2);// 服务员
				u.setUser_name(waiter.getUsername());
				u.setUser_status(1);
				dao.doSaveObject(u);
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
	@RequestMapping(value = "/waiter/changWaiterStatus", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateWaiter(String waiter_id, String status) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			User u = (User) dao.getOneObject("from User u where u.type=2 and u.business_id=" + waiter_id);
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
	 * 删除服务员
	 * 
	 * @param waiter_id
	 * @return
	 */
	@RequestMapping(value = "/waiter/deleteWaiter", method = RequestMethod.POST)
	public @ResponseBody Object doDeleteWaiter(String waiter_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			User u = (User) dao.getOneObject("from User u where u.business_id=" + waiter_id);
			Waiter w = (Waiter) dao.getOneObject("from Waiter w where w.waiter_id=" + waiter_id);
			if(null!=u){
				dao.doDeleteObject(u);
			}
			if(null!=w){
				dao.doDeleteObject(w);
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
	 * 分页查询服务员
	 * 店铺管理员功能
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value="/waiter/selectWaiters",method=RequestMethod.POST)
	public @ResponseBody Object selectAllWaiter(String shop_id,int pageNo){
		if(pageNo==0){
			pageNo = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getOnePageResult(10, pageNo, "from Waiter w where w.shop_id="+shop_id);
			for (Object object : list) {
				Waiter w = (Waiter)object;
				User u = (User)dao.getOneObject("from User u where business_id="+w.getWaiter_id());
				w.setUser(u);
			}
			int total = dao.getAllNumber("from Waiter w where w.shop_id="+shop_id);
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
	 * 密码重置
	 * 
	 * @param waiter_id
	 * @return
	 */
	@RequestMapping(value = "/waiter/czWaiter", method = RequestMethod.POST)
	public @ResponseBody Object doczWaiter(String waiter_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			User u = (User) dao.getOneObject("from User u where u.business_id=" + waiter_id);
			if(null!=u){
				u.setPassword("000000");
				dao.doUpdateObject(u);
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
	 * 服务员密码修改
	 * 
	 * @param shop
	 * @return
	 */
	@RequestMapping(value = "/waiter/updatePassword", method = RequestMethod.POST)
	public @ResponseBody Object doUpdatewaiter(String oldpassword,String password,String passwordnew,int waiter_id,HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			User u = (User) dao.getOneObject("from User u where u.business_id=" + waiter_id);
			if (null != u) {
				if(!oldpassword.equals(u.getPassword())){
					code = "1001";
					message = "原密码不正确";
				}else{
					if(!password.equals(passwordnew)){
						code = "1001";
						message = "确认密码和新密码不一致";
					}else{
						u.setPassword(passwordnew);
						dao.doUpdateObject(u);
					}	
				}

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
	 * 服务员提交建议
	 * @param waiter_id
	 * @param content
	 * @return
	 */
	@RequestMapping(value="/waiter/saveSuggest",method=RequestMethod.POST)
	public @ResponseBody Object doSaveSuggess(String waiter_id,String content){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtils.isEmpty(waiter_id)||StringUtils.isEmpty(content)){
				code = "1002";
				message = "参数异常";
			}else{
				Waiter waiter = (Waiter)dao.getOneObject("from Waiter w where w.waiter_id="+waiter_id);
				if(null!=waiter){
					Shop shop = (Shop)dao.getOneObject("from Shop s where s.shop_id="+waiter.getShop_id());
					Suggest s = new Suggest();
					s.setWaiter_id(waiter.getWaiter_id());
					s.setWaiter_name(waiter.getName());
					if(null!=shop){
						s.setShop_id(shop.getShop_id());
						s.setShop_name(shop.getShop_name());
					}
					s.setContent(content);
					Date date = new Date();
					s.setCreate_time(new Timestamp(date.getTime()));
					dao.doSaveObject(s);
				}
				
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
