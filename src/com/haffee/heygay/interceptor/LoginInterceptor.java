package com.haffee.heygay.interceptor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.User;

import net.sf.json.JSONObject;

public class LoginInterceptor implements HandlerInterceptor{
	
	@Autowired
	private IUserDao dao;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("进入拦截器");
		String login_key = request.getHeader("key");
		String id = request.getHeader("id");
		System.out.println("login_key:"+login_key);
		if(StringUtils.isEmpty(login_key)){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("CODE", "1003");
			map.put("MESSAGE", "请登录");
			JSONObject json = JSONObject.fromObject(map);
			response.setContentType("text/html;charset=utf-8"); 
			try {
				response.getWriter().write(json.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}else{
			User u = (User)dao.getOneObject("from User u where u.login_key='"+login_key+"' and u.business_id="+id );
			if(null==u){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("CODE", "1003");
				map.put("MESSAGE", "请登录");
				JSONObject json = JSONObject.fromObject(map);
				response.setContentType("text/html;charset=utf-8"); 
				try {
					response.getWriter().write(json.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}else{
				//判断上次请求时间到现在是否有30分钟
				Date date = new Date();
				long ms = date.getTime()-u.getKey_update_time().getTime();
				long min  = ms/(1000*60);
				if(min>30){
					u.setLogin_key("");
					dao.doUpdateObject(u);
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("CODE", "1003");
					map.put("MESSAGE", "请求超时请登录");
					JSONObject json = JSONObject.fromObject(map);
					response.setContentType("text/html;charset=utf-8"); 
					try {
						response.getWriter().write(json.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}else{
					//更新请求时间
					u.setKey_update_time(new Timestamp(date.getTime()));
					dao.doUpdateObject(u);
					return true;
				}
				
				
			}
		}
	}

}
