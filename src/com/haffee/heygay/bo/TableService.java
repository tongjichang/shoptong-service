package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.OrderInfo;
import com.haffee.heygay.po.OrderInfoAdd;
import com.haffee.heygay.po.Shop;
import com.haffee.heygay.po.ShoppingCart;
import com.haffee.heygay.po.ShoppingCartAdd;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.po.Waiter;
import com.haffee.heygay.util.QRCodeUtil;

/**
 * 餐桌管理 适用于店铺管理员、服务员、个人用户
 * 
 * @author jacktong
 * @date 2017年7月4日
 *
 */
@Controller
public class TableService {

	@Autowired
	private IUserDao dao;

	/**
	 * 展示桌台信息
	 * 
	 * @param response
	 * @param shopid
	 * @return
	 */
	@RequestMapping(value = "/tableservice/getTableInfo", method = RequestMethod.POST)
	@ResponseBody
	public Object selectTableInfo(HttpServletResponse response, HttpServletRequest request, String shopid) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> area_list = dao.getAllObject("from Area a where a.shop_id=" + shopid);
			if (null == area_list || area_list.size() == 0) {
				code = "1001";
				message = "查不到数据";
			} else {
				for (int i = 0; i < area_list.size(); i++) {
					Area a = (Area) area_list.get(i);
					List<Object> list = dao.getAllObject("from TableInfo t where t.area_id = " + a.getArea_id());
					for (int j = 0; j < list.size(); j++) {
						TableInfo t = (TableInfo) list.get(j);
						// 1.查询订单
						// 2.查询服务员信息
						// 3.查询预定信息
						OrderInfo order_info = (OrderInfo) dao
								.getOneObject("from OrderInfo o where o.status in (-1,0) and o.shop_id=" + shopid
										+ " and o.table_id=" + t.getTable_id());
						t.setOrder_info(order_info);
						if (null != order_info) {
							Waiter w = (Waiter) dao
									.getOneObject("from Waiter w where w.waiter_id=" + order_info.getWaiter_id());
							t.setWaiter(w);
							// 关联菜品
							ShoppingCart s = (ShoppingCart) dao.getOneObject(
									"from ShoppingCart s where s.cart_id=" + order_info.getShopping_cart_id());
							if (null != s) {
								t.setCart(s);
							}
						}
						List<Object> book_list = dao
								.getAllObject("from BookInfo b where b.table_id=" + t.getTable_id());
						t.setBook_list(book_list);
					}
					a.setTable_list(list);
				}
				map.put("DATA", area_list);
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
	 * 新增区域
	 * 
	 * @param response
	 * @param request
	 * @param area
	 * @return
	 */
	@RequestMapping(value = "/tableservice/addArea", method = RequestMethod.POST)
	@ResponseBody
	public Object doSaveArea(HttpServletResponse response, HttpServletRequest request, Area area) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Date date = new Date();
			area.setCreate_time(new Timestamp(date.getTime()));
			boolean isSuccess = dao.doSaveObject(area);
			if (!isSuccess) {
				code = "1002";
				message = "保存失败，请稍后再试！";
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
	 * 删除分区
	 * 
	 * @param area
	 * @return
	 */
	@RequestMapping(value = "/tableservice/deleteArea", method = RequestMethod.POST)
	@ResponseBody
	public Object doDeleteArea(Area area) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> table_list = dao.getAllObject("from TableInfo t where t.area_id=" + area.getArea_id());
			if (null == table_list || table_list.size() == 0) {
				boolean isSuccess = dao.doDeleteObject(area);
				if (!isSuccess) {
					code = "1002";
					message = "删除失败，请稍后再试！";
				}
			} else {
				code = "1002";
				message = "此分区下面有桌台信息，请先将桌台删除！";
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
	 * 查询一个分区
	 * 
	 * @param area_id
	 * @return
	 */
	@RequestMapping(value = "/tableservice/getOneArea/{area_id}", method = RequestMethod.GET)
	@ResponseBody
	public Object selectOneArea(@PathVariable("area_id") String area_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Object o = dao.getOneObject("from Area a where a.area_id=" + area_id);
			if (null == o) {
				code = "1001";
				message = "查无数据";
			} else {
				map.put("DATA", o);
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
	 * 更新分区名称
	 * 
	 * @param area
	 * @return
	 */
	@RequestMapping(value = "/tableservice/updateArea", method = RequestMethod.POST)
	@ResponseBody
	public Object doUpdateArea(Area area) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + area.getArea_id());
			if (a != null) {
				a.setArea_name(area.getArea_name());
				dao.doUpdateObject(a);
			} else {
				code = "1002";
				message = "未查到要更新数据";
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
	 * 新增桌台
	 * 
	 * @param table
	 * @return
	 */
	@RequestMapping(value = "/tableservice/addTable", method = RequestMethod.POST)
	@ResponseBody
	public Object doAddTable(TableInfo table, HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Date date = new Date();
			table.setCreate_time(new Timestamp(date.getTime()));
			dao.doSaveObject(table);

			// 生成二维码
			TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.area_id=" + table.getArea_id()
					+ "and t.table_name='" + table.getTable_name() + "' and shop_id=" + table.getShop_id());
			Shop s = (Shop) dao.getOneObject("from Shop s where shop_id=" + t.getShop_id());
			String logo = "";
			if (null != s) {
				logo = s.getIcon();
			}

			String path = request.getSession().getServletContext().getRealPath("shop_" + table.getShop_id());
			if(null!=logo){
				logo = path+logo.replace("/heygay/shop_"+table.getShop_id(), "");
			}
			QRCodeUtil.encode(
					"http://www.heyguy.cn:30000/users?shop_id=" + t.getShop_id() + "&table_id=" + t.getTable_id()+"&table_name="+t.getTable_name(), logo,
					path, true, "qrcode_" + t.getTable_id());


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
	 * 删除桌台
	 * 
	 * @param table
	 * @return
	 */
	@RequestMapping(value = "/tableservice/deleteTable", method = RequestMethod.POST)
	@ResponseBody
	public Object doDeleteTable(TableInfo table) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getAllObject("from OrderInfo o where o.table_id="+table.getTable_id());
			if(null!=list){
				for (Object object : list) {
					dao.doDeleteObject(object);
				}
			}
			// 校验状态
			TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + table.getTable_id());
			if (null == t) {
				code = "1001";
				message = "未查到数据";
			} else {
				if (t.getTable_status() == 0) {
					dao.doDeleteObject(table);
				} else {
					code = "1002";
					message = "桌台未处于空闲状态，不能删除";
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

	/**
	 * 查询一个桌台
	 * 
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/tableservice/getOneTable/{table_id}", method = RequestMethod.GET)
	@ResponseBody
	public Object selectOneTable(@PathVariable("table_id") String table_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + table_id);
			if (null == t) {
				code = "1001";
				message = "未查到数据";
			} else {
				// 关联未处理订单
				OrderInfo o = (OrderInfo) dao
						.getOneObject("from OrderInfo o where o.table_id = " + t.getTable_id() + " and o.status=0");
				if (null != o) {
					t.setOrder_info(o);
					// 关联服务员
					Waiter w = (Waiter) dao.getOneObject("from Waiter w where w.waiter_id = " + o.getWaiter_id());
					t.setWaiter(w);
					// 关联菜品
					ShoppingCart s = (ShoppingCart) dao
							.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
					if (null != s) {
						t.setCart(s);
					}
				}
				List<Object> book_list = dao
						.getAllObject("from BookInfo b where b.table_id=" + t.getTable_id() + "and b.status=1");
				if (book_list != null) {
					t.setBook_list(book_list);
				}
				map.put("DATA", t);
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
	 * 修改桌台信息
	 * 
	 * @param table
	 * @return
	 */
	@RequestMapping(value = "/tableservice/updateTable", method = RequestMethod.POST)
	@ResponseBody
	public Object doUpdateTable(TableInfo table) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + table.getTable_id());
			if (null != t) {
				if (StringUtils.isNotEmpty(table.getTable_name())) {
					t.setTable_name(table.getTable_name());
				}
				if (table.getPeople_count() != 0) {
					t.setPeople_count(table.getPeople_count());
				}
				dao.doUpdateObject(t);
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
	 * 查询所有分区信息
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/tableservice/getArea", method = RequestMethod.POST)
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
	 * 换桌
	 * 
	 * @param curr_table_id
	 * @param new_table_id
	 * @return
	 */
	@RequestMapping(value = "/tableservice/changeTable", method = RequestMethod.POST)
	public @ResponseBody Object doChangeTable(String curr_table_id, String new_table_id) {
		// 1.查询当前桌台未结账订单，修改桌台，包括加菜
		// 2.查询当前订单关联购物车，修改桌台，包括加菜
		// 3.修改桌台状态
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(curr_table_id) || StringUtils.isEmpty(new_table_id)) {
				code = "1002";
				message = "参数异常";
			} else {
				TableInfo table = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + curr_table_id);
				TableInfo new_table = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + new_table_id);
				if(null!=new_table&&new_table.getTable_status()!=0){
					code = "1001";
					message = "新桌台未查询到数据，或新桌台不处于空闲状态，不能换到此桌";
				}else{
					if (null != table) {
						OrderInfo order = (OrderInfo) dao.getOneObject(
								"from OrderInfo o where o.table_id=" + curr_table_id + " and o.status in (-1,0)");
						if (null != order) {
							order.setTable_id(Integer.valueOf(new_table_id));
							dao.doUpdateObject(order);
							if (order.getStatus() == 0) {
								OrderInfoAdd order_add = (OrderInfoAdd) dao
										.getOneObject("from OrderInfoAdd o where o.table_id=" + curr_table_id
												+ " and o.status = 0");
								if(null!=order_add){
									order_add.setTable_id(Integer.valueOf(new_table_id));
									dao.doUpdateObject(order_add);
								}
								ShoppingCartAdd cart_add = (ShoppingCartAdd)dao.getOneObject("from ShoppingCartAdd s where s.table_id="+curr_table_id);
								if(null!=cart_add){
									cart_add.setTable_id(Integer.valueOf(new_table_id));
									dao.doUpdateObject(cart_add);
								}
							}
						}
						ShoppingCart cart = (ShoppingCart)dao.getOneObject("from ShoppingCart s where s.table_id="+curr_table_id+" and s.status in (0,1)");
						if(null!=cart){
							cart.setTable_id(Integer.valueOf(new_table_id));
							dao.doUpdateObject(cart);
						}
						table.setTable_status(0);
						dao.doUpdateObject(table);
						new_table.setTable_status(1);
						dao.doUpdateObject(new_table);
					} else {
						code = "1001";
						message = "未查询到当前桌台信息";
					}
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
	
	/**
	 * 分页查询桌台
	 * @param shop_id
	 * @param page_no
	 * @return
	 */
	@RequestMapping(value = "/tableservice/selectTableByPage", method = RequestMethod.POST)
	public @ResponseBody Object selectOnePageTable(String shop_id,int page_no){
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getOnePageResult(10, page_no, "from TableInfo t where t.shop_id=" + shop_id);
			List<Object> area_list = dao.getAllObject("from Area");
			if(null!=list){
				for (Object object : list) {
					TableInfo t = (TableInfo)object;
					for (Object object2 : area_list) {
						Area a = (Area)object2;
						if(t.getArea_id()==a.getArea_id()){
							t.setArea_name(a.getArea_name());
						}
					}
				}
			}
			
			int total = dao.getAllNumber("from TableInfo t where t.shop_id=" + shop_id);
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
	

}
