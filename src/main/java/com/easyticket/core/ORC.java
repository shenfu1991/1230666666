package com.easyticket.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyticket.util.HttpClientUtil;

/**
 * 验证码识别
 * 
 * @author lenovo
 *
 */

public class ORC {

	private static final Logger logger = Logger.getLogger(ORC.class);

	/**
	 * 验证码识别，360接口
	 * 
	 * @param base64
	 * @return x ,y
	 */
	public static String getImgPositionBy360(String base64) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("base64", base64);
		String checkResult = HttpClientUtil.sendPost(Api.getCheck, JSON.toJSONString(param), null);
		JSONObject check = JSON.parseObject(checkResult);
		if (check!=null && check.getBooleanValue("success")) {
			Header header0 = new BasicHeader("Host", "check.huochepiao.360.cn");
			Header header1 = new BasicHeader("Upgrade-Insecure-Requests", "1");
			Header header2 = new BasicHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36 QIHU 360EE");
			Header header3 = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
			StringEntity stringEntity = new StringEntity(
					"{\"check\":\"" + check.getJSONObject("data").getString("check") + "\",\"img_buf\":\"" + base64
							+ "\",\"logon\":" + 1 + ",\"type\":\"D\"}",
					"utf-8");
			stringEntity.setContentType("application/json;charset=UTF-8");
			try {
				HttpUriRequest getVcode = RequestBuilder.post().setUri(new URI(Api.getPositionBy360))
						.setEntity(stringEntity).addHeader(header0).addHeader(header1).addHeader(header2)
						.addHeader(header3).build();
				CloseableHttpClient httpClient = HttpClientUtil.getClient();
				CloseableHttpResponse response = httpClient.execute(getVcode);
				HttpEntity entity1 = response.getEntity();
				String content1 = EntityUtils.toString(entity1, "UTF-8");
				if (content1.equals("error")) {
					logger.error("验证码识别出错...");
					return null;
				} else {
					JSONObject obj = JSON.parseObject(content1);
					return obj.getString("res").replaceAll("\\(", "").replaceAll("\\)", "");
				}
			} catch (Exception e) {
				logger.info(e.getMessage(), e);
			}
			return "";
		} else {
			logger.info("获取check失败...");
			return null;
		}
	}

	/**
	 * 验证码识别，机器学习
	 * 
	 * @param base64
	 * @return x ,y
	 */
	public static String getImgPositionByAi(String base64) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("base64", base64);
		String positionResult = HttpClientUtil.sendPost(Api.getPositionByAi, param);
		if (positionResult.equals("error")) {
			logger.error("验证码识别出错...");
			return null;
		} else {
			JSONObject obj = JSON.parseObject(positionResult);
			return obj.getString("res").replaceAll("\\(", "").replaceAll("\\)", "");
		}
	}

}
