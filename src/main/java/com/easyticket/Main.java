package com.easyticket;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyticket.book.TicketBook;
import com.easyticket.cdn.CdnManage;
import com.easyticket.cdn.CheckCdn;
import com.easyticket.core.Api;
import com.easyticket.core.BookQueue;
import com.easyticket.core.Config;
import com.easyticket.core.Device;
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

	public void main() {
		// 初始化线程池
		SimpleThreadLocalPool.init();
		/*
		 * String result = HttpClientUtil.sendGet("https://ifconfig.co/ip");
		 * logger.info("当前公网IP地址：" + result.replaceAll("\n", ""));
		 */
		// 初始化车站信息
		Stations.init();
		// 初始化查询地址
		InitLeftQueryUrl.init();
		// 初始化席别代码
		SeatType.inint();
		//获取动态秘钥
		Device.init();

		// 过滤cdn
		CdnManage.init();
		if (CdnManage.ips.isEmpty()) {
			logger.info("未找到cdn，开始过滤");
			SimpleThreadLocalPool.get().execute(new CheckCdn());

		} else {
			// 超过3天没过滤，需要重新过滤
			File cdnFolder = new File(Config.getCookiePath() + "/cdn/");
			if (cdnFolder.listFiles() != null) {
				String cdnFilterDate = cdnFolder.listFiles()[cdnFolder.listFiles().length - 1].getName().split("_")[0]
						.replaceAll("-", "");
				logger.info("CDN上次过滤时间：" + cdnFilterDate);
				if ((Integer.valueOf(DateUtil.getDate(DateUtil.TO_DATE).replaceAll("-", ""))
						- Integer.valueOf(cdnFilterDate)) >= 3) {
					logger.info("CDN超过3天为过滤，需要重新过滤！");
					SimpleThreadLocalPool.get().execute(new CheckCdn());
				}

			}
			logger.info(String.format("可用cdn%s个", CdnManage.ips.size()));
		}
		Config config = new Config();
		FileKit.delete(new File(config.getCookiePath() + config.getUserName() + "_12306Session.txt"));
		boolean login = new Login().login();
		try {
			if (login) {
				Main.canRun = true;
				while (canRun) {
					new QueryTicket().run();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println(DateUtil.stampToDate("1554704785526"));
		
		System.out.println((Long.valueOf("1554704785526")<System.currentTimeMillis()));
	}
	
	
	 public static int compare_date(String DATE1, String DATE2) {
	        
	        
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        try {
	            Date dt1 = df.parse(DATE1);
	            Date dt2 = df.parse(DATE2);
	            if (dt1.getTime() > dt2.getTime()) {
	                System.out.println("dt1 在dt2前");
	                return 1;
	            } else if (dt1.getTime() < dt2.getTime()) {
	                System.out.println("dt1在dt2后");
	                return -1;
	            } else {
	                return 0;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
	        return 0;
	    }

	
}
