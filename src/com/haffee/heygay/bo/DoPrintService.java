package com.haffee.heygay.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.Goods;
import com.haffee.heygay.po.OrderInfo;
import com.haffee.heygay.po.Shop;
import com.haffee.heygay.po.ShopPrinter;
import com.haffee.heygay.po.ShoppingCart;
import com.haffee.heygay.po.ShoppingCartGoods;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.po.Waiter;
import com.haffee.heygay.util.Api_java_demo;

/**
 * 后厨 和结账打印
 * 
 * @author wangjt
 *
 */
@Controller
public class DoPrintService  {

	@Autowired
	private IUserDao dao;

	/**
	 * 结账打印
	 * 
	 * @param order_id
	 * @return
	 */
	//@RequestMapping(value = "/order/print", method = RequestMethod.POST)
	public @ResponseBody Object doCheck(String order_id,String shop_id) {
		String message = "成功";
		String code = "1000";
		String ifprint = "0";
		Map<String, Object> map = new HashMap<String, Object>();
		try {   //获取商铺信息
		    	Shop shop = (Shop)dao.getOneObject("from Shop s where s.shop_id=" + shop_id);
		    	//获取订单信息
		        OrderInfo o = (OrderInfo) dao.getOneObject("from OrderInfo o where o.order_id=" + order_id);
		        //获取服务员信息
				Waiter w = (Waiter)dao.getOneObject("from Waiter w where w.shop_id="+shop_id+"and w.waiter_id="+o.getWaiter_id());
				if (null != o) {
					TableInfo t = (TableInfo)dao.getOneObject("from TableInfo t where table_id="+o.getTable_id());
					if(null!=t){
						o.setTable(t);
						Area a = (Area)dao.getOneObject("from Area a where area_id="+t.getArea_id());
						if(null!=a){
							o.setArea(a);
						}
					}
					ShoppingCart cart = (ShoppingCart) dao
							.getOneObject("from ShoppingCart s where s.cart_id=" + o.getShopping_cart_id());
					List<Object> list = dao.getAllObject("from ShoppingCartGoods scg where scg.cart_id="+cart.getCart_id());
					cart.setGoods_set(list);
					o.setCart(cart);

				} else {
				}
				//获取结账打印机列表
				List<Object> printers = dao.getAllObject("from ShopPrinter sp where sp.shop_id="+shop_id+"and sp.type_print=1");
				//菜品分类
	     		List<Object> ctg_list = dao.getAllObject("from GoodsCategory c where c.shop_id=" + shop_id + "");
	     		//获取菜品列表
	     		List<Object> goods = dao.getAllObject("from Goods t where shop_id=" + shop_id + "");
	     		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	        	//pras.add(new Copies(2));//打印份数，3份 
	        	DocFlavor flavor = DocFlavor.BYTE_ARRAY.PNG;
	        	//可用的打印机列表(字符串数组)
	        	 //查找所有打印服务  
	            //PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, pras);  
				 //PrintService myPrinter = null;  
				   //本地打印机循环
			    	//for (int y = 0; y < services.length; y++) {  
			    		//结账打印机循环
			    	for(Object object0 : printers){
			    		ShopPrinter shopprinter = (ShopPrinter)object0;
			    		//ifprint = "0";
			            //System.out.println("service found: " + services[y]);  
			            //String svcName = services[y].toString();  
			            //myPrinter = services[y]; 
			            //两者都有的进入菜品打印机判断
			            //if (svcName.contains(shopprinter.getPrinter_no())){
				            //System.out.println("my printer found: " + svcName);  
				            //System.out.println("my printer found: " + myPrinter);
				            //菜品打印机循环
//				    		for(Object object : o.getCart().getGoods_set()){
//				    			ShoppingCartGoods s =(ShoppingCartGoods)object;
//				    			Goods good = (Goods)dao.getOneObject("from Goods t where good_id="+s.getGood_id());
//				    	        String dyjs = "";
//				    	        dyjs = good.getPrint_str();
//				    	        String[] dyj = dyjs.split("\\,");
//				    	        for (int i = 0; i < dyj.length; i++) {   
//				    	            if (shopprinter.getPrinter_no().contains(dyj[i])){
//				    	            	//菜品存在打印机可打印
//				    	            	ifprint = "1";
//				    	            }
//				    	        }
//				    		}
				    		//if(ifprint.equals("1")){
				    			//此处可读取配置表 打印次数
				    			Api_java_demo print = new Api_java_demo(shop_id,shopprinter.getPrinter_no(),o,w,"2");
				    			print.run();
				    		//}
	    	            //} 
			    	}
			        //}

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
