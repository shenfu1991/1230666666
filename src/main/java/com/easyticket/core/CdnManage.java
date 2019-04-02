package com.easyticket.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyticket.util.FileUtil;
import com.jfinal.kit.PathKit;

/**
 * cdn管理
 * 
 * @author lenovo
 *
 */

public class CdnManage {

	private static final Logger logger = LoggerFactory.getLogger(CdnManage.class);

	public static List<String> ips = new ArrayList<>();

	public static void init() {
		try {
			String file = FileUtil.readFile(
					new File(PathKit.class.getClassLoader().getResource("").toURI().getPath() + "availableCdn.txt"),
					"UTF-8");
			if (StringUtils.isNotBlank(file)) {
				List<String> cdnIPList = Arrays.asList(file.split(" "));
				ips.addAll(cdnIPList);
				
			}
		
		} catch (Exception e) {
			logger.error("读取CDN文件出错！", e);
		}
	}

	public static String getIp() {
		if (ips == null || ips.size() == 0) {
			throw new RuntimeException("cdn ip未过滤！");
		}
		int len = ips.size();
		Random random = new Random();
		int a = random.nextInt(len - 1);
		return ips.get(a);
	}
	
	public static void main(String[] args) {
		CdnManage.init();
		System.out.println(CdnManage.getIp());
		
	}

}
