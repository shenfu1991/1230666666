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
import com.easyticket.util.HttpClientUtil;

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
					+ "?algID=DSMUHpqg2c&hashCode=7D2olfNltplDRYYVR5GfyPVdYWm_PQvOWVmvaWUpOJI&FMQw=0&q4f3=zh-CN&VySQ=FGE8-ztrZHjoQKFkw9FzanJmGHUMxCjZ&VPIf=1&custID=133&VEek=unknown&dzuS=29.0%20r0&yD16=0&EOQP=eea1c671b27b7f53fb4ed098696f3560&lEnu=3232235939&jp76=a5b2ffe86c07bdcd9a3445c81d946c92&hAqN=Win32&platform=WEB&ks0Q=d82d0dd98d93e709d91f86348e2a0e86&TeRS=1042x1920&tOHY=24xx1080x1920&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20WOW64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/63.0.3239.132%20Safari/537.36&E3gR=c55529c990fcaabc078c4304eadf4ccb&timestamp="
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
