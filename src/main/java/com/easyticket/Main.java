package com.easyticket;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.easyticket.book.TicketBook;
import com.easyticket.core.BookQueue;
import com.easyticket.core.CdnManage;
import com.easyticket.core.CheckCdn;
import com.easyticket.core.Config;
import com.easyticket.core.InitLeftQueryUrl;
import com.easyticket.core.SeatType;
import com.easyticket.job.CheckLogin;
import com.easyticket.query.QueryTicket;
import com.easyticket.station.Stations;
import com.easyticket.thread.SimpleThreadLocalPool;
import com.easyticket.user.Login;
import com.easyticket.util.DateUtil;
import com.easyticket.util.HttpClientUtil;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.PropKit;


public class Main {
	private static final Logger logger = Logger.getLogger(Main.class);
	public static boolean canRun = false;

	
	public void main(){
		//初始化线程池
		SimpleThreadLocalPool.init();
		/*String result = HttpClientUtil.sendGet("https://ifconfig.co/ip");
		logger.info("当前公网IP地址：" + result.replaceAll("\n", ""));*/
		// 初始化车站信息
		Stations.init();
		// 初始化查询地址
		InitLeftQueryUrl.init();
		
		SeatType.inint();
		
		//过滤cdn
		CdnManage.init();
		if(CdnManage.ips.isEmpty()){
			logger.info("未找到cdn，开始过滤");
			SimpleThreadLocalPool.get().execute(new CheckCdn());
			
		}else{
			logger.info(String.format("可用cdn%s个", CdnManage.ips.size()));
		}
		Config config = new Config();
		FileKit.delete(new File( config.getCookiePath() + config.getUserName() + "_12306Session.txt"));
		boolean login = new Login().login();
		try {
			if(login){
				Main.canRun = true;
				while (canRun) {
					 new QueryTicket().run();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
