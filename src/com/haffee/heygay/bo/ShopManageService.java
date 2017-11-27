package com.haffee.heygay.bo;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Shop;
import com.haffee.heygay.po.User;
import com.haffee.heygay.util.QRCodeUtil;

import cn.jiguang.common.utils.StringUtils;

/**
 * 店铺管理 适用于店铺管理员
 * 
 * @author jacktong
 *
 */
@Controller
public class ShopManageService {

	@Autowired
	private IUserDao dao;

	/**
	 * 查询店铺信息
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/shop/getShopInfo/{shop_id}", method = RequestMethod.GET)
	public @ResponseBody Object selectShop(@PathVariable("shop_id") String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Object o = dao.getOneObject("from Shop s where s.shop_id=" + shop_id);
			if (null != o) {
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
	 * 更新店铺信息
	 * 
	 * @param shop
	 * @return
	 */
	@RequestMapping(value = "/shop/updateShop", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateShop(Shop shop,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "wechat_img_file", required = false) MultipartFile wechat_img_file,
			@RequestParam(value = "alipay_img_file", required = false) MultipartFile alipay_img_file,
			HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Shop s = (Shop) dao.getOneObject("from Shop s where s.shop_id=" + shop.getShop_id());
			if (null != s) {
				s.setAddress(shop.getAddress());
				s.setBegin_time(shop.getBegin_time());
				s.setEnd_time(shop.getEnd_time());
				s.setIcon(shop.getIcon());
				s.setBegin_time(shop.getBegin_time());
				s.setEnd_time(shop.getEnd_time());
				s.setIntroduction(shop.getIntroduction());
				s.setNotice(shop.getNotice());
				s.setShop_name(shop.getShop_name());
				s.setShop_owner_name(shop.getShop_owner_name());
				s.setShop_owner_phone(shop.getShop_owner_phone());
				// 2.上传图片
				String path = request.getSession().getServletContext().getRealPath("shop_" + s.getShop_id());
				// String fileName = file.getOriginalFilename();
				SimpleDateFormat simpleDateFormat;
				simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				String str = simpleDateFormat.format(date);
				String fileName = str + (int) (Math.random() * 10000000) + ".jpg";
				String wechat_pay = "wechat_pay.jpg";
				String alipay_pay = "alipay_pay.jpg";
				File targetFile = new File(path, fileName);
				File targetFile_wechat = new File(path, wechat_pay);
				File targetFile_alipay = new File(path, alipay_pay);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				if (!targetFile_wechat.exists()) {
					targetFile_wechat.mkdirs();
				}
				if (!targetFile_alipay.exists()) {
					targetFile_alipay.mkdirs();
				}
				try {
					file.transferTo(targetFile);
					wechat_img_file.transferTo(targetFile_wechat);
					alipay_img_file.transferTo(targetFile_alipay);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String imgUrl = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + fileName;
				String wechat_pay_img = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + wechat_pay;
				String alipay_pay_img = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + alipay_pay;
				if (file != null) {
					s.setIcon(imgUrl);
				}
				if(null!=wechat_img_file){
					s.setWechat_img(wechat_pay_img);
				}
				if(null!=alipay_img_file){
					s.setAlipay_img(alipay_pay_img);
				}
				dao.doUpdateObject(s);
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
	 * 更新店铺信息
	 * 
	 * @param shop
	 * @return
	 */
	@RequestMapping(value = "/shop/updateShop2", method = RequestMethod.POST)
	public @ResponseBody Object doUpdateShop2(Shop shop,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "wechat_img_file", required = false) MultipartFile wechat_img_file,
			@RequestParam(value = "alipay_img_file", required = false) MultipartFile alipay_img_file,
			HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Shop s = (Shop) dao.getOneObject("from Shop s where s.shop_id=" + shop.getShop_id());
			if (null != s) {
				s.setAddress(shop.getAddress());
				s.setBegin_time(shop.getBegin_time());
				s.setEnd_time(shop.getEnd_time());
				//s.setIcon(shop.getIcon());
				s.setBegin_time(shop.getBegin_time());
				s.setEnd_time(shop.getEnd_time());
				s.setIntroduction(shop.getIntroduction());
				s.setNotice(shop.getNotice());
				s.setShop_name(shop.getShop_name());
				s.setShop_owner_name(shop.getShop_owner_name());
				s.setShop_owner_phone(shop.getShop_owner_phone());
				s.setPrint_way(shop.getPrint_way());
				// 2.上传图片
				String path = request.getSession().getServletContext().getRealPath("shop_" + s.getShop_id());
				// String fileName = file.getOriginalFilename();
				SimpleDateFormat simpleDateFormat;
				simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				String str = simpleDateFormat.format(date);
				String fileName = str + (int) (Math.random() * 10000000) + ".jpg";
				String wechat_pay = "wechat_pay.jpg";
				String alipay_pay = "alipay_pay.jpg";
				File targetFile = new File(path, fileName);
				File targetFile_wechat = new File(path, wechat_pay);
				File targetFile_alipay = new File(path, alipay_pay);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				if (!targetFile_wechat.exists()) {
					targetFile_wechat.mkdirs();
				}
				if (!targetFile_alipay.exists()) {
					targetFile_alipay.mkdirs();
				}
				try {
					if(null!=file){
						file.transferTo(targetFile);
					}
					if(null!=wechat_img_file){
						wechat_img_file.transferTo(targetFile_wechat);
					}
					if(null!=alipay_img_file){
						alipay_img_file.transferTo(targetFile_alipay);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				String imgUrl = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + fileName;
				String wechat_pay_img = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + wechat_pay;
				String alipay_pay_img = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + alipay_pay;
				if (file != null) {
					s.setIcon(imgUrl);
				}
				if(null!=wechat_img_file){
					s.setWechat_img(wechat_pay_img);
				}
				if(null!=alipay_img_file){
					s.setAlipay_img(alipay_pay_img);
				}
				dao.doUpdateObject(s);
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
	 * 新增店铺
	 * 
	 * @param shop
	 * @return
	 */
	@RequestMapping(value = "/shop/addShop", method = RequestMethod.POST)
	public @ResponseBody Object doAddShop(Shop shop, @RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "wechat_img_file", required = false) MultipartFile wechat_img_file,
			@RequestParam(value = "alipay_img_file", required = false) MultipartFile alipay_img_file,
			HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		User u = (User) dao.getOneObject("from User u where u.user_name='"+ shop.getUser_name()+"'");
		if(null!=u){
			code = "1002";
			message = "用户名已经存在";
		}else{
			if (StringUtils.isNotEmpty(shop.getShop_name())) {
				// 获得文件：
				try {
					Date date = new Date();
					shop.setCreate_time(new Timestamp(date.getTime()));
					shop.setShop_status(1);
					shop.setPrint_way(1);
					dao.doSaveObject(shop);
					Shop s = (Shop) dao.getOneObject("from Shop s where s.shop_name='" + shop.getShop_name() + "'");
					// 1.创建User
					User user = new User();
					user.setUser_name(shop.getUser_name());
					user.setPassword("000000");// 默认6个0
					user.setUser_status(1);
					user.setType(1);
					user.setRole_id(1);
					user.setBusiness_id(s.getShop_id());
					user.setCreate_time(new Timestamp(date.getTime()));
					dao.doSaveObject(user);
					// 2.上传图片
					String path = request.getSession().getServletContext().getRealPath("shop_" + s.getShop_id());
					// String fileName = file.getOriginalFilename();
					SimpleDateFormat simpleDateFormat;
					simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					String str = simpleDateFormat.format(date);
					String fileName = str + (int) (Math.random() * 10000000) + ".jpg";
					String wechat_pay = "wechat_pay.jpg";
					String alipay_pay = "alipay_pay.jpg";
					File targetFile = new File(path, fileName);
					File targetFile_wechat = new File(path, wechat_pay);
					File targetFile_alipay = new File(path, alipay_pay);
					if (!targetFile.exists()) {
						targetFile.mkdirs();
					}
					if (!targetFile_wechat.exists()) {
						targetFile_wechat.mkdirs();
					}
					if (!targetFile_alipay.exists()) {
						targetFile_alipay.mkdirs();
					}
					try {
						file.transferTo(targetFile);
						if(null!=wechat_img_file){
							wechat_img_file.transferTo(targetFile_wechat);
						}
						if(null!=alipay_img_file){
							alipay_img_file.transferTo(targetFile_alipay);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					String imgUrl = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + fileName;
					String wechat_pay_img = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + wechat_pay;
					String alipay_pay_img = request.getContextPath() + "/shop_" + s.getShop_id() + "/" + alipay_pay;
					s.setIcon(imgUrl);
					if(null!=wechat_img_file){
						s.setWechat_img(wechat_pay_img);
					}
					if(null!=alipay_img_file){
						s.setAlipay_img(alipay_pay_img);
					}
					// 3.更新shop图片
					dao.doUpdateObject(s);//新增店铺BUG
					String logo = "";
					if (null != s) {
						logo = s.getIcon();
					}
					String path_logo = request.getSession().getServletContext().getRealPath("shop_" + s.getShop_id());
					if(null!=logo){
						logo = path_logo+logo.replace("/shoptong/shop_"+s.getShop_id(), "");
					}
					QRCodeUtil.encode(
							"http://39.106.117.141:3521/users?shop_id=" + s.getShop_id(), logo,
							path, true, "qrcode_" + s.getShop_id());
				} catch (Exception e) {
					code = "1002";
					message = "系统异常";
					e.printStackTrace();
				}
			} else {
				code = "1002";
				message = "参数异常";
			}
		}
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}

	/**
	 * 分页查询服务员 查询店铺信息
	 * 
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value = "/shop/getShopInfoall", method = RequestMethod.POST)
	public @ResponseBody Object selectAllWaiter(String shop_name, int page_no) {
		if (page_no == 0) {
			page_no = 1;
		}
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String sql = "from Shop s ";
			if (!shop_name.equals("")) {
				sql = sql + "where s.shop_name= '" + shop_name + "'";
			}
			List<Object> list = dao.getOnePageResult(10, page_no, sql);

			int total = dao.getAllNumber(sql);
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
	 * 删除商铺
	 * 
	 * @param g
	 * @return
	 */
	@RequestMapping(value = "/shops/deleteShops", method = RequestMethod.POST)
	public @ResponseBody Object doDelete(Shop s) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			dao.doDeleteObject(s);
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
	 * 更新店铺信息
	 * 
	 * @param shop
	 * @return
	 */
	@RequestMapping(value = "/shop/updatePassword", method = RequestMethod.POST)
	public @ResponseBody Object doUpdatePassword(String oldpassword, String password, String passwordnew,
			String shop_id, HttpServletRequest request) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			User u = (User) dao.getOneObject("from User u where u.business_id=" + shop_id);
			if (null != u) {
				if (!oldpassword.equals(u.getPassword())) {
					code = "1001";
					message = "原密码不正确";
				} else {
					if (!password.equals(passwordnew)) {
						code = "1001";
						message = "确认密码和新密码不一致";
					} else {
						u.setPassword(passwordnew);
						dao.doUpdateObject(u);
					}
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

}
