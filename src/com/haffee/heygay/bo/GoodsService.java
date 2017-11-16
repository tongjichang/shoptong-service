package com.haffee.heygay.bo;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.Goods;
import com.haffee.heygay.po.GoodsCategory;
import com.haffee.heygay.po.Shop;
import com.haffee.heygay.po.ShopPrinter;
import com.haffee.heygay.po.ShoppingCart;
import com.haffee.heygay.po.ShoppingCartAdd;
import com.haffee.heygay.po.ShoppingCartGoods;
import com.haffee.heygay.po.ShoppingCartGoodsAdd;
import com.haffee.heygay.po.TableInfo;

/**
 * 商品管理 适用于店铺管理员、个人、服务员
 * 
 * @author jacktong
 * @date 2017年7月4日
 *
 */
@Controller
public class GoodsService {

	@Autowired
	private IUserDao dao;

	/**
	 * 添加菜品分类 店铺管理员
	 * 
	 * @param ctg_name
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/goods/addCategory", method = RequestMethod.POST)
	public @ResponseBody Object doAddCategory(GoodsCategory ctg) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Date date = new Date();
			ctg.setCreate_time(new Timestamp(date.getTime()));
			ctg.setCategory_status(1); // 状态正常
			dao.doSaveObject(ctg);
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
	 * 修改分类
	 * 
	 * @param ctg
	 * @return
	 */
	@RequestMapping(value = "/goods/updateCategory", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateCategory(GoodsCategory ctg) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			GoodsCategory c = (GoodsCategory) dao
					.getOneObject("from GoodsCategory c where c.category_id=" + ctg.getCategory_id());
			if (null != c) {
				if (!"".equals(ctg.getCategory_name())) {
					c.setCategory_name(ctg.getCategory_name());
				}
				if (ctg.getCategory_status() != 0) {
					c.setCategory_status(ctg.getCategory_status());
				}
				dao.doUpdateObject(c);
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
	 * 删除分类
	 * 
	 * @param ctg
	 * @return
	 */
	@RequestMapping(value = "/goods/deleteCategory", method = RequestMethod.POST)
	public @ResponseBody Object doDeleteCtg(GoodsCategory ctg) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			dao.doDeleteObject(ctg);
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
	 * 查询商品分类，包含菜品
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/goods/selectCategory/{shop_id}", method = RequestMethod.GET)
	public @ResponseBody Object selectCategory(@PathVariable("shop_id") String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> ctg_list = dao
					.getAllObject("from GoodsCategory c where c.shop_id=" + shop_id + " and c.category_status=1");
			if (ctg_list.size() == 0) {
				code = "1001";
				message = "未查到数据";
			} else {
				for (int i = 0; i < ctg_list.size(); i++) {
					GoodsCategory c = (GoodsCategory) ctg_list.get(i);
					List<Object> goods_list = dao.getAllObject(
							"from Goods g where g.category_id = " + c.getCategory_id() + " and g.status=1");
					c.setGoods_list(goods_list);
				}
				map.put("DATA", ctg_list);
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
	 * 查询商品分类，包含菜品
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/goods/selectCtgForShop/{shop_id}", method = RequestMethod.GET)
	public @ResponseBody Object selectCategoryForShop(@PathVariable("shop_id") String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> ctg_list = dao.getAllObject("from GoodsCategory c where c.shop_id=" + shop_id + "");
			if (ctg_list.size() == 0) {
				code = "1001";
				message = "未查到数据";
			} else {
				for (int i = 0; i < ctg_list.size(); i++) {
					GoodsCategory c = (GoodsCategory) ctg_list.get(i);
					List<Object> goods_list = dao
							.getAllObject("from Goods g where g.category_id = " + c.getCategory_id() + "");
					for (Object object : goods_list) {
						Goods g = (Goods) object;

					}
					c.setGoods_list(goods_list);
				}
				map.put("DATA", ctg_list);
			}
			Shop s = (Shop) dao.getOneObject("from Shop s where shop_id=" + shop_id);
			map.put("SHOP", s);
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
	 * wap端
	 * 
	 * @param shop_id
	 * @param table_id
	 * @return
	 */
	@RequestMapping(value = "/goods/selectCtgForShopPerson/{shop_id}/{cart_id}", method = RequestMethod.GET)
	public @ResponseBody Object selectCategoryForShopPerson(@PathVariable("shop_id") String shop_id,@PathVariable("cart_id") String cart_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> ctg_list = dao.getAllObject("from GoodsCategory c where c.shop_id=" + shop_id + " and c.category_status = 1");
			if (ctg_list.size() == 0) {
				code = "1001";
				message = "未查到数据";
			} else {
				for (int i = 0; i < ctg_list.size(); i++) {
					GoodsCategory c = (GoodsCategory) ctg_list.get(i);
					List<Object> goods_list = dao
							.getAllObject("from Goods g where g.category_id = " + c.getCategory_id() + " and g.status = 1");
					for (Object object : goods_list) {
						Goods g = (Goods) object;
						ShoppingCart sc = (ShoppingCart) dao.getOneObject("from ShoppingCart s where  s.cart_id='" + cart_id+"' and s.status=0");
						if (null != sc) {
							ShoppingCartGoods scg = (ShoppingCartGoods) dao
									.getOneObject("from ShoppingCartGoods scg where scg.cart_id='" + sc.getCart_id()+"'");
							if(null!=scg)
								g.setIn_cart_num(scg.getGood_num());
						}
					}
					c.setGoods_list(goods_list);
				}
				map.put("DATA", ctg_list);
			}
			Shop s = (Shop) dao.getOneObject("from Shop s where shop_id=" + shop_id);
			map.put("SHOP", s);
			//TableInfo table = (TableInfo)dao.getOneObject("from TableInfo t where t.table_id="+table_id);
			//map.put("TABLE", table);
			//if(null!=table){
			//	Area area = (Area)dao.getOneObject("from Area a where a.area_id="+table.getArea_id());
			//	map.put("AREA", area);
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

	/**
	 * 添加商品
	 * 
	 * @param g
	 * @return
	 */
	@RequestMapping(value = "/goods/addGoods", method = RequestMethod.POST)
	public @ResponseBody Object doAddGoods(Goods g, @RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request) {
		String imgUrl = "";
		Date date = new Date();
		if(null!=file){
			// 获得文件：
			String path = request.getSession().getServletContext().getRealPath("shop_" + g.getShop_id());
			//String fileName = file.getOriginalFilename();
			SimpleDateFormat simpleDateFormat;
			simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			String str = simpleDateFormat.format(date);
			String fileName = str+(int)(Math.random()*10000000)+".jpg";
			File targetFile = new File(path, fileName);
			if (!targetFile.exists()) {
				targetFile.mkdirs();
			}
			// 保存
			try {
				file.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			imgUrl = request.getContextPath() + "/shop_" + g.getShop_id() + "/" + fileName;
		}else{
			imgUrl = request.getContextPath() +"/default.jpg";
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getAllObject("from ShopPrinter sp where sp.shop_id="+g.getShop_id());
			StringBuffer buffer = new StringBuffer();
			if(null!=list&&list.size()>0){
				for (Object object : list) {
					ShopPrinter printer = (ShopPrinter)object;
					buffer.append(printer.getPrinter_no()+",");
				}
			}
			g.setStatus(1);
			g.setImg_url(imgUrl);
			g.setCreate_time(new Timestamp(date.getTime()));
			g.setTuijian("0");
			if(buffer.length()>0){
				g.setPrint_str(buffer.substring(0,buffer.length()-1));
			}
			dao.doSaveObject(g);
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
	 * 删除商品
	 * 
	 * @param g
	 * @return
	 */
	@RequestMapping(value = "/goods/deleteGoods", method = RequestMethod.POST)
	public @ResponseBody Object doDelete(Goods g) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			dao.doDeleteObject(g);
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
	 * 查询单个商品 修改使用
	 * 
	 * @param goods_id
	 * @return
	 */
	@RequestMapping(value = "/goods/selectGoods", method = RequestMethod.POST)
	public @ResponseBody Object selectGoods(String goods_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Object o = dao.getOneObject("from Goods g where g.good_id=" + goods_id);
			if (null == o) {
				code = "1001";
				message = "未查到数据";
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
	 * 更新商品信息
	 * 
	 * @param g
	 * @return
	 */
	@RequestMapping(value = "/goods/updateGoods", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateGoods(Goods g,
			@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Goods good = (Goods) dao.getOneObject("from Goods g where g.good_id=" + g.getGood_id());
			if (null == good) {
				code = "1001";
				message = "未查到数据";
			} else {
				if (g.getDiscount() != 0) {
					good.setDiscount(g.getDiscount());// 折扣
				}
				if (StringUtils.isNotEmpty(g.getGoods_name())) {
					good.setGoods_name(g.getGoods_name());
				}
				if (StringUtils.isNotEmpty(g.getImg_url())) {
					good.setImg_url(g.getImg_url());
				}
				if (StringUtils.isNotEmpty(g.getIntroduction())) {
					good.setIntroduction(g.getIntroduction());
				}
				if (g.getPre_price() != 0) {
					good.setPre_price(g.getPre_price());
				}
				if (g.getSales_count() != 0) {
					good.setSales_count(g.getSales_count());
				}
				if (g.getStatus() != 0) {
					good.setStatus(g.getStatus());
				}
				if (g.getCategory_id() != 0) {
					good.setCategory_id(g.getCategory_id());
				}
				if (null != file) {
					// 获得文件：
					String path = request.getSession().getServletContext().getRealPath("shop_" + g.getShop_id());
					String fileName = file.getOriginalFilename();
					System.out.println(path);
					File targetFile = new File(path, fileName);
					if (!targetFile.exists()) {
						targetFile.mkdirs();
					}
					// 保存
					try {
						file.transferTo(targetFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String imgUrl = request.getContextPath() + "/shop_" + g.getShop_id() + "/" + fileName;
					good.setImg_url(imgUrl);
				}
				dao.doUpdateObject(good);
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
	 * 查询所有菜品 店铺管理
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/goods/selectAllGoods/{shop_id}", method = RequestMethod.GET)
	public @ResponseBody Object selectAllGoods(@PathVariable("shop_id") String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getAllObject("from Goods g where g.shop_id=" + shop_id);
			if (list.size() != 0) {
				map.put("DATA", list);
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
	 * 查询一页商品
	 * 
	 * @param shop_id
	 * @param page_no
	 * @return
	 */
	@RequestMapping(value = "/goods/selectOnepageGoods", method = RequestMethod.POST)
	public @ResponseBody Object selectOnePageGoods(String shop_id, int page_no) {
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> list = dao.getOnePageResult(10, page_no, "from Goods g where g.shop_id=" + shop_id);
			if(null!=list&&list.size()>0){
				for (Object object : list) {
					Goods g = (Goods)object;
					GoodsCategory gc = (GoodsCategory)dao.getOneObject("from GoodsCategory gc where gc.category_id="+g.getCategory_id());
					g.setCtg(gc);
					String print_Str = g.getPrint_str();
					if(null!=print_Str){
						String [] array = print_Str.split(",");
						StringBuffer buffer = new StringBuffer();
						List<Object> printer_list = dao.getAllObject("from ShopPrinter sp where sp.shop_id="+shop_id);
						for (String string : array) {
							ShopPrinter sp = (ShopPrinter)dao.getOneObject("from ShopPrinter sp where sp.printer_no='"+string+"' and sp.shop_id="+shop_id);
							if(null!=sp){
								buffer.append(sp.getPrinter_name()+",");
							}
						}
						g.setPrinter_list(printer_list);
						if(buffer.length()>0){
							g.setPrint_name_str(buffer.substring(0, buffer.length()-1));
						}
					}
				}
			}
			int total = dao.getAllNumber("from Goods g where g.shop_id=" + shop_id);
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
	 * 修改是否打印
	 * @param goods_id
	 * @param if_print
	 * @return
	 */
	@RequestMapping(value="/goods/ifPrintChange",method=RequestMethod.POST)
	public @ResponseBody Object doIfPrintChange(String goods_id,int if_print){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Goods g =(Goods)dao.getOneObject("from Goods g where g.good_id="+goods_id);
			if(g!=null){
				g.setIf_print(if_print);
				dao.doUpdateObject(g);
			}else{
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
	 * 修改打印机
	 * @param goods_id
	 * @param printer_str
	 * @return
	 */
	@RequestMapping(value="/goods/printChange",method=RequestMethod.POST)
	public @ResponseBody Object doPrinterChange(String goods_id,String printer_str){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Goods g =(Goods)dao.getOneObject("from Goods g where g.good_id="+goods_id);
			if(g!=null){
				if(null==printer_str||printer_str.equals("")){
					g.setPrint_str("");
				}else{
					g.setPrint_str(printer_str);
				}
				dao.doUpdateObject(g);
			}else{
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
	 * 修改推荐
	 * @return
	 */
	@RequestMapping(value="/goods/tuijianChange",method=RequestMethod.POST)
	public @ResponseBody Object doChangeTuijian(String goods_id,String tuijian){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isEmpty(goods_id)){
			code = "1001";
			message = "参数异常";
		}else{
			try {
				Goods goods = (Goods)dao.getOneObject("from Goods g where g.good_id="+goods_id);
				if(null!=goods){
					if(null==tuijian){
						tuijian = "0";
					}
					goods.setTuijian(tuijian);
					dao.doUpdateObject(goods);
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
