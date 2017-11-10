package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.Service;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.po.Waiter;
import com.haffee.heygay.util.JPush;

/**
 * 服务管理 适用于 所有用户
 * 
 * @author jacktong
 * @date 2017年7月4日
 *
 */
@Controller
public class ServiceManageService {

	@Autowired
	private IUserDao dao;

	/**
	 * 查询服务 --店铺管理
	 * 
	 * @param shop_id
	 * @param page_no
	 * @return
	 */
	@RequestMapping(value = "/service/selectAllService", method = RequestMethod.POST)
	public @ResponseBody Object selectOnePageService(int shop_id, int page_no) {
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getOnePageResult(10, page_no,
					"from Service s where s.shop_id=" + shop_id + " order by s.create_time desc");
			if (null != list && list.size() > 0) {
				for (Object object : list) {
					Service service = (Service) object;
					Waiter w = (Waiter) dao.getOneObject("from Waiter w where w.waiter_id=" + service.getWaiter_id());
					service.setWaiter(w);
					TableInfo t = (TableInfo) dao
							.getOneObject("from TableInfo t where t.table_id=" + service.getTable_id());
					service.setTable(t);
					Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + t.getArea_id());
					service.setArea(a);
				}
			}
			int total = dao.getAllNumber("from Service s where s.shop_id=" + shop_id);
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
	 * 处理服务
	 * 
	 * @param service_id
	 * @param waiter_id
	 * @return
	 */
	@RequestMapping(value = "/service/updateService", method = RequestMethod.POST)
	public @ResponseBody Object updateService(int service_id, int waiter_id, int status) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Service s = (Service) dao
					.getOneObject("from Service s where s.service_id=" + service_id + " and s.status=0");
			if (null != s) {
				s.setWaiter_id(waiter_id);
				s.setStatus(status);
				Date date = new Date();
				s.setReceive_time(new Timestamp(date.getTime())); //add by jacktong 2017-9-23
				dao.doUpdateObject(s);
			} else {
				code = "1001";
				message = "没有要处理的服务";
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
	 * 查询服务列表--分页 服务员
	 * 
	 * @param waiter_id
	 * @param page_no
	 * @return
	 */
	@RequestMapping(value = "/service/selectServiceListWaiter", method = RequestMethod.POST)
	public @ResponseBody Object selectServiceForWaiter(String waiter_id, int page_no,String shop_id,String status) { //新增shop_id,status
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = null;
			int total = 0;
			if(status.equals("0")){ //如果是未处理，查询全部
				list = dao.getOnePageResult(10, page_no,
						"from Service s where s.shop_id=" + shop_id + " and s.status = "+status+" order by s.create_time desc");
				total = dao.getAllNumber("from Service s where s.shop_id=" + shop_id+" and s.status="+status);
			}else{ //否则根据waiter_id查询
				list = dao.getOnePageResult(10, page_no,
						"from Service s where s.shop_id=" + shop_id + " and s.status = "+status+" and s.waiter_id="+waiter_id+" order by s.create_time desc");
				total = dao.getAllNumber("from Service s where s.shop_id=" + shop_id+" and s.status="+status+" and s.waiter_id="+waiter_id);
			}
			if (null != list && list.size() > 0) {
				for (Object object : list) {
					Service service = (Service) object;
					if(!waiter_id.equals("0")){
						Waiter w = (Waiter) dao.getOneObject("from Waiter w where w.waiter_id=" + service.getWaiter_id());
						service.setWaiter(w);
					}
					TableInfo t = (TableInfo) dao
							.getOneObject("from TableInfo t where t.table_id=" + service.getTable_id());
					service.setTable(t);
					Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + t.getArea_id());
					service.setArea(a);
				}
			}
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
	 * 查询单个服务--服务员
	 * 
	 * @param service_id
	 * @return
	 */
	@RequestMapping(value = "/service/selectOneService", method = RequestMethod.POST)
	public @ResponseBody Object selectOneService(String service_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(service_id)) {
				code = "1002";
				message = "参数异常";
			} else {
				Service service = (Service) dao.getOneObject("from Service s where s.service_id=" + service_id);
				Waiter w = (Waiter) dao.getOneObject("from Waiter w where w.waiter_id=" + service.getWaiter_id());
				service.setWaiter(w);
				TableInfo t = (TableInfo) dao
						.getOneObject("from TableInfo t where t.table_id=" + service.getTable_id());
				service.setTable(t);
				Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + t.getArea_id());
				service.setArea(a);
				map.put("DATA", service);
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
	 * 新增服务 --顾客
	 * 
	 * @param s
	 * @return
	 */
	@RequestMapping(value = "/userservice/addservice", method = RequestMethod.POST)
	public @ResponseBody Object doAddService(Service s) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (null == s) {
				code = "1002";
				message = "参数异常";
			} else {
				
				TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + s.getTable_id());
				Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + t.getArea_id());
//				OrderInfo order = (OrderInfo) dao
//						.getOneObject("from OrderInfo o where o.table_id=" + s.getTable_id() + " and o.status = 0");
				Date date = new Date();
				s.setCreate_time(new Timestamp(date.getTime()));
				s.setStatus(0);
				//s.setWaiter_id(order.getWaiter_id());
				s.setIf_notice("0");
				dao.doSaveObject(s);
				Service service = (Service) dao.getOneObject("from Service s where s.table_id=" + s.getTable_id()
						+ " and s.service_content='" + s.getService_content() + "'");
				//if (null != order) {
					List<Object> list = dao.getAllObject("from Waiter w where w.shop_id=" + s.getShop_id());
					String notify_title = "";
					if (null != a) {
						notify_title = a.getArea_name() + "_" + t.getTable_name() + "有顾客加菜";
					}
					String message_title = a.getArea_name() + "_" + t.getTable_name() + "";
					String message_content = service.getService_content();
					String extrasparam = "{id:" + service.getService_id() + ",type:2}";
					StringBuffer buffer = new StringBuffer();
					for (Object object : list) {
						Waiter w = (Waiter) object;
						buffer.append(w.getUsername() + "_" + w.getWaiter_id());
						buffer.append(",");
					}
					String[] array = buffer.substring(0, buffer.length() - 1).split(",");
					JPush.sendToAllWithTags(notify_title, message_title, message_content, extrasparam, array);
				//}
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
