package com.easyticket.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.easyticket.Main;
import com.easyticket.query.QueryTicket;
import com.jfinal.plugin.quartz.JFinalJob;

public class ShuaPiaoJob extends JFinalJob{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(Main.canRun){
			new QueryTicket().run();	
		}

	}

	

}
