package com.easyticket.job;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.easyticket.Main;
import com.easyticket.util.DateUtil;
import com.jfinal.plugin.quartz.JFinalJob;

/**
 * 开启购票
 * @author lenovo
 *
 */
public class StartTicketJob extends JFinalJob{
	private static final Logger logger = Logger.getLogger(StartTicketJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Main.canRun = true;
		logger.info("开始运行购票");
		new Main().main();
	}

	

}
