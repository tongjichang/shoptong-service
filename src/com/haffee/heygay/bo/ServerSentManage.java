package com.haffee.heygay.bo;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.haffee.heygay.dao.IUserDao;
import com.haffee.heygay.po.Service;
import com.haffee.heygay.po.ShoppingCart;
import com.haffee.heygay.po.ShoppingCartAdd;

import net.sf.json.JSONObject;

/**
 * 推送
 * 
 * @author jacktong
 * @date 2017年7月4日
 *
 */
@Controller
public class ServerSentManage {

	@Autowired
	private IUserDao dao;

	/**
	 * 桌面service通知
	 * 
	 * @param request
	 * @param response
	 * @param shop_id
	 */
	@RequestMapping(value = "/notice/shopNotice/{shop_id}", method = RequestMethod.GET)
	public void selectService(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("shop_id") String shop_id) {
		OutputStream bos = null;
		try {
			// 声明浏览器在连接断开之后进行再次连接之前的等待时间 10秒
			String retry = "retry:" + 5000 + "\n";
			// 事件的标识符
			// 最后一次接收到的事件的标识符
			// String last = request.getHeader("Last-Event-ID");
			StringBuffer buffer = new StringBuffer();
			buffer.append("from Service s where s.shop_id=" + shop_id + "");
			buffer.append(" and s.if_notice='0'");
			Service service = (Service) dao.getOneObject(buffer.toString());
			if(null!=service){
				service.setIf_notice("1");
				dao.doUpdateObject(service);
			}
			response.setContentType("text/event-stream");// 记得要设置哦
			bos = new BufferedOutputStream(response.getOutputStream());
			if (null != service) {
				String id = "id:" + service.getService_id() + "\n";
				String event = "event:serviceNotice\n\n";
				JSONObject json = JSONObject.fromObject(service);
				String data = "data:" + json.toString() + "\n";
				bos.write(id.getBytes());
				bos.write(data.getBytes());
				bos.write(retry.getBytes());
				bos.write(event.getBytes());
				bos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 加载购物车
	 * @param shop_id
	 * @param table_id
	 */
	@RequestMapping(value="/notice/loadShppingCart",method=RequestMethod.GET)
	public void selectShoppingCart(String shop_id,String table_id,HttpServletResponse response){
		OutputStream bos = null;
		try {
			response.setContentType("text/event-stream");// 记得要设置哦
			bos = new BufferedOutputStream(response.getOutputStream());
			ShoppingCart sc = (ShoppingCart) dao.getOneObject(
					"from ShoppingCart s where s.table_id=" + table_id + " and s.status = 0 and s.shop_id=" + shop_id);
			if (sc != null) {
				List<Object> list = dao.getAllObject("from ShoppingCartGoods scg where scg.cart_id=" + sc.getCart_id());
				sc.setGoods_set(list);
				String id = "id:" + sc.getCart_id() + "\n";
				String event = "event:cartNotice\n\n";
				JSONObject json = JSONObject.fromObject(sc);
				String data = "data:" + json.toString() + "\n";
				bos.write(id.getBytes());
				bos.write(data.getBytes());
				bos.write(event.getBytes());
				bos.flush();
			} else {
				ShoppingCartAdd sc_add = (ShoppingCartAdd) dao.getOneObject("from ShoppingCartAdd s where s.table_id="
						+ table_id + " and s.status = 0 and s.shop_id=" + shop_id);
				if (sc_add != null) {
					List<Object> list = dao
							.getAllObject("from ShoppingCartGoodsAdd scg where scg.cart_id=" + sc_add.getCart_id());
					sc_add.setGoods_set(list);
					String id = "id:" + sc_add.getCart_id() + "\n";
					String event = "event:cartNotice\n\n";
					JSONObject json = JSONObject.fromObject(sc_add);
					String data = "data:" + json.toString() + "\n";
					bos.write(id.getBytes());
					bos.write(data.getBytes());
					bos.write(event.getBytes());
					bos.flush();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
