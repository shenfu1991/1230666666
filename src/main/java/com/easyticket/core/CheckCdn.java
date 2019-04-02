package com.easyticket.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;



import com.alibaba.fastjson.JSON;
import com.easyticket.util.FileUtil;
import com.easyticket.util.HttpClientUtil;
import com.jfinal.kit.PathKit;

public class CheckCdn implements Runnable {

	private static final Logger logger = Logger.getLogger(CheckCdn.class);

	@Override
	public void run() {
		try {
			List<String> cdnIPList = Arrays.asList(FileUtil
					.readFile(new File(PathKit.class.getClassLoader().getResource("").toURI().getPath() + "cdn.txt"),
							"UTF-8")
					.split(" "));
			int cdnSize = cdnIPList.size();
			int len = 0;
			for (int i = 0; i < cdnSize; i++) {
				String ip = cdnIPList.get(i);
				String url = String.format("http://%s/otn/resources/js/framework/station_name.js", ip);
				if (checkCdn(url)) {
					len++;
					logger.info(String.format("CDN：%s可用",  ip));
					writeFile(ip);
				} 
			}
			logger.info(String.format("CDN过滤完成，可用CDN%s个", len));
		} catch (Exception e) {
			logger.error("过滤CDN文件出错！", e);
		}

	}

	/**
	 * 检测cdn是否可用
	 * 
	 * @param url
	 * @return boolean
	 */
	private static boolean checkCdn(String url) {
		try {
			HttpGet get = new HttpGet(url);
			get.addHeader("Host", "kyfw.12306.cn");
			get.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)
					.setConnectionRequestTimeout(1000).setSocketTimeout(3000).build();
			get.setConfig(requestConfig);
			CloseableHttpResponse response = HttpClientUtil.getClient().execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static void writeFile(String content) {
		try {
			File writeName = new File(PathKit.getWebRootPath() + "/src/main/resources/availableCdn.txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(writeName, true));
			output.write(content + "\r\n");
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
