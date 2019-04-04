package com.easyticket.job;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.easyticket.Main;
import com.jfinal.plugin.quartz.JFinalJob;

public class StopTicketJob extends JFinalJob{
	private static final Logger logger = Logger.getLogger(StopTicketJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Main.canRun = false;
		logger.info("停止购票，系统将在明天6点继续运行");
	}

	
 
}
