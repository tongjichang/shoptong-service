package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.Goods;
import com.haffee.heygay.po.OrderInfo;
import com.haffee.heygay.po.OrderInfoAdd;
import com.haffee.heygay.po.ShopPrinter;
import com.haffee.heygay.po.ShoppingCart;
import com.haffee.heygay.po.ShoppingCartAdd;
import com.haffee.heygay.po.ShoppingCartGoods;
import com.haffee.heygay.po.ShoppingCartGoodsAdd;
import com.haffee.heygay.po.ShoppingCartGoodsBack;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.util.Api_java_demo;

/**
 * 购物车管理 适用于个人用户、服务员
 * 
 * @author jacktong
 *
 */
@Controller
public class ShoppingCartService {

	@Autowired
	private IUserDao dao;

	/**
	 * 添加商品进购物车
	 * 
	 * @param good
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/cart/addCart", method = RequestMethod.POST)
	@ResponseBody
	public Object doAddShoppingCart(String good_id, String cart_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		// 1.查询购物车是否存在
		// 2.查询菜品信息
		// 3.增加菜品进购物车
		try {
			if (StringUtils.isEmpty(good_id)) {
				code = "1002";
				message = "参数异常";
			} else {
				Goods good = (Goods) dao.getOneObject("from Goods g where g.good_id=" + good_id);
				if (null == good) {
					code = "1001";
					message = "未查到商品信息";
				} else {
					if (StringUtils.isEmpty(cart_id)) {
						// 新建
						String uuid = UUID.randomUUID().toString().replaceAll("-", "");
						ShoppingCart sc = new ShoppingCart();
						sc.setCart_id(uuid);
						sc.setTotal_num(1);
						sc.setTotal_price(good.getPre_price() * good.getDiscount());
						Date date = new Date();
						sc.setCreate_time(new Timestamp(date.getTime()));
						sc.setStatus(0); // 默认未提交
						dao.doSaveObject(sc);
						// 添加菜品
						// 组装购物车商品
						ShoppingCartGoods sc_good = new ShoppingCartGoods();
						sc_good.setGood_id(good.getGood_id());
						sc_good.setGood_name(good.getGoods_name());
						sc_good.setGood_num(1);
						sc_good.setGood_price(good.getPre_price() * good.getDiscount());
						sc_good.setGood_total_price(good.getPre_price() * good.getDiscount());
						sc_good.setCart_id(uuid);
						//sc_good.setPre_price(good.getPre_price());
						//sc_good.setDiscount(good.getDiscount());
						sc_good.setImg_url(good.getImg_url());
						dao.doSaveObject(sc_good);
						// 将购物车商品添加到商品集合
						// list.add(sc_good);
						// 将商品集合添加到购物车中
						// sc.setGoods_set(list);
						// 新建购物车
						map.put("CART_ID", uuid); //将购物车ID返回给客户端

					} else {
						ShoppingCart cart = (ShoppingCart)dao.getOneObject("from ShoppingCart sc where sc.cart_id='"+cart_id+"' and sc.status=0");
						if(null!=cart){
							List<Object> goods_set = dao
									.getAllObject("from ShoppingCartGoods scg where scg.cart_id='" + cart.getCart_id()+"'");
							boolean is_has = false;
							for (Object object : goods_set) {
								ShoppingCartGoods sc_good = (ShoppingCartGoods) object;
								if (sc_good.getGood_id() == good.getGood_id()) {
									// 已经存在商品
									sc_good.setGood_num(sc_good.getGood_num() + 1); // 商品数量加1
									// 总价格
									sc_good.setGood_total_price(
											sc_good.getGood_total_price() + sc_good.getGood_price());
									is_has = true;
									dao.doUpdateObject(object);
									break;
								}
							}
							if (!is_has) { // 购物车中没有此商品
								// 组装购物车商品
								ShoppingCartGoods sc_good = new ShoppingCartGoods();
								sc_good.setGood_id(good.getGood_id());
								sc_good.setGood_name(good.getGoods_name());
								sc_good.setGood_num(1);
								sc_good.setGood_price(good.getPre_price() * good.getDiscount());
								sc_good.setGood_total_price(good.getPre_price() * good.getDiscount());
								sc_good.setCart_id(cart.getCart_id());
								sc_good.setImg_url(good.getImg_url());
								dao.doSaveObject(sc_good);
							}
							cart.setTotal_num(cart.getTotal_num() + 1);
							cart.setTotal_price(cart.getTotal_price() + good.getPre_price());
							dao.doUpdateObject(cart);
						}
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
	 * 清空购物车
	 * 
	 * @param cart_id
	 * @return
	 */
	@RequestMapping(value = "/cart/deleteCart", method = RequestMethod.POST)
	@ResponseBody
	public Object doDeleteShoppingCart(ShoppingCart cart) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ShoppingCart sc = (ShoppingCart) dao
					.getOneObject("from ShoppingCart s where s.cart_id='" + cart.getCart_id() + "' and s.status=0");
			if (null != sc) {
				List<Object> list = dao.getAllObject("from ShoppingCartGoods scg where scg.cart_id='" + sc.getCart_id()+"'");
				for (Object object : list) {
					if (null != object) {
						dao.doDeleteObject(object);
					}
				}
				dao.doDeleteObject(sc);
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
	 * 查询购物车
	 * 
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/cart/getCart", method = RequestMethod.POST)
	@ResponseBody
	public Object selectShoppingCart(String cart_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ShoppingCart sc = (ShoppingCart) dao.getOneObject(
					"from ShoppingCart s where s.cart_id='" + cart_id+"' and s.status=0");
			if (sc != null) {
				List<Object> list = dao.getAllObject("from ShoppingCartGoods scg where scg.cart_id='" + sc.getCart_id()+"'");
				sc.setGoods_set(list);
				map.put("DATA", sc);
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
	 * 购物车中移除商品
	 * 
	 * @param cart_id
	 * @param good_id
	 * @return
	 */
	@RequestMapping(value = "/cart/removeCart", method = RequestMethod.POST)
	@ResponseBody
	public Object doUpdateShoppingCart(String cart_id, String good_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ShoppingCart cart = (ShoppingCart) dao
					.getOneObject("from ShoppingCart s where s.cart_id='" + cart_id + "' and s.status=0");
			if (null != cart) {
				List<Object> goods_set = dao
						.getAllObject("from ShoppingCartGoods scg where scg.cart_id='" + cart.getCart_id()+"'");
				for (Object object : goods_set) {
					ShoppingCartGoods sc_good = (ShoppingCartGoods) object;
					if (sc_good.getGood_id() == Integer.valueOf(good_id)) {
						if (sc_good.getGood_num() > 1) {
							sc_good.setGood_num(sc_good.getGood_num() - 1);
							sc_good.setGood_total_price(sc_good.getGood_total_price() - sc_good.getGood_price());
							dao.doUpdateObject(object);
						} else {
							dao.doDeleteObject(object);
						}
						cart.setTotal_num(cart.getTotal_num() - 1);
						cart.setTotal_price(cart.getTotal_price() - sc_good.getGood_price());
						if (cart.getTotal_num() > 0) {
							dao.doUpdateObject(cart);
						} else {
							dao.doDeleteObject(cart);
						}
						break;
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
	 * 服务员修改是否上菜
	 * 
	 * @param cart_goods_id
	 * @param if_up
	 * @return
	 */
	@RequestMapping(value = "/cart/updateGoodsIfUp", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateGoodsIfUp(String cart_goods_id, String if_up) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(cart_goods_id)) {
			code = "1002";
			message = "参数异常";
		} else {
			ShoppingCartGoods scg = (ShoppingCartGoods) dao
					.getOneObject("from ShoppingCartGoods scg where scg.cart_good_id=" + cart_goods_id);
			if (null != scg) {
				if (StringUtils.isEmpty(if_up)) {
					scg.setIf_up(1);// 已上菜
				} else {
					scg.setIf_up(0);// 未上菜
				}
				dao.doUpdateObject(scg);
			} else {
				code = "1001";
				message = "未查询到菜品数据";
			}
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}

	/**
	 * 修改菜品价格
	 * 
	 * @param cart_goods_id
	 * @param price
	 * @return
	 */
	@RequestMapping(value = "/cart/updateGoodsPrice", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateGoodsPrice(String cart_goods_id, float price) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(cart_goods_id)) {
			code = "1002";
			message = "参数异常";
		} else {
			try {
				ShoppingCartGoods scg = (ShoppingCartGoods) dao
						.getOneObject("from ShoppingCartGoods scg where scg.cart_good_id=" + cart_goods_id);
				if (null != scg) {
					float differ = price - scg.getGood_total_price(); // 差额
					scg.setGood_total_price(scg.getGood_total_price() + differ);
					dao.doUpdateObject(scg);
					ShoppingCart cart = (ShoppingCart) dao
							.getOneObject("from ShoppingCart sc where sc.cart_id=" + scg.getCart_id());
					if (null != cart) {
						cart.setTotal_price(cart.getTotal_price() + differ);
						dao.doUpdateObject(cart);
						OrderInfo order = (OrderInfo) dao
								.getOneObject("from OrderInfo o where o.shopping_cart_id=" + cart.getCart_id());
						if(null!=order){
							order.setPayment(order.getPayment() + differ);
							order.setReal_payment(order.getPayment() * order.getDiscount() - order.getMinus_money());
							dao.doUpdateObject(order);
						}
					}
				} else {
					ShoppingCartGoodsAdd scg_add = (ShoppingCartGoodsAdd) dao
						.getOneObject("from ShoppingCartGoodsAdd scg where scg.cart_good_id=" + cart_goods_id);
					if(null!=scg_add){
						float differ = price - scg_add.getGood_total_price(); // 差额
						scg_add.setGood_total_price(scg_add.getGood_total_price() + differ);
						dao.doUpdateObject(scg_add);
						ShoppingCartAdd cart_add = (ShoppingCartAdd) dao
								.getOneObject("from ShoppingCartAdd sc where sc.cart_id=" + scg_add.getCart_id());
						if (null != cart_add) {
							cart_add.setTotal_price(cart_add.getTotal_price() + differ);
							dao.doUpdateObject(cart_add);
							OrderInfoAdd order_add = (OrderInfoAdd) dao
									.getOneObject("from OrderInfoAdd o where o.shopping_cart_id=" + cart_add.getCart_id());
							if(null!=order_add){
								order_add.setPayment(order_add.getPayment() + differ);
								order_add.setReal_payment(order_add.getPayment() * order_add.getDiscount() - order_add.getMinus_money());
								dao.doUpdateObject(order_add);
							}
						}
					}else{
						code = "1001";
						message = "未查询到数据";
					}
				}
			} catch (Exception e) {
				code = "1002";
				message = "系统异常";
				e.printStackTrace();
			}
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}

	/**
	 * 退菜
	 * 
	 * @param cart_goods_id
	 * @param num
	 * @param price
	 * @return
	 */
	@RequestMapping(value = "/cart/returnGoods", method = RequestMethod.POST)
	public @ResponseBody Object doBackGoods(String cart_goods_id, int num, float price) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		// 1.查询菜品
		// 2.生成退菜菜品
		// 3.更新购物车价钱、更新订单
		try {
			if (StringUtils.isEmpty(cart_goods_id)) {
				code = "1002";
				message = "参数异常";
			} else {
				ShoppingCartGoods scg = (ShoppingCartGoods) dao
						.getOneObject("from ShoppingCartGoods scg where scg.cart_good_id=" + cart_goods_id);
				if (null != scg) {
					if(num>scg.getGood_num()){
						code = "1002";
						message = "退菜数量超出点餐数量，请重新输入";
					}else if(price>scg.getGood_price()){
						code = "1002";
						message = "退菜单价超过菜品原价，请重新输入";
					}else{
						ShoppingCartGoodsBack back = new ShoppingCartGoodsBack();
						//back.setCart_id(scg.getCart_id());
						back.setFrom(scg.getFrom());
						back.setDiscount(scg.getDiscount());
						back.setGood_id(scg.getGood_id());
						back.setGood_name("退**" + scg.getGood_name());
						back.setGood_num(num);
						back.setGood_price(scg.getGood_price());
						back.setGood_total_price(0 - price*num);
						back.setImg_url(scg.getImg_url());
						back.setPre_price(scg.getPre_price());
						dao.doSaveObject(back);
						// 更新购物车
						ShoppingCart cart = (ShoppingCart) dao
								.getOneObject("from ShoppingCart sc where sc.cart_id=" + scg.getCart_id());
						if(null!=cart){
							cart.setTotal_num(cart.getTotal_num()-num>0?cart.getTotal_num()-num:0);
							cart.setTotal_price(cart.getTotal_price()-price);
							dao.doUpdateObject(cart);
							OrderInfo order = (OrderInfo)dao.getOneObject("from OrderInfo o where o.shopping_cart_id="+cart.getCart_id());
							if(null!=order){
								order.setGoods_num(order.getGoods_num()-num>0?order.getGoods_num()-num:0);
								order.setPayment(order.getPayment()-num*price);
								order.setDiscount_payment(order.getPayment()*order.getDiscount());
								order.setReal_payment(order.getDiscount_payment()-order.getMinus_money());
								dao.doUpdateObject(order);
							}
						}
						Goods goods = (Goods)dao.getOneObject("from Goods g where g.good_id="+scg.getGood_id());
						if(goods.getIf_print()==1){
							String[] printer_array = goods.getPrint_str().split("\\,");
							if(printer_array.length>0){
								for (String printer_no : printer_array) {
									if(StringUtils.isEmpty(printer_no)){
										continue;
									}
									ShopPrinter printer = (ShopPrinter)dao.getOneObject("from ShopPrinter sp where sp.printer_no="+printer_no);
									if(null!=printer&&printer.getType_print().equals("1")){
										TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+cart.getTable_id());
										Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
										Api_java_demo printer_thread = new Api_java_demo();
										printer_thread.setFlag("4");
										printer_thread.setPrint_no(printer.getPrinter_no());
										printer_thread.setBack_cart_goods(back);
										printer_thread.setArea(area);
										printer_thread.setTable(table);
										Thread thread = new Thread(printer_thread);
										thread.start();
									}
								}
							}
						}
					}
				} else {
					code = "1002";
					message = "未查到菜品信息";
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
	 * 服务员确认订单之前，减菜
	 * @param goods_id
	 * @param num
	 * @return
	 */
	@RequestMapping(value="/cart/removeGoods",method=RequestMethod.POST)
	public @ResponseBody Object doRemoveGoods(String order_num,String goods_id,int num){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//1.确认购物车是否存在，及状态是否未确认
			//2.确认商品及数量是否合法
			if(StringUtils.isEmpty(order_num)||StringUtils.isEmpty(goods_id)||num==0){
				code = "1002";
				message = "参数异常";
			}else{
				OrderInfo order = (OrderInfo)dao.getOneObject("from OrderInfo o where o.order_num="+order_num);
				OrderInfoAdd order_add = (OrderInfoAdd)dao.getOneObject("from OrderInfoAdd o where o.order_num="+order_num);
				if(null!=order){
					if(order.getStatus()==-1){
						ShoppingCart cart = (ShoppingCart)dao.getOneObject("from ShoppingCart sc where sc.cart_id="+order.getShopping_cart_id());
						if(null!=cart){
							List<Object> goods_list = dao.getAllObject("from ShoppingCartGoods s where s.cart_id="+cart.getCart_id()); 
							if(null!=goods_list&&goods_list.size()>0){
								boolean flag = false;
								for (Object object : goods_list) {
									ShoppingCartGoods g = (ShoppingCartGoods)object;
									if(g.getGood_id()==Integer.valueOf(goods_id)){
										//有此菜品
										if(g.getGood_num()>num){
											g.setGood_num(g.getGood_num()-num);
											g.setGood_total_price(g.getGood_total_price()-g.getGood_price()*g.getDiscount()*num);
											cart.setTotal_num(cart.getTotal_num()-num);
											cart.setTotal_price(cart.getTotal_price()-g.getPre_price()*num);
											order.setGoods_num(order.getGoods_num()-num);
											order.setPayment(order.getPayment()-g.getPre_price()*num);
											order.setDiscount_payment(order.getPayment()*order.getDiscount());
											order.setReal_payment(order.getDiscount_payment()-order.getMinus_money());
											dao.doUpdateObject(g);
										}else{
											cart.setTotal_num(cart.getTotal_num()-g.getGood_num());
											cart.setTotal_price(cart.getTotal_price()-g.getGood_total_price());
											order.setGoods_num(order.getGoods_num()-g.getGood_num());
											order.setPayment(order.getPayment()-g.getGood_total_price());
											order.setDiscount_payment(order.getPayment()*order.getDiscount());
											order.setReal_payment(order.getDiscount_payment()-order.getMinus_money());
											dao.doDeleteObject(g);
										}
										flag = true;
										break;
									}
								}
								if(!flag){
									code = "1002";
									message = "此订单购物车中无此商品，请确认";
								}
							}else{
								code = "1002";
								message = "未查询到此订单中购物车商品";
							}
							dao.doUpdateObject(cart);
						}else{
							code = "1002";
							message = "此订单未查询到购物车信息，请刷新确认";
						}
					}else if(order.getStatus()==0){
						code = "1002";
						message = "此订单已被接单，请使用退菜功能";
					}else{
						code = "1002";
						message = "此订单已结账或者取消";
					}
					dao.doUpdateObject(order);
				}else if(null!=order_add){
					ShoppingCartAdd cart_add = (ShoppingCartAdd)dao.getOneObject("from ShoppingCartAdd sc where sc.cart_id="+order_add.getShopping_cart_id());
					if(null!=cart_add){
						List<Object> goods_list_add = dao.getAllObject("from ShoppingCartGoodsAdd s where s.cart_good_id="+cart_add.getCart_id()); 
						if(null!=goods_list_add&&goods_list_add.size()>0){
							boolean flag = false;
							for (Object object : goods_list_add) {
								ShoppingCartGoodsAdd g_add = (ShoppingCartGoodsAdd)object;
								if(g_add.getGood_id()==Integer.valueOf(goods_id)){
									//有此菜品
									if(g_add.getGood_num()>num){
										g_add.setGood_num(g_add.getGood_num()-num);
										g_add.setGood_total_price(g_add.getGood_total_price()-g_add.getGood_price()*g_add.getDiscount()*num);
										order_add.setGoods_num(order_add.getGoods_num()-num);
										order_add.setPayment(order_add.getPayment()-g_add.getPre_price()*num);
										order_add.setDiscount_payment(order_add.getPayment()*order_add.getDiscount());
										order_add.setReal_payment(order_add.getDiscount_payment()-order_add.getMinus_money());
										dao.doUpdateObject(g_add);
									}else{
										cart_add.setTotal_num(cart_add.getTotal_num()-g_add.getGood_num());
										cart_add.setTotal_price(cart_add.getTotal_price()-g_add.getGood_total_price());
										order_add.setGoods_num(order_add.getGoods_num()-g_add.getGood_num());
										order_add.setPayment(order_add.getPayment()-g_add.getGood_total_price());
										order_add.setDiscount_payment(order_add.getPayment()*order_add.getDiscount());
										order_add.setReal_payment(order_add.getDiscount_payment()-order_add.getMinus_money());
										dao.doDeleteObject(g_add);
									}
									flag = true;
									break;
								}
							}
							if(!flag){
								code = "1002";
								message = "此订单购物车中无此商品，请确认";
							}
						}else{
							code = "1002";
							message = "未查询到此订单中购物车商品";
						}
						dao.doUpdateObject(cart_add);
					}else{
						code = "1002";
						message = "此订单未查询到购物车信息，请刷新确认";
					}
					dao.doUpdateObject(order_add);
				}else{
					code = "1001";
					message = "未查询到订单数据";
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
