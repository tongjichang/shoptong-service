package com.haffee.heygay.util;

public class PrintUtils  {  
//	
//	@Autowired
//	private IUserDao dao;
//	private String shopid;  
//	
//    private String orders;  
//    private String printer; 
//    private OrderInfo o;
//    private List<Object> ctg_list;
//    private List<Object> goods;
//    private Waiter w;
//	// 构造函数  
//    public PrintUtils(String shopid, String orders,String printer, OrderInfo o,List<Object> ctg_list,List<Object> goods,Waiter w) {  
//        this.shopid = shopid;     
//        this.orders = orders;  
//        this.printer = printer;   
//        this.o = o;   
//        this.ctg_list = ctg_list;  
//        this.goods = goods; 
//        this.w = w;
//    } 
//    @Override  
//    public int print(Graphics g, PageFormat pageFormat, int page) throws PrinterException {  
//        SimpleDateFormat simpleDateFormat;
// 		simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
// 		Date date = new Date();
// 		String str = simpleDateFormat.format(date);
//        if (page > 0) {  
//            return NO_SUCH_PAGE;  
//        } 
//        Graphics2D g2d = (Graphics2D) g; 
//        //打印起点坐标  
//        double x= pageFormat.getImageableX();  //返回与此 PageFormat相关的 Paper对象的可成像区域左上方点的 x坐标。  
//        double y= pageFormat.getImageableY();  //返回与此 PageFormat相关的 Paper对象的可成像区域左上方点的 y坐标。
//        Font font = new Font("宋体",Font.BOLD,10); //根据指定名称、样式和磅值大小，创建一个新 Font。
//        g2d.setFont(font);//设置标题打印字体     
//        float heigth = font.getSize2D();//获取字体的高度
//        String cpflg = "0";
//        String ifprint = "0";
//        g2d.setColor(Color.black);  
//		g2d.drawString("预结单",(float)x+45,(float)y+heigth); 
//        float line = 2*heigth; //下一行开始打印的高度
//        g2d.setFont(new Font("宋体", Font.PLAIN,8));//设置正文字体  
//        heigth = font.getSize2D();// 字体高度  
//        line+=2;
// 		//虚线设置
// 		g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,4.0f,new float[]{4.0f},0.0f));  
// 		//在此图形上下文的坐标系中使用当前颜色在点 (x1, y1) 和 (x2, y2) 之间画一条线。 即绘制虚线
// 		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line));   
//        line+=heigth;
////        g2d.setFont(new Font("Default", Font.PLAIN, 10));  
//        g2d.drawString("桌号：" + o.getTable_id()+"  人数:"+o.getPeople_count()+" 服务员:"+w.getName(), (float)x+5,(float)y+line);  
//        line+=heigth+2;
//        g2d.drawString("账单：" + o.getOrder_num(), (float)x+5,(float)y+line); 
//        line+=heigth+2;
//        g2d.drawString("时间：" + str, (float)x+5,(float)y+line); 
//        line+=heigth+2; 
//      //虚线设置
// 		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line));  
//// 		g2d.setFont(new Font("Default", Font.PLAIN, 10));  
// 		line+=heigth+2;
// 		g2d.drawString("菜点明细", (float)x+50,(float)y+line); 
// 		line+=heigth;
// 		//虚线设置
// 		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line));  
// 		line += heigth+2;  
// 		//设置标题  
// 		g2d.drawString("名称",(float)x+20, (float)y+line);  
// 		g2d.drawString("单价",(float)x+60, (float)y+line);  
// 		g2d.drawString("数量",(float)x+90, (float)y+line);  
// 		g2d.drawString("小计",(float)x+120, (float)y+line);  
// 		line+=heigth;
// 		//在此图形上下文的坐标系中使用当前颜色在点 (x1, y1) 和 (x2, y2) 之间画一条线。 即绘制虚线
// 		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line)); 
// 		line += heigth+2;  
//// 		g2d.drawString("名 称                  单价         数量              小计             ", 7, 65); 
// 		for(int h=0;h<ctg_list.size();h++){
// 			float xj = 0f;
// 			cpflg = "0";
// 			GoodsCategory goodsCategory = (GoodsCategory)ctg_list.get(h);
//			for(Object object : o.getCart().getGoods_set()){
//		        //获取当前循环菜品的配置打印机编号
//		        String dyjs = "GP-58130";
//		        Goods gds = new Goods();
//				ShoppingCartGoods s =(ShoppingCartGoods)object;
//				for(Object object1 :goods){
//					gds =(Goods)object1;
//					if(s.getGood_id()==gds.getGood_id()){
//						dyjs = gds.getPrint_str();
//						String[] dyj = dyjs.split("\\,");
//				        for (int i = 0; i < dyj.length; i++) { 
//				            if (printer.contains(dyj[i])) {  
//				            	if(goodsCategory.getCategory_id()==gds.getCategory_id()){
//				            		cpflg = "1";
//				            	}
//				            } 
//				        } 
//					}
//				}
//		        
//			}
//			if(cpflg.equals("1")){
//        		g2d.drawString(goodsCategory.getCategory_name()+"类:", (float)x+15, (float)y+line); 
//        		line += heigth;  
//			}
//			for(Object object : o.getCart().getGoods_set()){
//		        //获取当前循环菜品的配置打印机编号
//		        String dyjs = "GP-58130";
//				ShoppingCartGoods s =(ShoppingCartGoods)object;
//				Goods gds = new Goods();
//				for(Object object1 :goods){
//					gds =(Goods)object1;
//					if(s.getGood_id()==gds.getGood_id()){
//						dyjs = gds.getPrint_str();
//				        String[] dyj = dyjs.split("\\,");
//				        for (int i = 0; i < dyj.length; i++) { 
//				            if (printer.contains(dyj[i])) {  
//				            	if(goodsCategory.getCategory_id()==gds.getCategory_id()){
//						        	cpflg = "1";
////					            	ifprint = "1";
////					            	//需要在当前打印机打印的菜品
////							        g2d.setFont(new Font("Default", Font.PLAIN, 12));  
//					            	g2d.drawString(s.getGood_name(),(float)x+15, (float)y+line);  
//					            	g2d.drawString(String.valueOf(gds.getPre_price()),(float)x+60, (float)y+line);  
//					            	g2d.drawString(String.valueOf(s.getGood_num()),(float)x+95,(float)y+line);  
//					            	g2d.drawString(String.valueOf(s.getGood_total_price()),(float)x+100,(float)y+line); 
//					            	line+=heigth;
//							        if(xj==0f){
//							        	xj =s.getGood_total_price();
//							        }else{
//							        	 BigDecimal b1 = new BigDecimal(xj + "");
//							             BigDecimal b2 = new BigDecimal(s.getGood_total_price() + "");
//							             xj = b1.add(b2).floatValue();
//							        }
//
//				            	}
//						        
//				            } 
//				        } 
//					}
//				}
//
//			}
//			if(cpflg.equals("1")){
//				g2d.drawString("小计:"+xj, (float)x+85,(float)y+line);
//				line+=heigth;
//			}
//		}
//		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line)); 
//		line+=heigth;
//		g2d.drawString("结算明细", (float)x+20,(float)y+line);
//		line+=heigth;
//		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line)); 
//		line+=heigth;
//		g2d.setFont(font);//设置标题打印字体 
//		g2d.drawString("名称",(float)x+20, (float)y+line);  
// 		g2d.drawString("金额",(float)x+110, (float)y+line); 
// 		line+=heigth-1;
// 		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line)); 
//		line+=heigth;
//		g2d.drawString("应收金额:",(float)x+20, (float)y+line);  
// 		g2d.drawString(String.valueOf(o.getPayment()),(float)x+110, (float)y+line); 
// 		line+=heigth;
//		g2d.drawString("折扣后金额:",(float)x+20, (float)y+line);  
// 		g2d.drawString(String.valueOf(o.getDiscount_payment()),(float)x+110, (float)y+line); 
// 		line+=heigth;
// 		g2d.drawString("抹零:",(float)x+20, (float)y+line);  
// 		g2d.drawString(String.valueOf(o.getMinus_money()),(float)x+110, (float)y+line); 
// 		line+=heigth;
// 		g2d.drawString("实收金额:",(float)x+20, (float)y+line);  
// 		g2d.drawString(String.valueOf(o.getReal_payment()),(float)x+110, (float)y+line); 
// 		line+=heigth;
// 		g2d.drawLine((int)x,(int)(y+line),(int)x+158,(int)(y+line)); 
//		line+=heigth+4;
// 		g2d.drawString("欢迎光临！"+shopid,(float)x+20, (float)y+line);  
//  
//        return PAGE_EXISTS;  
////        return NO_SUCH_PAGE;  
//    }  
//    public  void PrintSale(String shopid, String orders,PrintService myPrinter,OrderInfo o,List<Object> ctg_list,List<Object> goods,Waiter w) throws PrinterException {  
//    	/*PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
//    	//pras.add(new Copies(2));//打印份数，3份 
//    	DocFlavor flavor = DocFlavor.BYTE_ARRAY.PNG;
//    	//可用的打印机列表(字符串数组)
//    	 //查找所有打印服务  
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, pras);  
//        PrintService myPrinter = null;  
//    	for (int y = 0; y < services.length; y++) {  
//    		ifprint = "0";
//            System.out.println("service found: " + services[y]);  
//            String svcName = services[y].toString();  
//            myPrinter = services[y];  
//            System.out.println("my printer found: " + svcName);  
//            System.out.println("my printer found: " + myPrinter);
//    	}*/
//    	 int height = 30000;  
//         
//         // 通俗理解就是书、文档  
//         Book book = new Book();  
//   
//         // 打印格式  
//         // 设置成竖打 
//         PageFormat pf = new PageFormat();  
//         pf.setOrientation(PageFormat.PORTRAIT);  
//   
//         // 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。  
//         Paper p = new Paper();  
//         p.setSize(158, height);  
//         p.setImageableArea(0,0,158,30000);  
//         pf.setPaper(p);  
//         // 把 PageFormat 和 Printable 添加到书中，组成一个页面  
//         book.append(new PrintUtils(shopid,orders,myPrinter.toString(),o,ctg_list,goods,w), pf);  
//   
//         // 获取打印服务对象  
//         PrinterJob job = PrinterJob.getPrinterJob();  
//         job.setPageable(book);  
//         job.setPrintService(myPrinter);
//         try {  
//             job.print();  
//         } catch (PrinterException e) {  
//             System.out.println("================打印出现异常");  
//         } 
//               
//        
//    	
//    	//当前默认打印机
//    	//PrintService PS = PrintServiceLookup.lookupDefaultPrintService();
////    	 PS.getName();
//    }
//    public static void main(String[] args) throws PrinterException {
//    	
////    	PrintSale("4","1");
//         
//  
//    }  
  
}
