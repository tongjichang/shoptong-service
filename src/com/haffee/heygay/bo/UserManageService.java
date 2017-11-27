package com.haffee.heygay.bo;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.AA10;
import com.haffee.heygay.po.User;
import com.haffee.heygay.po.Waiter;
import com.haffee.heygay.util.SmsUtils;

import net.sf.json.JSONObject;

/**
 * 系统用户管理
 * @author jacktong
 * @date 2017年7月4日
 *
 */
@Controller
public class UserManageService {
	@Autowired
	private IUserDao dao;
	
	/**
	 * 登陆
	 * @param username
	 * @param password
	 * @param type
	 * @param response
	 */
	@RequestMapping(value = "/userservice/login", method = RequestMethod.POST)
	@ResponseBody
	public void doLogin(User user,HttpServletRequest request,HttpServletResponse response){
		System.out.println("---------------------------");
		System.out.println(user.getUser_name());
		System.out.println(user.getPassword());
		System.out.println(user.getType());
		System.out.println("---------------------------");
		String message = "成功";
		String code = "1000";		
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			User in_user = new User();
			in_user.setUser_name(user.getUser_name());
			User out_user = dao.getUserByUsername(in_user);
			if(null == out_user||out_user.getUser_name().equals("")||out_user.getType()!=Integer.valueOf(user.getType())){
				code = "1001";
				message = "无此人账户信息";
			}else if(out_user.getUser_status()!=1){
				code = "1002";
				message = "账户冻结";
			}else{
				if(user.getPassword().equals(out_user.getPassword())){
					if(out_user.getType()==2){
						Waiter w = (Waiter)dao.getOneObject("from Waiter w where waiter_id="+out_user.getBusiness_id());
						out_user.setWaiter(w);
						out_user.setAlias(out_user.getUser_name()+"_"+out_user.getBusiness_id());
					}
					String login_key = request.getSession().getId();
					out_user.setLogin_key(login_key);
					Date date = new Date();
					out_user.setKey_update_time(new Timestamp(date.getTime()));
					dao.doUpdateObject(out_user);
					map.put("DATA", out_user);
					map.put("KEY", login_key);
				}else{
					code = "1001";
					message = "账号密码不匹配";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			code = "1002";
			message = "系统异常";
			e.printStackTrace();
		}
		
		map.put("CODE", code);
		map.put("MESSAGE", message);
		JSONObject json = JSONObject.fromObject(map);
		response.setContentType("text/html;charset=utf-8");
		try {
			System.out.println(json.toString());
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 注销
	 * @param user_id
	 * @return
	 */
	@RequestMapping(value="/userservice/logout",method=RequestMethod.POST)
	public @ResponseBody Object dologOut(String business_id){
		String message = "成功";
		String code = "1000";		
		Map<String,Object> map = new HashMap<String,Object>();
		if(StringUtils.isEmpty(business_id)){
			code = "1002";
			message = "参数异常";
		}else{
			User user = (User)dao.getOneObject("from User u where u.business_id = "+business_id);
			if(null==user){
				code = "1001";
				message = "未查到用户";
			}else{
				user.setLogin_key(null);
				dao.doUpdateObject(user);
			}
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 校验用户是否存在
	 * @param username
	 * @return
	 */
	@RequestMapping(value="/userservice/checkUser",method=RequestMethod.POST)
	public @ResponseBody Object checkUserExists(String username){
		String message = "成功";
		String code = "1000";		
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			User user = (User)dao.getOneObject("from User u where u.user_name='"+username+"' and u.role_id=9");
			if(null!=user){
				map.put("DATA", "1");
			}else{
				map.put("DATA", "0");
			}
		} catch (Exception e) {
			map.put("DATA", "0");
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 注册用户
	 * @param phone_no
	 * @return
	 */
	@RequestMapping(value="/userservice/registePhone",method=RequestMethod.POST)
	public @ResponseBody Object registePhone(String phone_no){
		String message = "成功";
		String code = "1000";		
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			User user = (User)dao.getOneObject("from User u where u.user_name='"+phone_no+"' and u.role_id=9");
			if(null==user){
				User u = new User();
				u.setRole_id(9);
				u.setUser_name(phone_no);
				u.setPassword(phone_no);
				dao.doSaveObject(u);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 发送验证码
	 * @param phone_no
	 * @return
	 */
	@RequestMapping(value="/userservice/sendCode",method=RequestMethod.POST)
	public @ResponseBody Object sentValidCode(String phone_no){
		String message = "成功";
		String code = "1000";		
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			Date date = new Date();
			int num = (int)(Math.random()*(9999-1000+1))+1000;
			AA10 a = new AA10();
			a.setCode(num+"");
			a.setPhone(phone_no);
			a.setSend_time(new Timestamp(date.getTime()));
			dao.doSaveObject(a);
			SmsUtils.singleSend(phone_no,num+"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 校验验证码
	 * @param phone
	 * @param valid_code
	 * @return
	 */
	@RequestMapping(value="/userservice/checkCode",method=RequestMethod.POST)
	public @ResponseBody Object checkValidCode(String phone,String valid_code){
		String message = "成功";
		String code = "1002";		
		Map<String,Object> map = new HashMap<String,Object>();
		
		AA10 a = (AA10)dao.getOneObject("from AA10 a where a.phone='"+phone+"' and code='"+valid_code+"' order by a.send_time desc");
		if(null!=a){
			Date date = new Date();
			long ms = date.getTime()-a.getSend_time().getTime();
			long min  = ms/(1000*60);
			if(min<5){
				code = "1000";
			}
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	
}
