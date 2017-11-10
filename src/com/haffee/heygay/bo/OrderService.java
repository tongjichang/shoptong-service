package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.BookInfo;
import com.haffee.heygay.po.Goods;
import com.haffee.heygay.po.OrderInfo;
import com.haffee.heygay.po.OrderInfoAdd;
import com.haffee.heygay.po.Shop;
import com.haffee.heygay.po.ShopPrinter;
import com.haffee.heygay.po.ShoppingCart;
import com.haffee.heygay.po.ShoppingCartAdd;
import com.haffee.heygay.po.ShoppingCartGoods;
import com.haffee.heygay.po.ShoppingCartGoodsAdd;
import com.haffee.heygay.po.ShoppingCartGoodsBack;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.po.Waiter;
import com.haffee.heygay.util.Api_java_demo;
import com.haffee.heygay.util.JPush;

/**
 * 订单管理 适用于个人用户、服务员、店铺管理员
 * 
 * @author jacktong
 *
 */ 
@Controller
public class OrderService {

	@Autowired
	private IUserDao dao;

	/**
	 * 提交订单--服务员
	 * 
	 * @param order
	 * @return
	 */
	@RequestMapping(value = "/order/saveOrder", method = RequestMethod.POST)
	public @ResponseBody Object doSaveOrder(String cart_id, String waiter_id,OrderInfo order_form) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(cart_id) || StringUtils.isEmpty(waiter_id)) {
			code = "1002";
			message = "参数错误";
		} else {
			try {
				ShoppingCart cart = (ShoppingCart) dao.getOneObject("from ShoppingCart s where s.cart_id=" + cart_id);
				if (cart != null) {
					TableInfo t = (TableInfo) dao
							.getOneObject("from TableInfo t where t.table_id=" + cart.getTable_id());
					Date date = new Date();
					OrderInfo order = new OrderInfo();
					order.setCreate_time(new Timestamp(date.getTime()));
					order.setOrder_time(new Timestamp(date.getTime()));
					order.setPayment(cart.getTotal_price());
					order.setDiscount_payment(cart.getTotal_price());
					order.setReal_payment(cart.getTotal_price());
					order.setGoods_num(cart.getTotal_num());
					order.setStatus(0);
					order.setShop_id(cart.getShop_id());
					order.setTable_id(cart.getTable_id());
					order.setWaiter_id(Integer.valueOf(waiter_id));
					order.setOrder_num(genOrderNum());// 设置订单号
					order.setShopping_cart_id(Integer.valueOf(cart_id));
					order.setDiscount(1);
					order.setComments(order_form.getComments()); //备注BUG add by jacktong 2017-9-27
					if(order_form.getPeople_count()!=0){
						order.setPeople_count(order_form.getPeople_count());
					}
					if(order_form.getWay()!=0){
						order.setWay(order_form.getWay());
					}
					if(order_form.getGo_goods_way()!=0){
						order.setGo_goods_way(order_form.getGo_goods_way());
					}
					dao.doSaveObject(order);// 保存订单
					cart.setStatus(1);
					dao.doUpdateObject(cart);// 更新购物车状态
					t.setTable_status(1);
					dao.doUpdateObject(t);// 更新桌台状态未占用
					// 处理销量
					List<Object> list = dao
							.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + cart.getCart_id());
					if (null != list) {
						for (Object object : list) {
							ShoppingCartGoods scg = (ShoppingCartGoods) object;
							Goods good = (Goods) dao.getOneObject("from Goods t where good_id=" + scg.getGood_id());
							scg.setPrinter_no_str(good.getPrint_str());
							scg.setIf_print(good.getIf_print());
							doUpdateGoodsSalesCount(scg.getGood_id(), scg.getGood_num());
						}
					}
					// 后厨打印
					Shop shop = (Shop)dao.getOneObject("from Shop s where s.shop_id="+cart.getShop_id());
					int print_way = 1;
					if(null!=shop){
						print_way = shop.getPrint_way();
					}
					List<Object> printers = dao
							 .getAllObject("from ShopPrinter sp where sp.shop_id=" + order.getShop_id() + "and sp.type_print=1");
					TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+order.getTable_id());
					Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
					Api_java_demo printer = new Api_java_demo(cart.getShop_id() + "", list, null,
							cart.getTable_id() + "", "1",printers,table,area,order,print_way);
					Thread thread = new Thread(printer);
					thread.start();
				} else {
					// 1.查询是否有加菜购物车，否则返回异常
					// 2.将购物车内容与原来订单合并
					// 3.删除加菜购物车商品、购物车、及订单内容
					ShoppingCartAdd cart_add = (ShoppingCartAdd) dao
							.getOneObject("from ShoppingCartAdd s where s.cart_id=" + cart_id);
					if (null != cart_add) {
						List<Object> list_add = dao.getAllObject(
								"from ShoppingCartGoodsAdd scga where scga.cart_id=" + cart_add.getCart_id());
						System.out.println("*****************************listadd"+list_add.size());
						// 已结账的订单内容
						OrderInfo order = (OrderInfo) dao.getOneObject(
								"from OrderInfo o where o.table_id=" + cart_add.getTable_id() + " and o.status in (0,-1)");
						ShoppingCart cart_submit = null;
						List<Object> list = null;
						if (null != order) {
							cart_submit = (ShoppingCart) dao.getOneObject(
									"from ShoppingCart sc where sc.cart_id=" + order.getShopping_cart_id());
							if (null != cart_submit) {
								list = dao.getAllObject(
										"from ShoppingCartGoods scg where scg.cart_id=" + cart_submit.getCart_id());
								if (null != list) {
									for (Object object : list) {
										ShoppingCartGoods scg = (ShoppingCartGoods) object;
										Goods good_原始 = (Goods) dao.getOneObject("from Goods t where good_id=" + scg.getGood_id());
										scg.setPrinter_no_str(good_原始.getPrint_str());
										scg.setIf_print(good_原始.getIf_print());
									}
									// 合并购物车菜品
									for (Object object : list_add) {
										boolean is_has = false;
										ShoppingCartGoodsAdd scga = (ShoppingCartGoodsAdd) object;
										Goods good = (Goods) dao.getOneObject("from Goods t where good_id=" + scga.getGood_id());
										for (Object object2 : list) {
											ShoppingCartGoods scg = (ShoppingCartGoods) object2;
											Goods good_原始 = (Goods) dao.getOneObject("from Goods t where good_id=" + scg.getGood_id());
											if (scga.getGood_id() == scg.getGood_id()) {
												scg.setGood_num(scg.getGood_num() + scga.getGood_num()); // 合并数量
												scg.setGood_total_price(
														scg.getGood_total_price() + scga.getGood_total_price());// 合并金额
												scg.setPrinter_no_str(good_原始.getPrint_str());
												scg.setIf_print(good_原始.getIf_print());
												dao.doUpdateObject(scg);
												is_has = true;
												break;
											}
										}
										if (!is_has) { // 原来购物车中没有商品
											ShoppingCartGoods scg = new ShoppingCartGoods();
											scg.setCart_id(cart_submit.getCart_id());
											scg.setGood_id(scga.getGood_id());
											scg.setGood_name(scga.getGood_name());
											scg.setGood_price(scga.getGood_price());
											scg.setGood_num(scga.getGood_num());
											scg.setGood_total_price(scga.getGood_price());
											scg.setPre_price(scga.getPre_price());
											scg.setDiscount(scga.getDiscount());
											scg.setImg_url(scga.getImg_url());
											scg.setPrinter_no_str(good.getPrint_str());
											scg.setIf_print(good.getIf_print());
											dao.doSaveObject(scg);
											list.add(scg);
										}
										scga.setPrinter_no_str(good.getPrint_str());
										scga.setIf_print(good.getIf_print());
										doUpdateGoodsSalesCount(scga.getGood_id(), scga.getGood_num()); // 处理销量
										dao.doDeleteObject(scga);// 删除加菜购物车商品
									}

									// 合并购物车
									cart_submit.setTotal_num(cart_submit.getTotal_num() + cart_add.getTotal_num());
									cart_submit
											.setTotal_price(cart_submit.getTotal_price() + cart_add.getTotal_price());
									if(order.getStatus()==-1){
										cart_submit.setStatus(1);
									}
									dao.doUpdateObject(cart_submit);
									dao.doDeleteObject(cart_add);// 删除加菜购物车

									// 合并订单
									order.setGoods_num(order.getGoods_num() + cart_add.getTotal_num());
									order.setPayment(order.getPayment() + cart_add.getTotal_price());
									order.setDiscount_payment(order.getPayment()*order.getDiscount());
									order.setReal_payment(order.getDiscount_payment()-order.getMinus_money());
									order.setComments(order.getComments()+"  "+order_form.getComments());
									int flag = 0; //是否有未确认订单
									if(order.getStatus()==-1){
										flag = 1;
										order.setStatus(0);
										order.setWaiter_id(Integer.valueOf(waiter_id));
									}
									dao.doUpdateObject(order);
									
									Shop shop = (Shop)dao.getOneObject("from Shop s where s.shop_id="+cart_add.getShop_id());
									int print_way = 1;
									if(null!=shop){
										print_way = shop.getPrint_way();
									}
									List<Object> printers = dao
											 .getAllObject("from ShopPrinter sp where sp.shop_id=" + order.getShop_id() + "and sp.type_print=1");
									TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+order.getTable_id());
									Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
									order.setComments(order_form.getComments()); //加菜打印内容
									order.setWay(order_form.getWay());
									order.setGo_goods_way(order_form.getGo_goods_way());
									if(flag==0){
										Api_java_demo printer = new Api_java_demo(cart_add.getShop_id() + "", null,
												list_add, cart_add.getTable_id() + "", "1",printers,table,area,order,print_way);
										Thread t = new Thread(printer);
										t.start();
									}else{//将未确认一起打印
										Api_java_demo printer = new Api_java_demo(cart_add.getShop_id() + "", list,
												null, cart_add.getTable_id() + "", "1",printers,table,area,order,print_way);
										Thread t = new Thread(printer);
										t.start();
									}
									

								}
							}

						}

					} else {
						code = "1001";
						message = "未查询到购物车";
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
	 * 顾客提交订单
	 * 
	 * @param cart_id
	 * @return
	 */
	@RequestMapping(value = "/userorder/saveOrder", method = RequestMethod.POST)
	public @ResponseBody Object doSaveOrderPerson(String cart_id, OrderInfo order_form) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(cart_id)) {
			code = "1002";
			message = "参数错误";
		} else {
			try {
				ShoppingCart cart = (ShoppingCart) dao
						.getOneObject("from ShoppingCart s where s.cart_id=" + cart_id );
				if (cart != null&&cart.getStatus()==0) {
					TableInfo t = (TableInfo) dao
							.getOneObject("from TableInfo t where t.table_id=" + cart.getTable_id());
					Date date = new Date();
					OrderInfo order = new OrderInfo();
					order.setCreate_time(new Timestamp(date.getTime()));
					order.setPayment(cart.getTotal_price());
					order.setReal_payment(cart.getTotal_price());
					order.setDiscount_payment(cart.getTotal_price());
					order.setGoods_num(cart.getTotal_num());
					order.setStatus(-1);
					order.setShop_id(cart.getShop_id());
					order.setTable_id(cart.getTable_id());
					order.setWaiter_id(cart.getWaiter_id());
					order.setOrder_num(genOrderNum());// 设置订单号
					order.setShopping_cart_id(Integer.valueOf(cart_id));
					order.setWay(order_form.getWay());
					if (StringUtils.isNotEmpty(order_form.getUser_phone())) {
						order.setUser_phone(order_form.getUser_phone());
					}
					if (StringUtils.isNotEmpty(order_form.getComments())) {
						order.setComments(order_form.getComments());
					}
					order.setPeople_count(order_form.getPeople_count());
					order.setDiscount(1);
					dao.doSaveObject(order);// 保存订单
					cart.setStatus(1);
					dao.doUpdateObject(cart);// 更新购物车状态
					t.setTable_status(1);
					dao.doUpdateObject(t);// 更新桌台状态为占用
					// 处理推送
					List<Object> waiter_list = dao.getAllObject("from Waiter w where w.shop_id=" + cart.getShop_id());
					OrderInfo o = (OrderInfo) dao
							.getOneObject("from OrderInfo o where o.order_num='" + order.getOrder_num() + "'");
					Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + t.getArea_id());
					if (null != waiter_list && null != o) {
						StringBuffer buffer = new StringBuffer();
						for (Object object : waiter_list) {
							Waiter w = (Waiter) object;
							buffer.append(w.getUsername() + "_" + w.getWaiter_id());
							buffer.append(",");
						}
						if (buffer.length() == 0) {
							code = "1002";
							message = "店铺没有服务员信息";
						} else {
							String[] array = buffer.substring(0, buffer.length() - 1).split(",");
							String notify_title = "";
							if (null != a) {
								notify_title = a.getArea_name() + "_" + t.getTable_name() + "有顾客点餐";
							}
							String message_title = a.getArea_name() + "_" + t.getTable_name() + "";
							String message_content = "顾客点餐";
							String extrasparam = "{id:" + o.getOrder_num() + ",type:1}";
							JPush.sendToAllWithTags(notify_title, message_title, message_content, extrasparam, array);
						}
					}
				}else {
					// 查询是否有加菜订单
					ShoppingCartAdd cart_add = (ShoppingCartAdd) dao
							.getOneObject("from ShoppingCartAdd s where s.cart_id=" + cart_id + " and s.status=0");
					if (null != cart_add) { // 有加菜订单
						//取订单，判断服务员是否确认，没确认，合并了事
						OrderInfo order = (OrderInfo)dao.getOneObject("from OrderInfo o where o.table_id="+cart_add.getTable_id()+" and o.status=-1");
						if(null!=order){
							//合并购物车，订单
							ShoppingCartAdd sca = (ShoppingCartAdd) dao
									.getOneObject("from ShoppingCartAdd s where s.cart_id=" + cart_id + " and s.status=0");
							ShoppingCart sc = (ShoppingCart) dao
									.getOneObject("from ShoppingCart s where s.cart_id=" + order.getShopping_cart_id());
							if(null != sca && null != sc){
								List<Object> goods_add_list = dao.getAllObject(
										"from ShoppingCartGoodsAdd s where s.cart_id=" + sca.getCart_id());
								List<Object> goods_list = dao
										.getAllObject("from ShoppingCartGoods s where s.cart_id=" + sc.getCart_id());
								// 合并购物车菜品
								for (Object object : goods_add_list) {
									boolean is_has = false;
									ShoppingCartGoodsAdd scga = (ShoppingCartGoodsAdd) object;
									for (Object object2 : goods_list) {
										ShoppingCartGoods scg = (ShoppingCartGoods) object2;
										if (scga.getGood_id() == scg.getGood_id()) {
											scg.setGood_num(scg.getGood_num() + scga.getGood_num()); // 合并数量
											scg.setGood_total_price(
													scg.getGood_total_price() + scga.getGood_total_price());// 合并金额
											dao.doUpdateObject(scg);
											is_has = true;
											break;
										}
									}
									if (!is_has) { // 原来购物车中没有商品
										ShoppingCartGoods scg = new ShoppingCartGoods();
										scg.setCart_id(sc.getCart_id());
										scg.setGood_id(scga.getGood_id());
										scg.setGood_name(scga.getGood_name());
										scg.setGood_price(scga.getGood_price());
										scg.setGood_num(scga.getGood_num());
										scg.setGood_total_price(scga.getGood_price());
										scg.setPre_price(scga.getPre_price());
										scg.setDiscount(scga.getDiscount());
										scg.setImg_url(scga.getImg_url());
										dao.doSaveObject(scg);
									}
									Goods good = (Goods) dao.getOneObject("from Goods t where good_id=" + scga.getGood_id());
									scga.setPrinter_no_str(good.getPrint_str());
									doUpdateGoodsSalesCount(scga.getGood_id(),scga.getGood_num()); //处理销量
									dao.doDeleteObject(scga);// 删除加菜购物车商品
									
								}
								// 合并购物车
								sc.setTotal_num(sc.getTotal_num() + sca.getTotal_num());
								sc.setTotal_price(sc.getTotal_price() + sca.getTotal_price());
								dao.doUpdateObject(sc);
								
								order.setGoods_num(sc.getTotal_num());
								order.setPayment(order.getPayment()+sca.getTotal_price());
								dao.doDeleteObject(sca);// 删除加菜购物车
								
								dao.doUpdateObject(order);
							}
						}else{
							//判断是否有没确认的加菜订单,有合并现有
							OrderInfoAdd order_a = (OrderInfoAdd)dao.getOneObject("from OrderInfoAdd o where o.table_id="+cart_add.getTable_id()+" and o.status=-1");
							if(null!=order_a){
								ShoppingCartAdd sca_a = (ShoppingCartAdd) dao
										.getOneObject("from ShoppingCartAdd s where s.cart_id=" + order_a.getShopping_cart_id());
								if(null!=cart_add&&null!=sca_a){
									//合并两个加菜购物车及订单
									List<Object> goods_add_list = dao
											.getAllObject("from ShoppingCartGoodsAdd s where s.cart_id=" + cart_add.getCart_id());
									List<Object> goods_list = dao.getAllObject(
											"from ShoppingCartGoodsAdd s where s.cart_id=" + sca_a.getCart_id());
									// 合并购物车菜品
									for (Object object : goods_add_list) {
										boolean is_has = false;
										ShoppingCartGoodsAdd scga = (ShoppingCartGoodsAdd) object;
										for (Object object2 : goods_list) {
											ShoppingCartGoodsAdd scg = (ShoppingCartGoodsAdd) object2;
											if (scga.getGood_id() == scg.getGood_id()) {
												scg.setGood_num(scg.getGood_num() + scga.getGood_num()); // 合并数量
												scg.setGood_total_price(
														scg.getGood_total_price() + scga.getGood_total_price());// 合并金额
												dao.doUpdateObject(scg);
												is_has = true;
												break;
											}
										}
										if (!is_has) { // 原来购物车中没有商品
											ShoppingCartGoodsAdd scg = new ShoppingCartGoodsAdd();
											scg.setCart_id(sca_a.getCart_id());
											scg.setGood_id(scga.getGood_id());
											scg.setGood_name(scga.getGood_name());
											scg.setGood_price(scga.getGood_price());
											scg.setGood_num(scga.getGood_num());
											scg.setGood_total_price(scga.getGood_price());
											scg.setPre_price(scga.getPre_price());
											scg.setDiscount(scga.getDiscount());
											scg.setImg_url(scga.getImg_url());
											dao.doSaveObject(scg);
										}
										Goods good = (Goods) dao.getOneObject("from Goods t where good_id=" + scga.getGood_id());
										scga.setPrinter_no_str(good.getPrint_str());
										doUpdateGoodsSalesCount(scga.getGood_id(),scga.getGood_num()); //处理销量
										dao.doDeleteObject(scga);// 删除加菜购物车商品
									}
									sca_a.setTotal_num(sca_a.getTotal_num() + cart_add.getTotal_num());
									sca_a.setTotal_price(sca_a.getTotal_price() + cart_add.getTotal_price());
									dao.doUpdateObject(sca_a);
									
									order_a.setGoods_num(sca_a.getTotal_num());
									order_a.setPayment(order_a.getPayment()+cart_add.getTotal_price());
									dao.doDeleteObject(cart_add);// 删除加菜购物车
									
									dao.doUpdateObject(order_a);
									
								}
								
							}else{
								TableInfo t = (TableInfo) dao
										.getOneObject("from TableInfo t where t.table_id=" + cart_add.getTable_id());
								Date date = new Date();
								OrderInfoAdd order_add = new OrderInfoAdd();
								order_add.setCreate_time(new Timestamp(date.getTime()));
								order_add.setPayment(cart_add.getTotal_price());
								order_add.setReal_payment(cart_add.getTotal_price());
								order_add.setGoods_num(cart_add.getTotal_num());
								order_add.setStatus(-1);
								order_add.setShop_id(cart_add.getShop_id());
								order_add.setTable_id(cart_add.getTable_id());
								order_add.setWaiter_id(cart_add.getWaiter_id());
								order_add.setOrder_num(genOrderNum());// 设置订单号
								order_add.setShopping_cart_id(Integer.valueOf(cart_id));
								order_add.setWay(order_form.getWay());
								if (StringUtils.isNotEmpty(order_form.getUser_phone())) {
									order_add.setUser_phone(order_form.getUser_phone());
								}
								if (StringUtils.isNotEmpty(order_form.getComments())) {
									order_add.setComments(order_form.getComments());
								}
								order_add.setPeople_count(order_form.getPeople_count());
								dao.doSaveObject(order_add);// 保存订单
								cart_add.setStatus(1);
								dao.doUpdateObject(cart_add);// 更新购物车状态
								// 处理推送
								List<Object> waiter_list = dao
										.getAllObject("from Waiter w where w.shop_id=" + cart_add.getShop_id());
								OrderInfoAdd o_add = (OrderInfoAdd) dao.getOneObject(
										"from OrderInfoAdd o where o.order_num='" + order_add.getOrder_num() + "'");
								Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + t.getArea_id());
								if (null != waiter_list && null != o_add) {
									StringBuffer buffer = new StringBuffer();
									for (Object object : waiter_list) {
										Waiter w = (Waiter) object;
										buffer.append(w.getUsername() + "_" + w.getWaiter_id());
										buffer.append(",");
									}
									String[] array = buffer.substring(0, buffer.length() - 1).split(",");
									String notify_title = "";
									if (null != a) {
										notify_title = a.getArea_name() + "_" + t.getTable_name() + "有顾客加菜";
									}
									String message_title = a.getArea_name() + "_" + t.getTable_name() + "";
									String message_content = "顾客点餐";
									String extrasparam = "{id:" + o_add.getOrder_num() + ",type:1}";
									JPush.sendToAllWithTags(notify_title, message_title, message_content, extrasparam, array);
								}
							}
						}
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
	 * 生成当前日期时间+5位随机数订单号
	 * 
	 * @return
	 */
	public String genOrderNum() {
		SimpleDateFormat simpleDateFormat;
		simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String str = simpleDateFormat.format(date);
		Random random = new Random();
		int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
		return str + rannum;// 当前时间
	}

	/**
	 * 查询某一桌订单 服务员
	 * 
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/order/getOrder", method = RequestMethod.POST)
	public @ResponseBody Object selectOrder(String table_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			OrderInfo o = (OrderInfo) dao
					.getOneObject("from OrderInfo o where o.table_id=" + table_id + " and o.status in (0,-1)");
			TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where table_id=" + o.getTable_id());
			if (null != o) {
				if (null != t) {
					o.setTable(t);
					Area a = (Area) dao.getOneObject("from Area a where area_id=" + t.getArea_id());
					if (null != a) {
						o.setArea(a);
					}
				}
				ShoppingCart cart = (ShoppingCart) dao
						.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
				if (null != cart) {
					List<Object> list = dao
							.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + cart.getCart_id());
					// 退菜内容
					List<Object> back_list = dao
							.getAllObject("from ShoppingCartGoodsBack scg where scg.cart_id=" + cart.getCart_id());
					if (null != list && null != back_list) {
						for (Object object : back_list) {
							list.add(object);
						}
					}
					cart.setGoods_set(list);
					o.setCart(cart);
				}
				map.put("DATA", o);
			}
			// 加菜订单
			OrderInfoAdd o_add = (OrderInfoAdd) dao
					.getOneObject("from OrderInfoAdd o where o.table_id=" + table_id + " and o.status in (0,-1)");
			if (null != o_add) {
				if (null != t) {
					o_add.setTable(t);
					Area a = (Area) dao.getOneObject("from Area a where area_id=" + t.getArea_id());
					if (null != a) {
						o_add.setArea(a);
					}
				}
				ShoppingCartAdd cart_add = (ShoppingCartAdd) dao
						.getOneObject("from ShoppingCartAdd s where s.cart_id=" + o_add.getShopping_cart_id());
				if (null != cart_add) {
					List<Object> list_add = dao
							.getAllObject("from ShoppingCartGoodsAdd scg where scg.cart_id=" + cart_add.getCart_id());
					cart_add.setGoods_set(list_add);
					o_add.setCart(cart_add);
				}
				map.put("DATA_ADD", o_add);
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
	 * 查询某一桌订单 店铺
	 * 
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/order/getOrderForAdmin", method = RequestMethod.POST)
	public @ResponseBody Object selectOrderAdmin(String table_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			OrderInfo o = (OrderInfo) dao
					.getOneObject("from OrderInfo o where o.table_id=" + table_id + " and o.status in (0,-1)");
			if (null != o) {
				TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where table_id=" + o.getTable_id());
				if (null != t) {
					o.setTable(t);
					Area a = (Area) dao.getOneObject("from Area a where area_id=" + t.getArea_id());
					if (null != a) {
						o.setArea(a);
					}
				}
				ShoppingCart cart = (ShoppingCart) dao
						.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
				List<Object> list = dao
						.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + cart.getCart_id());
				// 退菜内容
				List<Object> back_list = dao
						.getAllObject("from ShoppingCartGoodsBack scg where scg.cart_id=" + cart.getCart_id());
				if (null != list && null != back_list) {
					for (Object object : back_list) {
						list.add(object);
					}
				}
				cart.setGoods_set(list);
				o.setCart(cart);
				map.put("DATA", o);
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
	 * 查询某一桌订单 顾客
	 * 
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/userorder/getOrder", method = RequestMethod.POST)
	public @ResponseBody Object selectOrderPerson(String table_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			OrderInfo o = (OrderInfo) dao
					.getOneObject("from OrderInfo o where o.table_id=" + table_id + " and o.status in (0,-1)");
			if (null != o) {
				TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where table_id=" + o.getTable_id());
				if (null != t) {
					o.setTable(t);
					Area a = (Area) dao.getOneObject("from Area a where area_id=" + t.getArea_id());
					if (null != a) {
						o.setArea(a);
					}
				}
				ShoppingCart cart = (ShoppingCart) dao
						.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
				if (cart != null) {
					List<Object> list = dao
							.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + cart.getCart_id());
					cart.setGoods_set(list);
					o.setCart(cart);
				}
				map.put("DATA", o);
			}

			// 加菜
			OrderInfoAdd o_add = (OrderInfoAdd) dao
					.getOneObject("from OrderInfoAdd o where o.table_id=" + table_id + " and o.status in (0,-1)");
			if (null != o_add) {
				TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where table_id=" + o_add.getTable_id());
				if (null != t) {
					o_add.setTable(t);
					Area a = (Area) dao.getOneObject("from Area a where area_id=" + t.getArea_id());
					if (null != a) {
						o_add.setArea(a);
					}
				}
				ShoppingCartAdd cart_add = (ShoppingCartAdd) dao
						.getOneObject("from ShoppingCartAdd s where s.cart_id=" + o_add.getShopping_cart_id());
				if (cart_add != null) {
					List<Object> list_add = dao
							.getAllObject("from ShoppingCartGoodsAdd scg where scg.cart_id=" + cart_add.getCart_id());
					cart_add.setGoods_set(list_add);
					o_add.setCart(cart_add);
				}
				map.put("DATA_ADD", o_add);
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
	 * 结账 服务员、店铺管理员
	 * 
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value = "/order/check", method = RequestMethod.POST)
	public @ResponseBody Object doCheck(String order_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(order_id)) {
			code = "1002";
			message = "参数错误";
		} else {
			try {
				OrderInfo o = (OrderInfo) dao.getOneObject("from OrderInfo o where order_id='" + order_id+"'");
				if (null != o) {
					OrderInfoAdd o_add = (OrderInfoAdd)dao.getOneObject("from OrderInfoAdd o where o.shop_id="+o.getShop_id()+" and o.table_id="+o.getTable_id()+" and o.status=-1");
					if(null==o_add){
						if(o.getStatus()==0){
							o.setStatus(1);// 修改状态
							Date date = new Date();
							o.setOrder_time(new Timestamp(date.getTime()));
							ShoppingCart s = (ShoppingCart) dao
									.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
							if (null != s) {
								s.setStatus(2);
								dao.doUpdateObject(s);
								List<Object> list = dao
										.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + s.getCart_id());
								s.setGoods_set(list);
								o.setCart(s);
							}
							TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + o.getTable_id());
							if (null != t) {
								t.setTable_status(0);
								dao.doUpdateObject(t);
								Area a = (Area)dao.getOneObject("from Area a where a.area_id="+t.getArea_id());
								o.setArea(a);
							}
							o.setTable(t);
							dao.doUpdateObject(o);
							ShopPrinter printer = (ShopPrinter)dao.getOneObject("from ShopPrinter s where s.shop_id="+o.getShop_id()+" and s.type_print=2");
							Waiter w = (Waiter)dao.getOneObject("from Waiter w where waiter_id="+o.getWaiter_id());
							if(StringUtils.isNotEmpty(printer.getPrinter_no())){
								checkPrint(o,printer.getPrinter_no(),w);
							}
							
							BookInfo book = (BookInfo)dao.getOneObject("from BookInfo b where b.table_id="+t.getTable_id()+" and b.shop_id="+t.getShop_id()+" and b.status=1");
							if(null!=book){
								t.setTable_status(2);
								dao.doUpdateObject(t);
							}
						}else if(o.getStatus()==-1){
							code = "1002";
							message = "此订单服务员还未确认，暂不能结账";
						}else{
							code = "1002";
							message = "此订单未处于用餐状态，无需结账处理！";
						}
					}else{
						code = "1002";
						message = "此桌有加菜订单未处理，请处理完进行结账";
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
		}

		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}

	/**
	 * 查询订单--分页 店铺
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/order/selectOrders", method = RequestMethod.POST)
	public @ResponseBody Object selectOrders(String shop_id, int page_no) {
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getOnePageResult(10, page_no, "from OrderInfo o where o.shop_id=" + shop_id);
			for (Object object : list) {
				OrderInfo order = (OrderInfo) object;
				// 1.查询分区 2.查询桌台 3.查询服务员
				Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + order.getArea_id());
				order.setArea(a);
				TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + order.getTable_id());
				order.setTable(t);
				Waiter w = (Waiter) dao.getOneObject("from Waiter w where waiter_id=" + order.getWaiter_id());
				order.setWaiter(w);
			}
			int total = dao.getAllNumber("from OrderInfo o where o.shop_id=" + shop_id);
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
	 * 取消订单
	 * 
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value = "/order/cancleOrder", method = RequestMethod.POST)
	public @ResponseBody Object cancleOrder(String order_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			OrderInfo o = (OrderInfo) dao.getOneObject("from OrderInfo o where o.order_id='" + order_id+"'");
			if (null != o) {
				int status = o.getStatus();
				ShoppingCart s = (ShoppingCart) dao
						.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
				if (null != s) {
					s.setStatus(3);// 取消
					dao.doUpdateObject(s);
				}
				if (status == 0 || status==-1) {// 未处理的订单取消，更新桌台状态
					TableInfo t = (TableInfo) dao.getOneObject("from TableInfo t where t.table_id=" + o.getTable_id());
					if (null != t) {
						t.setTable_status(0);
						dao.doUpdateObject(t);
					}
				}
				o.setStatus(2);
				dao.doUpdateObject(o);
				OrderInfoAdd o_add = (OrderInfoAdd)dao.getOneObject("from OrderInfoAdd o where o.table_id="+o.getTable_id());
				if(null!=o_add){
					dao.doDeleteObject(o_add);
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
	 * 服务员确认顾客提交订单
	 * 
	 * @param order_id
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/order/confirmOrder", method = RequestMethod.POST)
	public @ResponseBody Object doConfirmOrder(String order_id, String table_id, String waiter_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if ((StringUtils.isEmpty(order_id) && StringUtils.isEmpty(table_id)) || StringUtils.isEmpty(waiter_id)) {
				code = "1002";
				message = "未取到参数";
			} else {
				StringBuffer buffer = new StringBuffer();
				if (StringUtils.isNotEmpty(order_id)) {
					buffer.append("from OrderInfo o where order_id='" + order_id + "' and o.status in (-1,0)");
				} else {
					buffer.append("from OrderInfo o where table_id=" + table_id + "and o.status in (-1,0)");
				}
				OrderInfo o = (OrderInfo) dao.getOneObject(buffer.toString());
				if (null != o && o.getStatus() == -1) {
					o.setStatus(0);
					o.setWaiter_id(Integer.valueOf(waiter_id));
					Date date = new Date();
					o.setOrder_time(new Timestamp(date.getTime()));
					dao.doUpdateObject(o);
					//处理销量
					List<Object> list = dao.getAllObject("from ShoppingCartGoods scg where scg.cart_id="+o.getShopping_cart_id());
					if(null!=list){
						for (Object object : list) {
							ShoppingCartGoods scg = (ShoppingCartGoods)object;
							Goods good = (Goods) dao.getOneObject("from Goods t where good_id=" + scg.getGood_id());
							scg.setPrinter_no_str(good.getPrint_str());
							scg.setIf_print(good.getIf_print());
							doUpdateGoodsSalesCount(scg.getGood_id(),scg.getGood_num());
						}
					}
					List<Object> printers = dao
							 .getAllObject("from ShopPrinter sp where sp.shop_id=" + o.getShop_id() + "and sp.type_print=1");
					TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+o.getTable_id());
					Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
					Shop shop = (Shop)dao.getOneObject("from Shop s where s.shop_id="+o.getShop_id());
					int print_way = 1;
					if(null!=shop){
						print_way = shop.getPrint_way();
					}
					//后厨打印
					Api_java_demo printer = new Api_java_demo(o.getShop_id()+"",list,null,o.getTable_id()+"","1",printers,table,area,o,print_way);
					Thread t = new Thread(printer);
					t.start();
				} else {
					if ((null != o && o.getStatus() == 0)||null==o) {
						// 是否有加菜订单为处理
						StringBuffer buffer_add = new StringBuffer();
						if (StringUtils.isNotEmpty(order_id)) {
							buffer_add.append("from OrderInfoAdd o where o.order_id='" + order_id + "' and o.status=-1");
						} else {
							buffer_add.append("from OrderInfoAdd o where o.table_id=" + table_id + " and o.status=-1");
						}
						OrderInfoAdd o_add = (OrderInfoAdd) dao.getOneObject(buffer_add.toString());
						if (o_add != null) {
							if(null==o){
								o = (OrderInfo)dao.getOneObject("from OrderInfo o where o.table_id="+o_add.getTable_id()+" and o.status=0");
							}
							if(null!=o){
								List<Object> goods_add_list = null;
								// 将商品及订单合并
								ShoppingCartAdd sca = (ShoppingCartAdd) dao.getOneObject(
										"from ShoppingCartAdd s where s.cart_id=" + o_add.getShopping_cart_id());
								ShoppingCart sc = (ShoppingCart) dao
										.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
								if (null != sca && null != sc) {
									goods_add_list = dao.getAllObject(
											"from ShoppingCartGoodsAdd s where s.cart_id=" + sca.getCart_id());
									List<Object> goods_list = dao
											.getAllObject("from ShoppingCartGoods s where s.cart_id=" + sc.getCart_id());
									// 合并购物车菜品
									for (Object object : goods_add_list) {
										boolean is_has = false;
										ShoppingCartGoodsAdd scga = (ShoppingCartGoodsAdd) object;
										for (Object object2 : goods_list) {
											ShoppingCartGoods scg = (ShoppingCartGoods) object2;
											if (scga.getGood_id() == scg.getGood_id()) {
												scga.setPrinter_no_str(scg.getPrinter_no_str());
												scg.setGood_num(scg.getGood_num() + scga.getGood_num()); // 合并数量
												scg.setGood_total_price(
														scg.getGood_total_price() + scga.getGood_total_price());// 合并金额
												dao.doUpdateObject(scg);
												is_has = true;
												break;
											}
										}
										if (!is_has) { // 原来购物车中没有商品
											ShoppingCartGoods scg = new ShoppingCartGoods();
											scg.setCart_id(sc.getCart_id());
											scg.setGood_id(scga.getGood_id());
											scg.setGood_name(scga.getGood_name());
											scg.setGood_price(scga.getGood_price());
											scg.setGood_num(scga.getGood_num());
											scg.setGood_total_price(scga.getGood_price());
											scg.setPre_price(scga.getPre_price());
											scg.setDiscount(scga.getDiscount());
											scg.setImg_url(scga.getImg_url());
											dao.doSaveObject(scg);
										}
										Goods good = (Goods) dao.getOneObject("from Goods t where good_id=" + scga.getGood_id());
										scga.setPrinter_no_str(good.getPrint_str());
										scga.setIf_print(good.getIf_print());
										doUpdateGoodsSalesCount(scga.getGood_id(),scga.getGood_num()); //处理销量
										dao.doDeleteObject(scga);// 删除加菜购物车商品
										
									}
									// 合并购物车
									sc.setTotal_num(sc.getTotal_num() + sca.getTotal_num());
									sc.setTotal_price(sc.getTotal_price() + sca.getTotal_price());
									dao.doUpdateObject(sc);
									dao.doDeleteObject(sca);// 删除加菜购物车
								}
								// 合并订单
								o.setGoods_num(o.getGoods_num() + o_add.getGoods_num());
								o.setPayment(o.getPayment() + o_add.getPayment());
								dao.doUpdateObject(o);
								dao.doDeleteObject(o_add);// 删除加菜订单
								
								Shop shop = (Shop)dao.getOneObject("from Shop s where s.shop_id="+o.getShop_id());
								int print_way = 1;
								if(null!=shop){
									print_way = shop.getPrint_way();
								}
								//后厨打印
								List<Object> printers = dao
										 .getAllObject("from ShopPrinter sp where sp.shop_id=" + o.getShop_id() + "and sp.type_print=1");
								TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+o.getTable_id());
								Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
								o.setComments(o_add.getComments());//为了打印出加菜备注
								o.setWay(o_add.getWay());
								o.setGo_goods_way(o_add.getGo_goods_way());
								Api_java_demo printer = new Api_java_demo(o.getShop_id()+"",null,goods_add_list,o.getTable_id()+"","1",printers,table,area,o,print_way);
								Thread t = new Thread(printer);
								t.start();
							}
						} else {
							code = "1001";
							message = "未查到订单信息";
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
	 * 更新订单价钱 用于柜台结账前
	 * 
	 * @param order
	 * @return
	 */
	@RequestMapping(value = "/order/updateOrder", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateOrder(OrderInfo order) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();

		OrderInfo o = (OrderInfo) dao.getOneObject("from OrderInfo o where o.order_id = '" + order.getOrder_id()+"'");
		try {
			if (null != o) {
				o.setDiscount(order.getDiscount());
				o.setDiscount_payment(order.getDiscount_payment());
				o.setMinus_money(order.getMinus_money());
				o.setReal_payment(order.getReal_payment());
				dao.doUpdateObject(o);
			} else {
				code = "1001";
				message = "未查询到订单数据";
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
	 * 服务员订单列表
	 * 
	 * @param waiter_id
	 * @return
	 */
	@RequestMapping(value = "/order/getOrderListForWaiter", method = RequestMethod.POST)
	public @ResponseBody Object selectOrderList(String waiter_id, int page_no, String status,String shop_id) {//新增shop_id
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isEmpty(shop_id)){
			code = "1001";
			message = "店铺ID未取到，参数异常";
		}else{
			try {
				if (StringUtils.isEmpty(waiter_id)) {
					List<Object> list = dao.getOnePageResult(10, page_no, "from OrderInfo o where o.status=-1 and o.shop_id="+shop_id+" order by o.create_time desc");
					for (Object object : list) {
						OrderInfo order = (OrderInfo) object;
						// 1.查询分区 2.查询桌台 3.查询服务员
						Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + order.getArea_id());
						order.setArea(a);
						TableInfo t = (TableInfo) dao
								.getOneObject("from TableInfo t where t.table_id=" + order.getTable_id());
						order.setTable(t);
						// Waiter w = (Waiter)dao.getOneObject("from Waiter w where
						// waiter_id="+order.getWaiter_id());
						// order.setWaiter(w);
						ShoppingCart cart = (ShoppingCart) dao
								.getOneObject("from ShoppingCart s where s.cart_id=" + order.getShopping_cart_id());
						if (null != cart) {
							List<Object> l = dao
									.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + cart.getCart_id());
							cart.setGoods_set(l);
							order.setCart(cart);
						}
					}
					
					//加菜未处理查询合并
					List<Object> list_add = dao.getOnePageResult(10, page_no, "from OrderInfoAdd o where o.status=-1 and o.shop_id="+shop_id+" order by o.create_time desc");
					for (Object object : list_add) {
						OrderInfoAdd order_add = (OrderInfoAdd) object;
						// 1.查询分区 2.查询桌台 3.查询服务员
						Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + order_add.getArea_id());
						order_add.setArea(a);
						TableInfo t = (TableInfo) dao
								.getOneObject("from TableInfo t where t.table_id=" + order_add.getTable_id());
						order_add.setTable(t);
						// Waiter w = (Waiter)dao.getOneObject("from Waiter w where
						// waiter_id="+order.getWaiter_id());
						// order.setWaiter(w);
						ShoppingCartAdd cart_add = (ShoppingCartAdd) dao
								.getOneObject("from ShoppingCartAdd s where s.cart_id=" + order_add.getShopping_cart_id());
						if (null != cart_add) {
							List<Object> l = dao
									.getAllObject("from ShoppingCartGoodsAdd scg where scg.cart_id=" + cart_add.getCart_id());
							cart_add.setGoods_set(l);
							order_add.setCart(cart_add);
						}
						list.add(order_add);
					}
					
					int total = dao.getAllNumber("from OrderInfo o where o.status=-1");
					int all_page_no = total % 10 > 0 ? total / 10 + 1 : total / 10;
					map.put("DATA", list);
					map.put("totalnum", total);
					map.put("page_no", page_no);
					map.put("pre_no", page_no - 1 > 0 ? page_no - 1 : 0);
					map.put("post_no", page_no + 1 > all_page_no ? all_page_no : page_no + 1);
				} else {
					List<Object> list = dao.getOnePageResult(10, page_no,
							"from OrderInfo o where o.shop_id=" + shop_id + " and o.status=" + status+" order by o.create_time desc");
					for (Object object : list) {
						OrderInfo order = (OrderInfo) object;
						// 1.查询分区 2.查询桌台 3.查询服务员
						Area a = (Area) dao.getOneObject("from Area a where a.area_id=" + order.getArea_id());
						order.setArea(a);
						TableInfo t = (TableInfo) dao
								.getOneObject("from TableInfo t where t.table_id=" + order.getTable_id());
						order.setTable(t);
						Waiter w = (Waiter) dao.getOneObject("from Waiter w where waiter_id=" + order.getWaiter_id());
						order.setWaiter(w);
						ShoppingCart cart = (ShoppingCart) dao
								.getOneObject("from ShoppingCart s where s.cart_id=" + order.getShopping_cart_id());
						if (null != cart) {
							List<Object> l = dao
									.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + cart.getCart_id());
							// 退菜内容
							List<Object> back_list = dao
									.getAllObject("from ShoppingCartGoodsBack scg where scg.cart_id=" + cart.getCart_id());
							if (null != l && null != back_list) {
								for (Object o : back_list) {
									l.add(o);
								}
							}
							cart.setGoods_set(l);
							order.setCart(cart);
						}
					}
					int total = dao.getAllNumber("from OrderInfo o where o.shop_id=" + shop_id);
					int all_page_no = total % 10 > 0 ? total / 10 + 1 : total / 10;
					map.put("DATA", list);
					map.put("totalnum", total);
					map.put("page_no", page_no);
					map.put("pre_no", page_no - 1 > 0 ? page_no - 1 : 0);
					map.put("post_no", page_no + 1 > all_page_no ? all_page_no : page_no + 1);
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
	 * 根据订单ID查询订单
	 * 
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value = "/order/getOneOrderBynNum", method = RequestMethod.POST)
	public @ResponseBody Object selectOneOrderById(String order_num) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(order_num)) {
			code = "1001";
			message = "参数异常，请传入订单编号";
		} else {
			try {
				OrderInfo order = (OrderInfo) dao
						.getOneObject("from OrderInfo o where o.order_num='" + order_num + "'");
				if (null != order) {
					TableInfo t = (TableInfo) dao
							.getOneObject("from TableInfo t where table_id=" + order.getTable_id());
					if (null != t) {
						order.setTable(t);
						Area a = (Area) dao.getOneObject("from Area a where area_id=" + t.getArea_id());
						if (null != a) {
							order.setArea(a);
						}
					}
					ShoppingCart cart = (ShoppingCart) dao
							.getOneObject("from ShoppingCart s where s.cart_id=" + order.getShopping_cart_id());
					if (cart != null) {
						List<Object> list = dao
								.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + cart.getCart_id());
						// 退菜内容
						List<Object> back_list = dao
								.getAllObject("from ShoppingCartGoodsBack scg where scg.cart_id=" + cart.getCart_id());
						if (null != list && null != back_list) {
							for (Object o : back_list) {
								list.add(o);
							}
						}
						cart.setGoods_set(list);
						order.setCart(cart);
					}
					map.put("DATA", order);
				} else {
					// 查询加菜订单
					OrderInfoAdd order_add = (OrderInfoAdd) dao
							.getOneObject("from OrderInfoAdd o where o.order_num='" + order_num + "'");
					if (null != order_add) {
						TableInfo t = (TableInfo) dao
								.getOneObject("from TableInfo t where table_id=" + order_add.getTable_id());
						if (null != t) {
							order_add.setTable(t);
							Area a = (Area) dao.getOneObject("from Area a where area_id=" + t.getArea_id());
							if (null != a) {
								order_add.setArea(a);
							}
						}
						ShoppingCartAdd cart_add = (ShoppingCartAdd) dao.getOneObject(
								"from ShoppingCartAdd s where s.cart_id=" + order_add.getShopping_cart_id());
						if (cart_add != null) {
							List<Object> list = dao.getAllObject(
									"from ShoppingCartGoodsAdd scg where scg.cart_id=" + cart_add.getCart_id());
							cart_add.setGoods_set(list);
							order_add.setCart(cart_add);
						}
						map.put("DATA", order_add);
					} else {
						code = "1001";
						message = "未查到订单";
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
	 * 更新销量
	 * 
	 * @param goods_id
	 * @param num
	 */
	public void doUpdateGoodsSalesCount(int goods_id, int num) {
		try {
			Goods goods = (Goods) dao.getOneObject("from Goods g where g.good_id=" + goods_id);
			if (null != goods) {
				goods.setSales_count(goods.getSales_count() + num);
				dao.doUpdateObject(goods);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询打印内容
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value="/order/getPrintContentByOrder",method=RequestMethod.POST)
	public @ResponseBody Object selectPrintContentByOrder(String order_id){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<CB>预结单</CB><BR>");
		buffer.append("--------------------------------<BR>");
		try {
			SimpleDateFormat simpleDateFormat;
			simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
			Date date = new Date();
			String str = simpleDateFormat.format(date);
			OrderInfo order = (OrderInfo)dao.getOneObject("from OrderInfo o where o.order_id='"+order_id+"'");
			if(null!=order){
				Waiter waiter = (Waiter)dao.getOneObject("from Waiter w where waiter_id="+order.getWaiter_id());
				TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where table_id="+order.getTable_id());
				if(null!=table){
					Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
					buffer.append("桌号：" + area.getArea_name()+"--"+table.getTable_name() + "  人数:" + order.getPeople_count() + " 服务员:" + waiter.getName() + "<BR>");
				}
				buffer.append("账单编号：" + order.getOrder_num() + "<BR>");
				buffer.append("时间：" + str + "<BR>");
				buffer.append("--------------------------------<BR>");
				buffer.append("名称　　单价   数量   金额<BR>");
				buffer.append("--------------------------------<BR>");
				ShoppingCart cart = (ShoppingCart)dao.getOneObject("from ShoppingCart sc where sc.cart_id="+order.getShopping_cart_id());
				if(null!=cart){
					List<Object> list = dao.getAllObject("from ShoppingCartGoods scg where scg.cart_id="+cart.getCart_id());
					if(null!=list){
						for (Object object : list) {
							ShoppingCartGoods s = (ShoppingCartGoods)object;
							buffer.append(s.getGood_name()+"<BR>");
							buffer.append("  　　  " + s.getGood_price() + "   " + s.getGood_num() + "   "
									+ s.getGood_total_price() + "<BR>");
						}
					}
				}
				List<Object> back_list = dao
						.getAllObject("from ShoppingCartGoodsBack scg where scg.cart_id=" + order.getShopping_cart_id());
				if(null!=back_list){
					for (Object object : back_list) {
						ShoppingCartGoodsBack s = (ShoppingCartGoodsBack) object;
						buffer.append(s.getGood_name()+"<BR>");
						buffer.append("      " + s.getGood_price() + "   " + s.getGood_num() + "      "
								+ s.getGood_total_price() + "<BR>");
					}
				}
				buffer.append("							总计:" + order.getPayment() + "<BR>");
				buffer.append("--------------------------------<BR>");
				buffer.append("应收金额:" + order.getPayment() + "<BR>");
				buffer.append("折扣后金额:" + order.getDiscount_payment() + "<BR>");
				buffer.append("抹零:" + order.getMinus_money() + "<BR>");
				buffer.append("实收金额:" + order.getReal_payment() + "<BR>");
				buffer.append("--------------------------------<BR>");
			}
			map.put("DATA", buffer.toString());
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
	 * 查询店铺未处理的订单和服务数量
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value="/order/getCountForOrderService",method=RequestMethod.POST)
	public @ResponseBody Object selectCountForOrderService(String shop_id){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			int order_total = dao.getAllNumber("from OrderInfo o where o.shop_id="+shop_id+" and o.status=-1");
			int service_total = dao.getAllNumber("from Service s where s.shop_id="+shop_id+" and  s.status=0");
			map.put("ORDER_COUNT", order_total);
			map.put("SERVICE_COUNT", service_total);
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
	 * 服务员更新订单备注、人数、堂食打包、上菜时间等
	 * @param order
	 * @return
	 */
	@RequestMapping(value="/order/updateOrderInfo",method=RequestMethod.POST)
	public @ResponseBody Object doUpdateOrderInfo(OrderInfo order){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtils.isEmpty(order.getOrder_num())){
				code = "1002";
				message = "订单编号参数异常";
			}else{
				OrderInfo o = (OrderInfo)dao.getOneObject("from OrderInfo o where o.order_num="+order.getOrder_num());
				OrderInfoAdd o_add = (OrderInfoAdd)dao.getOneObject("from OrderInfoAdd o where o.order_num="+order.getOrder_num());
				if(null!=o){
					if(StringUtils.isNotEmpty(order.getComments())){
						o.setComments(order.getComments());
					}
					if(order.getPeople_count()>0){
						o.setPeople_count(order.getPeople_count());
					}
					if(order.getWay()>0){
						o.setWay(order.getWay());
					}
					if(order.getGo_goods_way()>0){
						o.setGo_goods_way(order.getGo_goods_way());
					}
					dao.doUpdateObject(o);
				}else if(null!=o_add){
					if(StringUtils.isNotEmpty(order.getComments())){
						o_add.setComments(order.getComments());
					}
					if(order.getPeople_count()>0){
						o_add.setPeople_count(order.getPeople_count());
					}
					if(order.getWay()>0){
						o_add.setWay(order.getWay());
					}
					if(order.getGo_goods_way()>0){
						o_add.setGo_goods_way(order.getGo_goods_way());
					}
					dao.doUpdateObject(o_add);
				}else{
					code = "1002";
					message = "未查询到订单信息";
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
	
	public void checkPrint(OrderInfo order,String printer_no,Waiter waiter){
		Api_java_demo printer = new Api_java_demo();
		printer.setFlag("2");
		printer.setO(order);
		printer.setPrinter(printer_no);
		printer.setW(waiter);
		Thread thread = new Thread(printer);
		thread.start();
	}
	
	/**
	 * 打印订单
	 * @param shop_id
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value="/order/print",method=RequestMethod.POST)
	public @ResponseBody Object doPrint(String shop_id,String order_id){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(order_id)) {
			code = "1002";
			message = "参数错误";
		} else {
			try {
				OrderInfo o = (OrderInfo) dao.getOneObject("from OrderInfo o where order_id='" + order_id+"'");
				if (null != o) {
					ShoppingCart s = (ShoppingCart) dao
							.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
					if (null != s) {
						List<Object> list = dao
								.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + s.getCart_id());
						s.setGoods_set(list);
						o.setCart(s);
						TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+o.getTable_id());
						o.setTable(table);
						if(null!=table){
							Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
							o.setArea(area);
						}
						List<Object> back_list = dao
								.getAllObject("from ShoppingCartGoodsBack scg where scg.cart_id=" + s.getCart_id());
						if (null != list && null != back_list) {
							for (Object object : back_list) {
								list.add(object);
							}
						}
					}
					ShopPrinter printer = (ShopPrinter)dao.getOneObject("from ShopPrinter s where s.shop_id="+o.getShop_id()+" and s.type_print=2");
					Waiter w = (Waiter)dao.getOneObject("from Waiter w where waiter_id="+o.getWaiter_id());
					if(StringUtils.isNotEmpty(printer.getPrinter_no())){
						checkPrint(o,printer.getPrinter_no(),w);
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
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	

}
