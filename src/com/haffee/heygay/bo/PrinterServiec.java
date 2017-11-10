package com.haffee.heygay.bo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Goods;
import com.haffee.heygay.po.ShopPrinter;
import com.haffee.heygay.util.Api_java_demo;

import cn.jiguang.common.utils.StringUtils;


@Controller
public class PrinterServiec {
	
	@Autowired
	private IUserDao dao;
	
	/**
	 * 加载打印机列表
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value="/printer/printerlist",method = RequestMethod.POST)
	public @ResponseBody Object selectPrint(String shop_id){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getAllObject("from ShopPrinter sp where sp.shop_id="+shop_id);
			map.put("DATA", list);
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
	 * 添加打印机
	 * @param printer
	 * @return
	 */
	@RequestMapping(value="/printer/addprinter",method = RequestMethod.POST)
	public @ResponseBody Object doAddPrinter(ShopPrinter printer){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(printer.getId()==0){
				Date date = new Date();
				printer.setCreate_time(new Timestamp(date.getTime()));
				dao.doSaveObject(printer);
			}else{
				Date date = new Date();
				printer.setCreate_time(new Timestamp(date.getTime()));
				dao.doUpdateObject(printer);
			}
			
			List<Object> list = dao.getAllObject("from Goods g where g.shop_id="+printer.getShop_id());
			if(null!=list&&list.size()>0){
				for (Object object : list) {
					Goods g = (Goods)object;
					if(null==g.getPrint_str()){
						g.setPrint_str(printer.getPrinter_no());
					}else{
						g.setPrint_str(g.getPrint_str()+","+printer.getPrinter_no());
					}
					dao.doUpdateObject(g);
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
	 * 删除打印机
	 * @param printer_id
	 * @return
	 */
	@RequestMapping(value="/printer/deletePrinter",method=RequestMethod.POST)
	public @ResponseBody Object doDeletePrinter(String printer_id,String shop_id){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ShopPrinter p = (ShopPrinter)dao.getOneObject("from ShopPrinter sp where sp.id="+printer_id);
			//获取菜品列表
     		List<Object> goods = dao.getAllObject("from Goods t where shop_id=" + shop_id + "");
     		Goods gds = new Goods();
     		if(null!=goods&&goods.size()>0){
				for(Object object1 :goods){
					gds =(Goods)object1;
					String dyjs = gds.getPrint_str();
	    	        String[] dyj = dyjs.split("\\,");
	    	        String dyjsnew = "";
	    	        for (int i = 0; i < dyj.length; i++) {   
	    	            if (!p.getPrinter_no().contains(dyj[i])){
	    	            	if(dyjsnew.equals("")){
	    	            		dyjsnew = dyj[i];
	    	            	}else{
	    	            		dyjsnew = dyjsnew+","+dyjsnew;
	    	            	}
	    	            }
	    	        }
	    	        gds.setPrint_str(dyjsnew);
	    	        dao.doUpdateObject(gds);
				}
     		}
			if(null!=p){
				dao.doDeleteObject(p);
			}else{
				code = "1001";
				message = "未查到打印机";
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
	 * 打印服务
	 * @param print_no
	 * @param content
	 * @return
	 */
	@RequestMapping(value="/printer/doPrinter",method=RequestMethod.POST)
	public @ResponseBody Object doPrint(String print_no,String content){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isEmpty(print_no)){
			code = "1002";
			message = "参数异常";
		}else{
			try {
				Api_java_demo printer = new Api_java_demo("3",print_no,content);
				Thread thread = new Thread(printer);
				thread.start();
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
