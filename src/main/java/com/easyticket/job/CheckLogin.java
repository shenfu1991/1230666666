package com.easyticket.job;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.easyticket.Main;
import com.easyticket.user.Login;
import com.easyticket.util.DateUtil;
import com.jfinal.plugin.quartz.JFinalJob;

/**
 * 检查登录状态
 * 
 * @author lenovo
 *
 */
public class CheckLogin extends JFinalJob {

	private static final Logger logger = Logger.getLogger(CheckLogin.class);
	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		if(Main.canRun){
			logger.info(DateUtil.getDate(DateUtil.TO_SECOND) + "检测登录状态");
			new Login().login();
		}
		
		

	}

}
