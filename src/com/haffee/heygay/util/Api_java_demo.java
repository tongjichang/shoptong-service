package com.haffee.heygay.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.haffee.heygay.po.Area;
import com.haffee.heygay.po.OrderInfo;
import com.haffee.heygay.po.ShopPrinter;
import com.haffee.heygay.po.ShoppingCartGoods;
import com.haffee.heygay.po.ShoppingCartGoodsAdd;
import com.haffee.heygay.po.ShoppingCartGoodsBack;
import com.haffee.heygay.po.TableInfo;
import com.haffee.heygay.po.Waiter;

public class Api_java_demo implements Runnable {

	public static final String URL = "http://api.feieyun.cn/Api/Open/";// 不需要修改
	public static final String USER = "459132449@qq.com";// *必填*：账号名
	public static final String UKEY = "MRXbnZvCuXBjt7XY";// *必填*: 注册账号后生成的UKEY
	// public static final String SN =
	// "xxxxxxxxx";//*必填*：打印机编号，必须要在管理后台里添加打印机或调用API接口添加之后，才能调用API

	// @Autowired
//	private static IUserDao dao = null;
//
//	private static ApplicationContext ctx = null;
//	static {
//		ctx = new ClassPathXmlApplicationContext("classpath:/dispatcher-servlet.xml");
//		dao = (IUserDao) ctx.getBean("dao");
//	}
	private String printer;
	private OrderInfo o;
	private Waiter w;
	private List<Object> goods_list;
	private List<Object> goods_add_list;
	private String flag; // 1: 后厨打印，2：结账打印

	private List<Object> printers;
	private TableInfo table;
	private Area area;
	private String print_no;
	private String content;
	private int print_way;
	private ShoppingCartGoodsBack back_cart_goods;
	

	public String getPrinter() {
		return printer;
	}


	public void setPrinter(String printer) {
		this.printer = printer;
	}


	public OrderInfo getO() {
		return o;
	}


	public void setO(OrderInfo o) {
		this.o = o;
	}


	public Waiter getW() {
		return w;
	}


	public void setW(Waiter w) {
		this.w = w;
	}


	public List<Object> getGoods_list() {
		return goods_list;
	}


	public void setGoods_list(List<Object> goods_list) {
		this.goods_list = goods_list;
	}


	public List<Object> getGoods_add_list() {
		return goods_add_list;
	}


