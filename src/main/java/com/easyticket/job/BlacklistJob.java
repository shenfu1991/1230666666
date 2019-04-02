package com.easyticket.job;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.easyticket.core.Config;
import com.easyticket.util.DateUtil;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.quartz.JFinalJob;

/**
 * 小黑屋
 * 
 * @author lenovo
 *
 */
public class BlacklistJob extends JFinalJob {
	
	private static final Logger logger = Logger.getLogger(BlacklistJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<String, String> map = Config.getBlacklistMap();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (DateUtil.getDayCompare(value, DateUtil.getDate("yyyyMMddHHmmss"), "yyyyMMddHHmmss") > PropKit.getInt("blacklist")) {
				logger.info(String.format("%s车次从小黑屋中移除", key.split("_")[0]));
				map.remove(key);
			}
		}

	}

}
