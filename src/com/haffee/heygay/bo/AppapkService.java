package com.haffee.heygay.bo;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Appapk;
import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.Goods;
import com.haffee.heygay.po.GoodsCategory;
import com.haffee.heygay.po.OrderInfo;
import com.haffee.heygay.po.OrderInfoAdd;
import com.haffee.heygay.po.Shop;
import com.haffee.heygay.po.ShopPrinter;
import com.haffee.heygay.po.ShoppingCart;
import com.haffee.heygay.po.ShoppingCartAdd;
import com.haffee.heygay.po.ShoppingCartGoods;
import com.haffee.heygay.po.ShoppingCartGoodsAdd;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.po.User;
import com.haffee.heygay.po.Waiter;
import com.haffee.heygay.util.JPush;
import com.haffee.heygay.util.PrintUtils;

/**
 * app版本管理
 * 
 * @author wangjt
 *
 */
@Controller
public class AppapkService  {

	@Autowired
	private IUserDao dao;

	/**
	 * 查询apk版本
	 * 
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value = "/app/apk", method = RequestMethod.GET)
	public @ResponseBody Object app() {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {   //获取商铺信息
			List<Object> list = dao.getAllObject("from Appapk  order by time desc");
			if(list!=null&&list.size()>0){ //modify by jacktong
				list.get(0);
			}
			map.put("DATA", list.get(0));
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