	public void setGoods_add_list(List<Object> goods_add_list) {
		this.goods_add_list = goods_add_list;
	}


	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}


	public List<Object> getPrinters() {
		return printers;
	}


	public void setPrinters(List<Object> printers) {
		this.printers = printers;
	}


	public TableInfo getTable() {
		return table;
	}


	public void setTable(TableInfo table) {
		this.table = table;
	}


	public Area getArea() {
		return area;
	}


	public void setArea(Area area) {
		this.area = area;
	}


	public String getPrint_no() {
		return print_no;
	}


	public void setPrint_no(String print_no) {
		this.print_no = print_no;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public static String getUrl() {
		return URL;
	}


	public static String getUser() {
		return USER;
	}


	public static String getUkey() {
		return UKEY;
	}


	public int getPrint_way() {
		return print_way;
	}


	public void setPrint_way(int print_way) {
		this.print_way = print_way;
	}


	public ShoppingCartGoodsBack getBack_cart_goods() {
		return back_cart_goods;
	}


	public void setBack_cart_goods(ShoppingCartGoodsBack back_cart_goods) {
		this.back_cart_goods = back_cart_goods;
	}


	public Api_java_demo() {

	}

	
	public Api_java_demo(String flag, String print_no, String content) {
		super();
		this.flag = flag;
		this.print_no = print_no;
		this.content = content;
	}


	public Api_java_demo(String shopid, String printer, OrderInfo o, Waiter w, String flag) {
		this.printer = printer;
		this.o = o;
		this.w = w;
		this.flag = flag;
	}

	public Api_java_demo(String shopid, List<Object> goods_list, List<Object> goods_add_list, String table_id,
			String flag, List<Object> printers, TableInfo table, Area area,OrderInfo order,int print_way) {
		this.goods_list = goods_list;
		this.goods_add_list = goods_add_list;
		this.flag = flag;
		this.printers = printers;
		this.table = table;
		this.area = area;
		this.o = order;
		this.print_way = print_way;
	}

	// **********测试时，打开下面注释掉方法的即可,更多接口文档信息,请访问官网开放平台查看**********
	public static void main(String[] args) throws Exception {

		// ==================添加打印机接口（支持批量）==================
		// ***返回值JSON字符串***
		// 正确例子：{"msg":"ok","ret":0,"data":{"ok":["sn#key#remark#carnum","316500011#abcdefgh#快餐前台"],"no":["316500012#abcdefgh#快餐前台#13688889999
		// （错误：识别码不正确）"]},"serverExecutedTime":3}
		// 错误：{"msg":"参数错误 :
		// 该帐号未注册.","ret":-2,"data":null,"serverExecutedTime":37}

		// 提示：打印机编号(必填) # 打印机识别码(必填) # 备注名称(选填) #
		// 流量卡号码(选填)，多台打印机请换行（\n）添加新打印机信息，每次最多100行(台)。
		String snlist = "217504405#qr4anfq5#测试机";
		String method = addprinter(snlist);
		System.out.println(method);

		// ==================方法1.打印订单==================
		// ***返回值JSON字符串***
		// 成功：{"msg":"ok","ret":0,"data":"xxxxxxx_xxxxxxxx_xxxxxxxx","serverExecutedTime":5}
		// 失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}

		// String method1 = print(SN);
		// System.out.println(method1);

		// ===========方法2.查询某订单是否打印成功=============
		// ***返回值JSON字符串***
		// 成功：{"msg":"ok","ret":0,"data":true,"serverExecutedTime":2}//data:true为已打印,false为未打印
		// 失败：{"msg":"错误描述","ret":非0, "data":null,"serverExecutedTime":7}

		// String orderid = "xxxxxxx_xxxxxxxx_xxxxxxxx";//订单ID，从方法1返回值data获取
		// String method2 = queryOrderState(orderid);
		// System.out.println(method2);

		// ===========方法3.查询指定打印机某天的订单详情============
		// ***返回值JSON字符串***
		// 成功：{"msg":"ok","ret":0,"data":{"print":6,"waiting":1},"serverExecutedTime":9}//print已打印，waiting为打印
		// 失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}

		// String strdate = "2016-11-12";//注意时间格式为"yyyy-MM-dd"
		// String method3 = queryOrderInfoByDate(SN,strdate);
		// System.out.println(method3);

		// ===========方法4.查询打印机的状态==========================
		// ***返回值JSON字符串***
		// 成功：{"msg":"ok","ret":0,"data":"状态","serverExecutedTime":4}
		// 失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}

		// String method4 = queryPrinterStatus(SN);
		// System.out.println(method4);

	}

	// =====================以下是函数实现部分================================================

	private static String addprinter(String snlist) {

		// 通过POST请求，发送打印信息到服务器
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)// 读取超时
				.setConnectTimeout(30000)// 连接超时
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		HttpPost post = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", USER));
		String STIME = String.valueOf(System.currentTimeMillis() / 1000);
		nvps.add(new BasicNameValuePair("stime", STIME));
		nvps.add(new BasicNameValuePair("sig", signature(USER, UKEY, STIME)));
		nvps.add(new BasicNameValuePair("apiname", "Open_printerAddlist"));// 固定值,不需要修改
		nvps.add(new BasicNameValuePair("printerContent", snlist));

		CloseableHttpResponse response = null;
		String result = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			response = httpClient.execute(post);
			int statecode = response.getStatusLine().getStatusCode();
			if (statecode == 200) {
				HttpEntity httpentity = response.getEntity();
				if (httpentity != null) {
					result = EntityUtils.toString(httpentity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				post.abort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	/**
	 * 结账打印
	 * 
	 * @return
	 */
	public String check_print() {
		SimpleDateFormat simpleDateFormat;
		simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
		Date date = new Date();
		String str = simpleDateFormat.format(date);
		// 标签说明：
		// 单标签:
		// "<BR>"为换行,"<CUT>"为切刀指令(主动切纸,仅限切刀打印机使用才有效果)
		// "<LOGO>"为打印LOGO指令(前提是预先在机器内置LOGO图片),"<PLUGIN>"为钱箱或者外置音响指令
		// 成对标签：
		// "<CB></CB>"为居中放大一倍,"<B></B>"为放大一倍,"<C></C>"为居中,<L></L>字体变高一倍
		// <W></W>字体变宽一倍,"<QR></QR>"为二维码,"<BOLD></BOLD>"为字体加粗,"<RIGHT></RIGHT>"为右对齐
		// 拼凑订单内容时可参考如下格式
		// 根据打印纸张的宽度，自行调整内容的格式，可参考下面的样例格式

		String content;
		content = "<CB>商品小票</CB><BR>";
		content += "--------------------------------<BR>";
		//content += "桌号：" + o.getArea().getArea_name()+" "+o.getTable().getTable_name() + "  人数:" + o.getPeople_count() + " 服务员:" + w.getName() + "<BR>";
		content += "账单编号：" + o.getOrder_num() + "<BR>";
		content += "时间：" + str + "<BR>";
		content += "--------------------------------<BR>";
		content += "名称　　单价   数量   金额<BR>";
		content += "--------------------------------<BR>";
		for (Object object : o.getCart().getGoods_set()) {
			if(object instanceof ShoppingCartGoods){
				ShoppingCartGoods s = (ShoppingCartGoods) object;
				content += s.getGood_name()+"<BR>";
				content +=  "　	    " + s.getGood_price() + "   " + s.getGood_num() + "      "
						+ s.getGood_total_price() + "<BR>";
			}
		}
		content += "							总计:" + o.getPayment() + "<BR>";
		content += "--------------------------------<BR>";
		// content += "名称 金额";
		// content += "--------------------------------<BR>";
		content += "应收金额:" + o.getPayment() + "<BR>";
		//content += "折扣后金额:" + o.getDiscount_payment() + "<BR>";
		//content += "抹零:" + o.getMinus_money() + "<BR>";
		//content += "实收金额:" + o.getReal_payment() + "<BR>";
		content += "--------------------------------<BR>";

		// 通过POST请求，发送打印信息到服务器
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)// 读取超时
				.setConnectTimeout(30000)// 连接超时
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		HttpPost post = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", USER));
		String STIME = String.valueOf(System.currentTimeMillis() / 1000);
		nvps.add(new BasicNameValuePair("stime", STIME));
		nvps.add(new BasicNameValuePair("sig", signature(USER, UKEY, STIME)));
		nvps.add(new BasicNameValuePair("apiname", "Open_printMsg"));// 固定值,不需要修改
		nvps.add(new BasicNameValuePair("sn", printer));
		nvps.add(new BasicNameValuePair("content", content));
		nvps.add(new BasicNameValuePair("times", "1"));// 打印联数

		CloseableHttpResponse response = null;
		String result = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			response = httpClient.execute(post);
			int statecode = response.getStatusLine().getStatusCode();
			if (statecode == 200) {
				HttpEntity httpentity = response.getEntity();
				if (httpentity != null) {
					// 服务器返回的JSON字符串，建议要当做日志记录起来
					result = EntityUtils.toString(httpentity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				post.abort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 方法2
	private static String queryOrderState(String orderid) {

		// 通过POST请求，发送打印信息到服务器
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)// 读取超时
				.setConnectTimeout(30000)// 连接超时
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		HttpPost post = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", USER));
		String STIME = String.valueOf(System.currentTimeMillis() / 1000);
		nvps.add(new BasicNameValuePair("stime", STIME));
		nvps.add(new BasicNameValuePair("sig", signature(USER, UKEY, STIME)));
		nvps.add(new BasicNameValuePair("apiname", "Open_queryOrderState"));// 固定值,不需要修改
		nvps.add(new BasicNameValuePair("orderid", orderid));

		CloseableHttpResponse response = null;
		String result = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			response = httpClient.execute(post);
			int statecode = response.getStatusLine().getStatusCode();
			if (statecode == 200) {
				HttpEntity httpentity = response.getEntity();
				if (httpentity != null) {
					// 服务器返回
					result = EntityUtils.toString(httpentity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				post.abort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 方法3
	private static String queryOrderInfoByDate(String sn, String strdate) {

		// 通过POST请求，发送打印信息到服务器
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)// 读取超时
				.setConnectTimeout(30000)// 连接超时
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		HttpPost post = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", USER));
		String STIME = String.valueOf(System.currentTimeMillis() / 1000);
		nvps.add(new BasicNameValuePair("stime", STIME));
		nvps.add(new BasicNameValuePair("sig", signature(USER, UKEY, STIME)));
		nvps.add(new BasicNameValuePair("apiname", "Open_queryOrderInfoByDate"));// 固定值,不需要修改
		nvps.add(new BasicNameValuePair("sn", sn));
		nvps.add(new BasicNameValuePair("date", strdate));// yyyy-MM-dd格式

		CloseableHttpResponse response = null;
		String result = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			response = httpClient.execute(post);
			int statecode = response.getStatusLine().getStatusCode();
			if (statecode == 200) {
				HttpEntity httpentity = response.getEntity();
				if (httpentity != null) {
					// 服务器返回
					result = EntityUtils.toString(httpentity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				post.abort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 方法4
	private static String queryPrinterStatus(String sn) {

		// 通过POST请求，发送打印信息到服务器
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)// 读取超时
				.setConnectTimeout(30000)// 连接超时
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		HttpPost post = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", USER));
		String STIME = String.valueOf(System.currentTimeMillis() / 1000);
		nvps.add(new BasicNameValuePair("stime", STIME));
		nvps.add(new BasicNameValuePair("sig", signature(USER, UKEY, STIME)));
		nvps.add(new BasicNameValuePair("apiname", "Open_queryPrinterStatus"));// 固定值,不需要修改
		nvps.add(new BasicNameValuePair("sn", sn));

		CloseableHttpResponse response = null;
		String result = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			response = httpClient.execute(post);
			int statecode = response.getStatusLine().getStatusCode();
			if (statecode == 200) {
				HttpEntity httpentity = response.getEntity();
				if (httpentity != null) {
					// 服务器返回
					result = EntityUtils.toString(httpentity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				post.abort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 生成签名字符串
	private static String signature(String USER, String UKEY, String STIME) {
		String s = DigestUtils.sha1Hex(USER + UKEY + STIME);
		return s;
	}

	/**
	 * 后厨打印--一单一打
	 * 
	 * @return
	 */
	public String goods_print() {
		// 1.店铺后厨打印机查询处理
		// 2.根据菜品对应打印机打印
		String returnStr = "";
		// TableInfo table = (TableInfo) dao.getOneObject("from TableInfo t
		// where t.table_id=" + table_id);
		// Area area = (Area) dao.getOneObject("from Area a wher a.area_id=" +
		// table.getArea_id());
		// 获取后厨打印机列表
		// List<Object> printers = dao
		// .getAllObject("from ShopPrinter sp where sp.shop_id=" + shopid + "and
		// sp.type_print=2");
		for (Object object : printers) {
			boolean if_print = false;
			StringBuffer buffer = new StringBuffer();
			buffer.append("<CB>" + area.getArea_name() + "---" + table.getTable_name() + "</CB><BR>");
			buffer.append("--------------------------------<BR>");
			ShopPrinter shopprinter = (ShopPrinter) object;
			if (null != goods_list) {
				// 菜品打印机循环
				for (Object object1 : goods_list) {
					ShoppingCartGoods s = (ShoppingCartGoods) object1;
					if(s.getIf_print()==0){
						continue;
					}
					// Goods good = (Goods) dao.getOneObject("from Goods t where
					// good_id=" + s.getGood_id());
					String dyjs = s.getPrinter_no_str();
					String[] dyj = dyjs.split("\\,");
					for (int i = 0; i < dyj.length; i++) {
						if (shopprinter.getPrinter_no().equals(dyj[i])) {
							// 菜品存在打印机可打印
							buffer.append(s.getGood_name() + " X " + s.getGood_num() + "份<BR>");
							if_print = true;
						}
					}
				}
				if (if_print) {
					buffer.append("备注："+o.getComments()+"<BR>");
					String way = "";
					if(o.getWay()==2){
						way = "打包";
					}else{
						way = "堂食";
					}
					String go_goods_way = "";
					if(o.getGo_goods_way()==2){
						go_goods_way = "等待叫起";
					}else{
						go_goods_way = "做好即上";
					}
					buffer.append("就餐方式："+way+"<BR>");
					buffer.append("上菜方式:"+go_goods_way+"<BR>");
					do_goods_print(buffer.toString(), shopprinter.getPrinter_no());
				}
			}
			if (null != goods_add_list) {
				// 菜品打印机循环
				for (Object object1 : goods_list) {
					ShoppingCartGoodsAdd s = (ShoppingCartGoodsAdd) object1;
					if(s.getIf_print()==0){
						continue;
					}
					// Goods good = (Goods) dao.getOneObject("from Goods t where
					// good_id=" + s.getGood_id());
					String dyjs = s.getPrinter_no_str();
					String[] dyj = dyjs.split("\\,");
					for (int i = 0; i < dyj.length; i++) {
						if (shopprinter.getPrinter_no().equals(dyj[i])) {
							// 菜品存在打印机可打印
							buffer.append(s.getGood_name() + " X " + s.getGood_num() + "<BR>");
							if_print = true;
						}
					}
				}
				if (if_print) {
					buffer.append("备注："+o.getComments()+"<BR>");
					String way = "";
					if(o.getWay()==2){
						way = "打包";
					}else{
						way = "堂食";
					}
					String go_goods_way = "";
					if(o.getGo_goods_way()==2){
						go_goods_way = "等待叫起";
					}else{
						go_goods_way = "做好即上";
					}
					buffer.append("就餐方式："+way+"<BR>");
					buffer.append("上菜方式:"+go_goods_way+"<BR>");
					do_goods_print(buffer.toString(), shopprinter.getPrinter_no());
				}
			}

		}
		return returnStr;
	}
	
	/**
	 * 一菜一打
	 * @return
	 */
	public String goods_print_pre_good() {
		// 1.店铺后厨打印机查询处理
		// 2.根据菜品对应打印机打印
		String returnStr = "";
		for (Object object : printers) {
			boolean if_print = false;
			StringBuffer buffer = new StringBuffer();
			ShopPrinter shopprinter = (ShopPrinter) object;
			if (null != goods_list) {
				// 菜品打印机循环
				for (Object object1 : goods_list) {
					String dyjs = "";
					if(object1 instanceof ShoppingCartGoods){
						ShoppingCartGoods s = (ShoppingCartGoods) object1;
						if(s.getIf_print()==0){
							continue;
						}
						dyjs = s.getPrinter_no_str();
						String[] dyj = dyjs.split("\\,");
						for (int i = 0; i < dyj.length; i++) {
							if (shopprinter.getPrinter_no().equals(dyj[i])) {
								buffer.append("<CB>" + area.getArea_name() + "---" + table.getTable_name() + "</CB><BR>");
								buffer.append("--------------------------------<BR>");
								// 菜品存在打印机可打印
								buffer.append("<B>"+s.getGood_name() + " X " + s.getGood_num() + "份</B><BR>");
								if_print = true;
								buffer.append("备注："+o.getComments()+"<BR>");
								String way = "";
								if(o.getWay()==2){
									way = "打包";
								}else{
									way = "堂食";
								}
								String go_goods_way = "";
								if(o.getGo_goods_way()==2){
									go_goods_way = "等待叫起";
								}else{
									go_goods_way = "做好即上";
								}
								buffer.append("就餐方式："+way+"<BR>");
								buffer.append("上菜方式:"+go_goods_way+"<BR><BR><BR><BR><BR><BR><BR>");
								buffer.append("<CUT>");
							}
						}
					}
					if(object1 instanceof ShoppingCartGoodsAdd){
						ShoppingCartGoodsAdd s = (ShoppingCartGoodsAdd) object1;
						if(s.getIf_print()==0){
							continue;
						}
						dyjs = s.getPrinter_no_str();
						String[] dyj = dyjs.split("\\,");
						for (int i = 0; i < dyj.length; i++) {
							if (shopprinter.getPrinter_no().equals(dyj[i])) {
								buffer.append("<CB>" + area.getArea_name() + "---" + table.getTable_name() + "</CB><BR>");
								buffer.append("--------------------------------<BR>");
								// 菜品存在打印机可打印
								buffer.append("<B>"+s.getGood_name() + " X " + s.getGood_num() + "份</B><BR>");
								if_print = true;
								buffer.append("备注："+o.getComments()+"<BR>");
								String way = "";
								if(o.getWay()==2){
									way = "打包";
								}else{
									way = "堂食";
								}
								String go_goods_way = "";
								if(o.getGo_goods_way()==2){
									go_goods_way = "等待叫起";
								}else{
									go_goods_way = "做好即上";
								}
								buffer.append("就餐方式："+way+"<BR>");
								buffer.append("上菜方式:"+go_goods_way+"<BR><BR><BR><BR><BR><BR><BR>");
								buffer.append("<CUT>");
							}
						}
					}
				}
				if (if_print) {
					do_goods_print(buffer.toString().substring(0,buffer.length()-5), shopprinter.getPrinter_no());
				}
			}
			if (null != goods_add_list) {
				// 菜品打印机循环
				for (Object object1 : goods_add_list) {
					ShoppingCartGoodsAdd s = (ShoppingCartGoodsAdd) object1;
					if(s.getIf_print()==0){
						continue;
					}
					String dyjs = s.getPrinter_no_str();
					String[] dyj = dyjs.split("\\,");
					for (int i = 0; i < dyj.length; i++) {
						if (shopprinter.getPrinter_no().equals(dyj[i])) {
							buffer.append("<CB>" + area.getArea_name() + "---" + table.getTable_name() + "</CB><BR>");
							buffer.append("--------------------------------<BR>");
							// 菜品存在打印机可打印
							buffer.append("<B>"+s.getGood_name() + " X " + s.getGood_num() + "份</B><BR>");
							if_print = true;
							buffer.append("备注："+o.getComments()+"<BR>");
							String way = "";
							if(o.getWay()==2){
								way = "打包";
							}else{
								way = "堂食";
							}
							String go_goods_way = "";
							if(o.getGo_goods_way()==2){
								go_goods_way = "等待叫起";
							}else{
								go_goods_way = "做好即上";
							}
							buffer.append("就餐方式："+way+"<BR>");
							buffer.append("上菜方式:"+go_goods_way+"<BR><BR><BR><BR><BR><BR>");
							buffer.append("<CUT>");
						}
					}
				}
				if (if_print) {
					do_goods_print(buffer.toString().substring(0,buffer.length()-5), shopprinter.getPrinter_no());
				}
			}

		}
		
		return returnStr;
	}

	/**
	 * 执行打印
	 * 
	 * @param goods
	 * @param print_no
	 * @return
	 */
	public String do_goods_print(String content, String print_no) {
		// 通过POST请求，发送打印信息到服务器
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)// 读取超时
				.setConnectTimeout(30000)// 连接超时
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		HttpPost post = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", USER));
		String STIME = String.valueOf(System.currentTimeMillis() / 1000);
		nvps.add(new BasicNameValuePair("stime", STIME));
		nvps.add(new BasicNameValuePair("sig", signature(USER, UKEY, STIME)));
		nvps.add(new BasicNameValuePair("apiname", "Open_printMsg"));// 固定值,不需要修改
		nvps.add(new BasicNameValuePair("sn", print_no));
		nvps.add(new BasicNameValuePair("content", content));
		nvps.add(new BasicNameValuePair("times", "1"));// 打印联数

		CloseableHttpResponse response = null;
		String result = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			response = httpClient.execute(post);
			int statecode = response.getStatusLine().getStatusCode();
			if (statecode == 200) {
				HttpEntity httpentity = response.getEntity();
				if (httpentity != null) {
					// 服务器返回的JSON字符串，建议要当做日志记录起来
					result = EntityUtils.toString(httpentity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				post.abort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public String dopRrint() {
		String result = "";
		if (flag.equals("2")) {
			result = check_print();
		} else if (flag.equals("1")) {
			if(print_way==2){
				result = goods_print();
			}else{
				//一单一打
				result = goods_print_pre_good();
			}
			
		} else if(flag.equals("3")){
			result = do_goods_print(content,print_no);
		}else if(flag.equals("4")){
			goodsBackPrint();
		}
		return result;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		dopRrint();
	}
	
	/**
	 * 退菜打印
	 */
	public void goodsBackPrint(){
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<CB>" + area.getArea_name() + "---" + table.getTable_name() + "(退菜)</CB><BR>");
			buffer.append("<B>"+back_cart_goods.getGood_name() + " X " + back_cart_goods.getGood_num() + "份</B><BR>");
			do_goods_print(buffer.toString(),print_no);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
