package com.easyticket.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyticket.user.Login;
import com.easyticket.util.HttpClientUtil;
import com.jfinal.kit.PropKit;

/**
 * 动态秘钥
 * 
 * @author lenovo
 *
 */
public class Device {

	private static final Logger logger = Logger.getLogger(Device.class);

	public static void init() {
		JSONObject jsonObject = getDeviceId();
		if (jsonObject != null) {
			BasicClientCookie exp = new BasicClientCookie("RAIL_EXPIRATION", jsonObject.getString("exp"));
			exp.setDomain(HeaderSotre.host);
			exp.setPath("/");
			CookieStore.cookieStore.addCookie(exp);

			BasicClientCookie DEVICEID = new BasicClientCookie("RAIL_DEVICEID", jsonObject.getString("dfp"));
			DEVICEID.setDomain(HeaderSotre.host);
			DEVICEID.setPath("/");
			CookieStore.cookieStore.addCookie(DEVICEID);

		}

	}

	/**
	 * exp
	 * 
	 * @return exp dfp
	 */
	public static JSONObject getDeviceId() {
		try {
			String url = Api.getBrowserDeviceId
					+ "?algID=IQSHeD5sKI&hashCode=iMN_WocRxaQTKmlJhCyGdiqRFCRQzYUFSclCQeghFGQ&FMQw=0&q4f3=zh-CN&VySQ=FGFf8NTIYKkGSmtsBqJcyw3w1qE4gGC7&VPIf=1&custID=133&VEek=unknown&dzuS=27.0%20r0&yD16=0&EOQP=eea1c671b27b7f53fb4ed098696f3560&lEnu=3232235781&jp76=2a9ca2e12c8435592de6782849e1d973&hAqN=Win32&platform=WEB&ks0Q=774f213411edc297d4bde6fc65ed2d79&TeRS=1042x1920&tOHY=24xx1080x1920&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20WOW64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/69.0.3497.100%20Safari/537.36&E3gR=3b9a90a447b49ae75b3a1d2133d95c8e&timestamp="
					+ System.currentTimeMillis();

			HttpGet get = new HttpGet(url);
			get.addHeader("Host", HeaderSotre.host);
			get.addHeader("Referer", "https://www.12306.cn/index/");
			get.addHeader("User-Agent", HeaderSotre.userAgent);

			CloseableHttpResponse re = HttpClientUtil.getClient().execute(get);
			String reuslt = EntityUtils.toString(re.getEntity());
			return JSON.parseObject(reuslt.replaceFirst("callbackFunction", "").replaceAll("\\(", "")
					.replaceAll("\\)", "").replaceAll("'", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAlgId() {
		try {
			String url = "https://kyfw.12306.cn/otn/HttpZF/GetJS";
			HttpGet get = new HttpGet(url);
			get.addHeader("Referer", "https://www.12306.cn/index/index.html");
			get.addHeader("User-Agent", HeaderSotre.userAgent);
			CloseableHttpResponse re = HttpClientUtil.getClient().execute(get);
			String reuslt = EntityUtils.toString(re.getEntity());
			Pattern p = Pattern.compile("algID\\\\x3d(.*?)\\\\x26");
			Matcher m = p.matcher(reuslt);
			while (m.find()) {
				return m.group(1);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
