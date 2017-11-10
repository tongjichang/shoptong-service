package com.haffee.heygay.bo;

import java.util.Date;
import java.util.TimerTask;

public class MyTask extends TimerTask{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("定时任务________"+new Date()); 
	}

}
