package com.easyticket.cdn;

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
import com.easyticket.core.Config;
import com.easyticket.core.HeaderSotre;
import com.easyticket.util.DateUtil;
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
				//可用CDN为15个的时候重新读取
				if(len==15){
					CdnManage.init();
				}
				String ip = cdnIPList.get(i);
				String url = String.format("http://%s/otn/resources/js/framework/station_name.js", ip);
				if (checkCdn(url)) {
					len++;
					logger.info(String.format("CDN：%s可用",  ip));
					writeFile(ip);
				} 
			}
			CdnManage.init();
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
			get.addHeader("Host", HeaderSotre.host);
			get.addHeader("User-Agent",HeaderSotre.userAgent);
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
			
			File cdnFolder = new File(Config.getCookiePath()+"cdn/");
			if(!cdnFolder.exists()){
				cdnFolder.mkdirs();
			}
			
			File writeName = new File(Config.getCookiePath()+"cdn/"+DateUtil.getDate(DateUtil.TO_DATE)+"_availableCdn.txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(writeName, true));
			output.write(content + "\r\n");
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
