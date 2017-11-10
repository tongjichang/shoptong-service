package com.haffee.heygay.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class Utils {
	  
	public static void main(String[] args){     
        Timer timer = new Timer();     
        //timer.schedule(new MyTask(), 1000, 2000);//在1秒后执行此任务,每次间隔2秒执行一次,如果传递一个Data参数,就可以在某个固定的时间执行这个任务.     
        Calendar calendar = Calendar.getInstance();  
        calendar.set(Calendar.HOUR_OF_DAY, 23);  
        calendar.set(Calendar.MINUTE, 25);  
        calendar.set(Calendar.SECOND, 0);  
        Date time = calendar.getTime();  
        timer = new Timer();  
        timer.schedule(new MyTask(), time);
        while(true){//这个是用来停止此任务的,否则就一直循环执行此任务     
            try{     
                int in = System.in.read();    
                if(in == 's'){     
                    timer.cancel();//使用这个方法退出任务     
                    break;  
                }     
            } catch (IOException e){     
                // TODO Auto-generated catch block     
                e.printStackTrace();     
            }     
        }     
    }    
      
    static class MyTask extends java.util.TimerTask{      
        public void run(){     
            System.out.println("________");     
        }     
    }    

}
