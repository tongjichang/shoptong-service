package com.haffee.heygay.bo;


import java.util.Date;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Controller
public class ScheduleUtils implements ApplicationListener<ContextRefreshedEvent>{
	@Autowired
	private ScheduledTasks s;
	public void init(String time,int bookid){
		Timer timer = new Timer();
		Date date = new Date(); 
		int m = date.getMinutes();
		int h = date.getHours();
		int minutes = (Integer.parseInt(time) - h)*60-m;
//		ScheduledTasks s = new ScheduledTasks(bookid);
		s.setBookid(bookid);
		timer.schedule(s,minutes*60*1000L);
//		timer.schedule(s,5*1000L);
		
		/*void cancel()   
		        终止此计时器，丢弃所有当前已安排的任务。   
		int purge()   
		        从此计时器的任务队列中移除所有已取消的任务。   
		void schedule(TimerTask task, Date time)   
		        安排在指定的时间执行指定的任务。   
		void schedule(TimerTask task, Date firstTime, long period)   
		        安排指定的任务在指定的时间开始进行重复的固定延迟执行。   
		void schedule(TimerTask task, long delay)   
		        安排在指定延迟后执行指定的任务。   
		void schedule(TimerTask task, long delay, long period)   
		        安排指定的任务从指定的延迟后开始进行重复的固定延迟执行。   
		void scheduleAtFixedRate(TimerTask task, Date firstTime, long period)   
		        安排指定的任务在指定的时间开始进行重复的固定速率执行。   
		void scheduleAtFixedRate(TimerTask task, long delay, long period)   
		        安排指定的任务在指定的延迟后开始进行重复的固定速率执行。 */
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent evt) {
		if (evt.getApplicationContext().getParent() == null) {
//            init("1");
        }
		
	}
	
	
    
	

}
