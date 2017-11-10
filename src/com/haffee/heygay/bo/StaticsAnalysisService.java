package com.haffee.heygay.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haffee.heygay.dao.IUserDao;

import cn.jiguang.common.utils.StringUtils;


@Controller
public class StaticsAnalysisService {

	@Autowired
	private IUserDao dao;

	/**
	 * 菜品销量统计
	 * @param time_flag 1：当天，2：近一周，3：近一月(默认) 
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param shop_id 店铺ID
	 * @return
	 */
	@RequestMapping(value="/statics/goodsStatics",method = RequestMethod.POST)
	public @ResponseBody Object goodsSalesStatic(String time_flag, String start_time, String end_time,
			String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		
		String sql = "select a.good_name,count(a.good_num)  zs,sum(a.good_total_price)  ze from ShoppingCartGoods a,ShoppingCart b where b.shop_id="+shop_id+" and a.cart_id = b.cart_id";
        StringBuffer sql2 = new StringBuffer();
		List<Object> objs = new LinkedList<Object>();
		sql2.append(sql);
		if(start_time != null&&end_time!=null&&!start_time.equals("")&&!end_time.equals("")){
			sql2.append(" and  date(b.create_time) between ? and ? ");
			objs.add(start_time);
			objs.add(end_time);
		}else{
			if (!StringUtils.isEmpty(time_flag)) {
				if(time_flag.equals("1")){
					sql2.append(" and to_days(b.create_time) = to_days(now())");
				}else if(time_flag.equals("2")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(b.create_time)");
				}else if(time_flag.equals("3")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(b.create_time)");
				}
			}else{
				sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(b.create_time)");
			}
		}
		sql2.append(" group by a.good_name order by ze desc");
		List<Object> list = dao.findBySqlnew2(10, 1,sql2.toString(),objs.toArray());
		map.put("DATA", list);
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	/**
	 * 总销量统计
	 * @param time_flag 1：当天，2：近一周，3：近一月(默认) 
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param shop_id 店铺ID
	 * @return
	 */
	@RequestMapping(value="/statics/shopStatics",method = RequestMethod.POST)
	public @ResponseBody Object shopStatics(String time_flag, String start_time, String end_time,
			String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		
		String sql = "select count(a.good_num)  zs,sum(a.good_total_price)  ze from ShoppingCartGoods a,ShoppingCart b where b.shop_id="+shop_id+" and a.cart_id = b.cart_id";
        StringBuffer sql2 = new StringBuffer();
		List<Object> objs = new LinkedList<Object>();
		sql2.append(sql);
		if(start_time != null&&end_time!=null&&!start_time.equals("")&&!end_time.equals("")){
			sql2.append(" and  date(b.create_time) between ? and ? ");
			objs.add(start_time);
			objs.add(end_time);
		}else{
			if (!StringUtils.isEmpty(time_flag)) {
				if(time_flag.equals("1")){
					sql2.append(" and to_days(b.create_time) = to_days(now())");
				}else if(time_flag.equals("2")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(b.create_time)");
				}else if(time_flag.equals("3")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(b.create_time)");
				}
			}else{
				sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(b.create_time)");
			}
		}
		sql2.append("  order by ze desc");
		List<Object> list = dao.findBySqlnew2(10, 1,sql2.toString(),objs.toArray());
		map.put("DATA", list);
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
	
	/**
	 * 服务员销量统计
	 * @param time_flag 1：当天，2：近一周，3：近一月(默认) 
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param shop_id 店铺ID
	 * @return
	 */
	@RequestMapping(value="/statics/waiterStatics",method = RequestMethod.POST)
	public @ResponseBody Object waitStatics(String time_flag, String start_time, String end_time,
			String shop_id) {
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		
		String sql = "select (select name from Waiter where waiter_id = b.waiter_id) name, b.waiter_id,count(distinct(b.cart_id))  zs,sum(a.good_total_price)  ze from ShoppingCartGoods a,ShoppingCart b where b.shop_id="+shop_id+" and a.cart_id = b.cart_id";
        StringBuffer sql2 = new StringBuffer();
		List<Object> objs = new LinkedList<Object>();
		sql2.append(sql);
		if(start_time != null&&end_time!=null&&!start_time.equals("")&&!end_time.equals("")){
			sql2.append(" and  date(b.create_time) between ? and ? ");
			objs.add(start_time);
			objs.add(end_time);
		}else{
			if (!StringUtils.isEmpty(time_flag)) {
				if(time_flag.equals("1")){
					sql2.append(" and to_days(b.create_time) = to_days(now())");
				}else if(time_flag.equals("2")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(b.create_time)");
				}else if(time_flag.equals("3")){
					sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(b.create_time)");
				}
			}else{
				sql2.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(b.create_time)");
			}
		}
		sql2.append(" GROUP BY  b.waiter_id order by ze desc");
		List<Object> list = dao.findBySqlnew2(10, 1,sql2.toString(),objs.toArray());
		map.put("DATA", list);
		map.put("CODE", code);
		map.put("MESSAGE", message);
		return map;
	}
//	今天
//	select * from 表名 where to_days(时间字段名) = to_days(now());
//	昨天
//	SELECT * FROM 表名 WHERE TO_DAYS( NOW( ) ) - TO_DAYS( 时间字段名) <= 1
//	7天
//	SELECT * FROM 表名 where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(时间字段名)
//
//	近30天
//	SELECT * FROM 表名 where DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(时间字段名)
//	本月
//	SELECT * FROM 表名 WHERE DATE_FORMAT( 时间字段名, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )
//	上一月
//	SELECT * FROM 表名 WHERE PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( 时间字段名, '%Y%m' ) ) =1
	
	/**
	 * service统计
	 * @param time_flag
	 * @param start_time
	 * @param end_time
	 * @param shop_id
	 * @return
	 */
	@RequestMapping(value="/statics/serviceStatics",method = RequestMethod.POST)
	public @ResponseBody Object serviceStatics(String time_flag, String start_time, String end_time,String shop_id){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();;
		try {
			StringBuffer service_buffer = new StringBuffer();
			StringBuffer order_buffer = new StringBuffer();
			StringBuffer order_check_buffer = new StringBuffer();
			service_buffer.append("SELECT COUNT(1) SERVICE_COUNT FROM Service s WHERE s.SHOP_ID="+shop_id);
			order_buffer.append("SELECT COUNT(1) ORDER_COUNT FROM OrderInfo o WHERE o.SHOP_ID="+shop_id);
			order_check_buffer.append("SELECT COUNT(1) ORDER_CHECK_COUNT FROM OrderInfo o WHERE SHOP_ID="+shop_id+" AND o.STATUS=1 ");
			
			List<Object> objs = new LinkedList<Object>();
			if(start_time != null&&end_time!=null&&!start_time.equals("")&&!end_time.equals("")){
				service_buffer.append(" and  date(s.create_time) between ? and ? ");
				order_buffer.append(" and  date(o.create_time) between ? and ? ");
				order_check_buffer.append(" and  date(o.create_time) between ? and ? ");
				objs.add(start_time);
				objs.add(end_time);
			}else{
				if (!StringUtils.isEmpty(time_flag)) {
					if(time_flag.equals("1")){
						service_buffer.append(" and to_days(s.create_time) = to_days(now())");
						order_buffer.append(" and to_days(o.create_time) = to_days(now())");
						order_check_buffer.append(" and to_days(o.create_time) = to_days(now())");
					}else if(time_flag.equals("2")){
						service_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(s.create_time)");
						order_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(o.create_time)");
						order_check_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(o.create_time)");
					}else if(time_flag.equals("3")){
						service_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(s.create_time)");
						order_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(o.create_time)");
						order_check_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(o.create_time)");
					}
				}else{
					service_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(s.create_time)");
					order_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(o.create_time)");
					order_check_buffer.append(" and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(o.create_time)");
				}
			}
			List<Object> list = dao.findBySqlnew2(10, 1,service_buffer.toString(),objs.toArray());
			List<Object> order_list = dao.findBySqlnew2(10, 1,order_buffer.toString(),objs.toArray());
			if(order_list.size()>0){
				list.add(order_list.get(0));
			}
			List<Object> order_check_list = dao.findBySqlnew2(10, 1,order_check_buffer.toString(),objs.toArray());
			if(order_check_list.size()>0){
				list.add(order_check_list.get(0));
			}
			map.put("DATA", list);
			//map.put("ORDER_DATA", order_list);
			//map.put("ORDER_CHECK_DATA", order_check_list);
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
	 * 超级管理员主页统计
	 * @return
	 */
	@RequestMapping(value="/statics/homeStatics",method = RequestMethod.GET)
	public @ResponseBody Object homeStatics(){
		String message = "成功";
		String code = "1000";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Object> objs = new LinkedList<Object>();
			
			StringBuffer shop_count_buffer = new StringBuffer("SELECT COUNT(1) SHOP_COUNT FROM Shop ");
			List<Object> shop_count_list = dao.findBySqlnew2(10, 1,shop_count_buffer.toString(),objs.toArray());
			map.put("SHOP_COUNT", shop_count_list);
			
			StringBuffer table_count_buffer = new StringBuffer("SELECT COUNT(1) TABLE_COUNT FROM TableInfo");
			List<Object> table_count_list = dao.findBySqlnew2(10, 1,table_count_buffer.toString(),objs.toArray());
			map.put("TABLE_COUNT", table_count_list);
			
			StringBuffer order_count_buffer = new StringBuffer("SELECT COUNT(1) ORDER_COUNT FROM OrderInfo o where o.status=1");
			List<Object> order_count_list = dao.findBySqlnew2(10, 1,order_count_buffer.toString(),objs.toArray());
			map.put("ORDER_COUNT", order_count_list);
			
			Date d = new Date();  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	        String dateNowStr = sdf.format(d); 
			StringBuffer order_count_buffer_today = new StringBuffer("SELECT COUNT(1) ORDER_COUNT_TODAY FROM OrderInfo o where o.status=1 and o.create_time > '"+dateNowStr+"'");
			List<Object> order_count_today_list = dao.findBySqlnew2(10, 1,order_count_buffer_today.toString(),objs.toArray());
			map.put("ORDER_COUNT_TODAY", order_count_today_list);
			
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
