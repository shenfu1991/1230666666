package com.easyticket.user;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyticket.Main;
import com.easyticket.core.Api;
import com.easyticket.core.Config;
import com.easyticket.core.CookieStore;
import com.easyticket.core.HeaderSotre;
import com.easyticket.core.ORC;
import com.easyticket.util.DateUtil;
import com.easyticket.util.FileUtil;
import com.easyticket.util.HttpClientUtil;
import com.jfinal.kit.PropKit;

public class Login {

	private static final Logger logger = Logger.getLogger(Login.class);

	static BasicCookieStore cookieStore = CookieStore.cookieStore;
	static CloseableHttpClient httpclient = null;

	/**
	 * login 获得 cookie uamtk auth/uamtk 不请求，会返回 uamtk票据内容为空 /otn/uamauthclient
	 * 能拿到用户名
	 * 
	 * @return
	 */
	public boolean login() {
		
		if (!DateUtil.isNormalTime()) {
			logger.info("维护时间，暂停查询");
			return false;
		}
		
		
		// 是否需要验证码登录
		boolean is_login_passCode = false;
		BasicClientCookie exp = new BasicClientCookie("RAIL_EXPIRATION", PropKit.get("RAIL_EXPIRATION"));
		exp.setDomain(HeaderSotre.host);
		exp.setPath("/");
		cookieStore.addCookie(exp);
		BasicClientCookie DEVICEID = new BasicClientCookie("RAIL_DEVICEID",
				PropKit.get("RAIL_DEVICEID"));
		DEVICEID.setDomain(HeaderSotre.host);
		exp.setPath("/");
		cookieStore.addCookie(DEVICEID);
		httpclient = HttpClientUtil.getHttpClient(cookieStore);
		Header[] headers = new BasicHeader[3];
		headers[0] = new BasicHeader("User-Agent", HeaderSotre.userAgent);
		headers[1] = new BasicHeader("Host", HeaderSotre.host);
		headers[2] = new BasicHeader("Referer", "https://kyfw.12306.cn/otn/index/init");
		try {
			HttpUriRequest initPage = RequestBuilder.get().setUri(new URI(Api.loginInitPage)).addHeader(headers[0])
					.addHeader(headers[1]).addHeader(headers[2]).build();
			CloseableHttpResponse initPageresponse = httpclient.execute(initPage);
			HttpEntity entity = initPageresponse.getEntity();
			EntityUtils.consume(entity);
			initPageresponse.close();

			// 读取cookie
			readFile2Cookie();

			JSONObject confObj = JSON.parseObject(JSON.toJSONString(getConf()));
			if (confObj.getJSONObject("data").getString("is_login_passCode").equals("Y")) {
				is_login_passCode = true;
			}
			if (is_login_passCode) {
				logger.info("使用需要验证码的登录流程");
				// 验证登陆状态
				Map<String, Object> onlineMap = this.checkOnlineStatus(headers);
				logger.info("帐号状态监测完成！");
				if (null != onlineMap && onlineMap.size() > 0) {
					if ("0".equalsIgnoreCase(onlineMap.get("result_code") + "")) {
						logger.info(String.format("帐号%s已经登录！", Config.getUserName()));
						return true;
					} else {
						logger.info(String.format("帐号%s未登录！", Config.getUserName()));
					}
				}
				logger.info(String.format("帐号%s准备开始登录！", Config.getUserName()));
				headers[2] = new BasicHeader("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
				getAllCookies(cookieStore);
			} else {
				logger.info("使用不需要验证码的登录流程");
				Map<String, Object> onlineMap = this.getConf();

				if (onlineMap.get("data") != null
						&& JSON.parseObject(onlineMap.get("data").toString()).getString("is_login").equals("Y")) {
					logger.info(String.format("帐号%s已经登录！", Config.getUserName()));
					return true;
				} else {
					logger.info(String.format("帐号%s未登录！", Config.getUserName()));
				}
				getAllCookies(cookieStore);
			}

			if (is_login_passCode) {
				String captchaImage = getCaptchaImage64(headers);
				logger.info("验证码获取完成！");

				logger.info("准备开始自动识别验证码！");

				String position = ORC.getImgPositionBy360(captchaImage);
				if (StringUtils.isBlank(position)) {
					logger.info("使用360验证码识别打码失败，启用AI验证码识别！");
					position = ORC.getImgPositionByAi(captchaImage);
				}
				logger.info("使用360验证码识别打码成功！");
				if (StringUtils.isBlank(position)) {
					logger.info("登录验证码打码失败");
					return false;
				}

				logger.info(String.format("登录验证码打码成功，坐标：%s", position));

				// 验证码校验状态
				boolean checkedCode = false;

				HttpGet checkCodeGet = new HttpGet(String.format(Api.captchaCheck, position, Math.random()));
				checkCodeGet.addHeader(headers[0]);
				checkCodeGet.addHeader(headers[1]);
				checkCodeGet.addHeader(headers[2]);
				CloseableHttpResponse checkCoderesponse = httpclient.execute(checkCodeGet);
				getAllCookies(cookieStore);
				HttpEntity checkCodeEntity = checkCoderesponse.getEntity();
				String checkCodeResult = EntityUtils.toString(checkCodeEntity, "UTF-8");
				JSONObject checkCodeObj = JSON.parseObject(checkCodeResult);
				if (null == checkCodeObj) {
					logger.info("登录验证码校验不通过！");
				} else if (checkCodeObj.getString("result_code").equals("4")) {
					logger.info("登录验证码校验通过，准备开始登录！");
					checkedCode = true;
				} else {
					logger.info("登录验证码校验不通过！原因：" + checkCodeObj.getString("result_message"));
				}

				if (!checkedCode) {
					return false;
				}
			}

			Thread.currentThread().sleep(400);

			headers = new BasicHeader[7];
			headers[0] = new BasicHeader("User-Agent", HeaderSotre.userAgent);
			headers[1] = new BasicHeader("Host", HeaderSotre.host);
			headers[2] = new BasicHeader("Referer", Api.loginInitPage);
			headers[3] = new BasicHeader("Accept", "*/*");
			headers[4] = new BasicHeader("Accept-Encoding", "gzip, deflate");
			headers[5] = new BasicHeader("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3");
			headers[6] = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");

			// 需要验证码的登录流程
			if (is_login_passCode) {
				List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				formparams.add(new BasicNameValuePair("username", Config.getUserName()));
				formparams.add(new BasicNameValuePair("password", Config.getPassword()));
				formparams.add(new BasicNameValuePair("appid", "otn"));

				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
				HttpPost login = new HttpPost(Api.login);
				login.setEntity(urlEncodedFormEntity);
				login.addHeader(headers[0]);
				login.addHeader(headers[1]);
				login.addHeader(headers[2]);
				login.addHeader(headers[3]);
				login.addHeader(headers[4]);
				login.addHeader(headers[5]);
				login.addHeader(headers[6]);

				login.addHeader("X-Requested-With", "XMLHttpRequest");
				login.addHeader("Connection", "keep-alive");
				CloseableHttpResponse loginResponse = httpclient.execute(login);
				Thread.currentThread().sleep(400);
				HttpEntity loginEntity = loginResponse.getEntity();

				String loginResultS = EntityUtils.toString(loginEntity);
		
				JSONObject loginResult = JSON.parseObject(loginResultS);
				if (!"0".equals(loginResult.getString("result_code"))) {
					logger.info(
							String.format("帐号%s登录失败，原因：%s", Config.getUserName(), loginResult.get("result_message")));
					return false;
				}

				HttpUriRequest userLogin = RequestBuilder.post().setUri(Api.userLogin).addHeader(headers[0])
						.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
						.addHeader(headers[5]).addHeader(headers[6]).addParameter("_json_att", "").build();
				login.addHeader("X-Requested-With", "XMLHttpRequest");
				login.addHeader("Connection", "keep-alive");
				CloseableHttpResponse response = httpclient.execute(userLogin);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 302) {
					EntityUtils.consume(response.getEntity());
					HttpUriRequest reload = RequestBuilder.get()// .post()
							.setUri(Api.baseUrl + "/otn/passport?redirect=/otn/login/userLogin").addHeader(headers[0])
							.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
							.addHeader(headers[5]).addHeader(headers[6]).build();

					response = httpclient.execute(reload);
					statusCode = response.getStatusLine().getStatusCode();
					System.out.println("跳转：" + response.getStatusLine().getStatusCode());
					EntityUtils.consume(response.getEntity());
				}

				logger.info(String.format("帐号%s登录成功", Config.getUserName()));
			} else { // 不需要验证码的登录流程

				List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				formparams.add(new BasicNameValuePair("loginUserDTO.user_name", Config.getUserName()));
				formparams.add(new BasicNameValuePair("userDTO.password", Config.getPassword()));
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
				HttpPost login = new HttpPost(Api.loginAysnSuggest);
				login.setEntity(urlEncodedFormEntity);
				login.addHeader(headers[0]);
				login.addHeader(headers[1]);
				login.addHeader(headers[2]);
				login.addHeader(headers[3]);
				login.addHeader(headers[4]);
				login.addHeader(headers[5]);
				login.addHeader(headers[6]);

				login.addHeader("X-Requested-With", "XMLHttpRequest");
				login.addHeader("Connection", "keep-alive");
				CloseableHttpResponse loginResponse = httpclient.execute(login);
				Thread.currentThread().sleep(400);
				HttpEntity loginEntity = loginResponse.getEntity();
				String loginResultS = EntityUtils.toString(loginEntity);
				JSONObject obj = JSON.parseObject(loginResultS);
				if (StringUtils.isNoneBlank(obj.getString("data"))
						&& obj.getJSONObject("data").getString("loginCheck").equals("Y")) {
					HttpUriRequest userLogin = RequestBuilder.post().setUri(Api.userLogin).addHeader(headers[0])
							.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
							.addHeader(headers[5]).addHeader(headers[6]).addParameter("_json_att", "").build();
					login.addHeader("X-Requested-With", "XMLHttpRequest");
					login.addHeader("Connection", "keep-alive");
					CloseableHttpResponse response = httpclient.execute(userLogin);
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == 302) {
						EntityUtils.consume(response.getEntity());
						HttpUriRequest reload = RequestBuilder.get()// .post()
								.setUri(Api.baseUrl + "/otn/passport?redirect=/otn/login/userLogin")
								.addHeader(headers[0]).addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3])
								.addHeader(headers[4]).addHeader(headers[5]).addHeader(headers[6]).build();

						response = httpclient.execute(reload);
						statusCode = response.getStatusLine().getStatusCode();
						System.out.println("跳转：" + response.getStatusLine().getStatusCode());
						EntityUtils.consume(response.getEntity());
					}
					logger.info(String.format("帐号%s登录成功", Config.getUserName()));
				}
			}

		} catch (Exception e) {
			logger.error("12306登录失败!", e);
			return false;
		}

		// 再次校验登陆状态
		if (is_login_passCode) {
			// 验证登陆状态
			String tk = "";
			Map<String, Object> onlineMap = this.checkOnlineStatus(headers);
			if (null != onlineMap && onlineMap.size() > 0) {
				if ("0".equalsIgnoreCase(onlineMap.get("result_code") + "")) {
					logger.info(String.format("再次校验登陆状态，帐号%s已经登录！", Config.getUserName()));
					tk = onlineMap.get("newapptk").toString();

				} else {
					logger.info(String.format("再次校验登陆状态，帐号%s未登录！", Config.getUserName()));
					return false;
				}
			}
			/*
			 * logger.info(String.format("帐号%s准备开始登录！", Config.getUserName()));
			 */
			headers[2] = new BasicHeader("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
			getAllCookies(cookieStore);
			logger.info("获取newapptk成功，newapptk：" + tk);
			try {
				HttpUriRequest uamauthclient = RequestBuilder.post().setUri(Api.uamauthclient).addHeader(headers[0])
						.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
						.addHeader(headers[5]).addHeader(headers[6]).addParameter("tk", tk).build();
				uamauthclient.addHeader("X-Requested-With", "XMLHttpRequest");
				uamauthclient.addHeader("Connection", "keep-alive");
				CloseableHttpResponse response = httpclient.execute(uamauthclient);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					String jsonStr = EntityUtils.toString(entity);
					Map<String, String> jsonObject = JSON.parseObject(jsonStr, Map.class);
					logger.info(String.format("校验通过：%s", jsonObject.get("username")));
					this.getAllCookies(this.cookieStore);
					HttpUriRequest userLogin = RequestBuilder.get().setUri(Api.userLogin).addHeader(headers[0])
							.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
							.addHeader(headers[5]).addHeader(headers[6]).build();
					userLogin.addHeader("X-Requested-With", "XMLHttpRequest");
					userLogin.addHeader("Connection", "keep-alive");
					response = httpclient.execute(userLogin);
					statusCode = response.getStatusLine().getStatusCode();
					EntityUtils.consume(response.getEntity());

					// 跳转到 用户页面

					HttpUriRequest initMy12306 = RequestBuilder.get().setUri(Api.initMy12306).addHeader(headers[0])
							.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
							.addHeader(headers[5]).addHeader(headers[6]).build();
					initMy12306.addHeader("X-Requested-With", "XMLHttpRequest");
					initMy12306.addHeader("Connection", "keep-alive");
					response = httpclient.execute(initMy12306);
					statusCode = response.getStatusLine().getStatusCode();
					logger.info(String.format("initMy12306状态:%s", statusCode));
					EntityUtils.consume(response.getEntity());
					logger.info("用户登录成功");
					// 将成功的cookie写入文件
					writeCookies2File();
					return true;

				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

		} else {
			Map<String, Object> onlineMap = this.getConf();
			if (onlineMap.get("data") != null
					&& JSON.parseObject(onlineMap.get("data").toString()).getString("is_login").equals("Y")) {
				logger.info(String.format("再次校验登陆状态，帐号%s已经登录！", Config.getUserName()));

			} else {
				logger.info(String.format("再次校验登陆状态，帐号%s未登录！", Config.getUserName()));
				return false;
			}

			this.getAllCookies(this.cookieStore);
			HttpUriRequest userLogin = RequestBuilder.get().setUri(Api.userLogin).addHeader(headers[0])
					.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
					.addHeader(headers[5]).addHeader(headers[6]).build();
			userLogin.addHeader("X-Requested-With", "XMLHttpRequest");
			userLogin.addHeader("Connection", "keep-alive");
			try {
				CloseableHttpResponse response = httpclient.execute(userLogin);
				int statusCode = response.getStatusLine().getStatusCode();
				EntityUtils.consume(response.getEntity());

				// 跳转到 用户页面

				HttpUriRequest initMy12306 = RequestBuilder.get().setUri(Api.initMy12306).addHeader(headers[0])
						.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
						.addHeader(headers[5]).addHeader(headers[6]).build();
				initMy12306.addHeader("X-Requested-With", "XMLHttpRequest");
				initMy12306.addHeader("Connection", "keep-alive");
				response = httpclient.execute(initMy12306);
				statusCode = response.getStatusLine().getStatusCode();
				logger.info(String.format("initMy12306状态:%s", statusCode));
				EntityUtils.consume(response.getEntity());
				logger.info("用户登录成功");
				// 将成功的cookie写入文件
				writeCookies2File();
			
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

		}

		return false;
	}

	/**
	 * 获取验证码
	 * 
	 * @return
	 */
	public String getCaptchaImage64(Header[] headers) {
		HttpGet hget = new HttpGet(String.format(Api.captchaImage, Math.random()));
		for (Header h : headers) {
			hget.addHeader(h);
		}
		try {
			CloseableHttpResponse response = httpclient.execute(hget);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity, "UTF-8");
			return JSON.parseObject(content).getString("image");
		} catch (Exception e) {
			logger.error("获取验证码失败,", e);
		}
		return null;
	}

	/**
	 * 检查登录状态 登录流程1
	 * 
	 * @param headers
	 * @return
	 */
	public Map<String, Object> checkOnlineStatus(Header[] headers) {
		Map<String, Object> map = new HashMap<String, Object>();
		HttpUriRequest getDevice = RequestBuilder.post().setUri(Api.uamtkStatic).addHeader(headers[0])
				.addHeader(headers[1]).addHeader(headers[2]).addParameter("appid", "otn").build();
		for (Header h : headers) {
			getDevice.addHeader(h);
		}
		CloseableHttpResponse response2 = null;
		try {
			response2 = httpclient.execute(getDevice);
			HttpEntity entity = response2.getEntity();

			String jsonStr = EntityUtils.toString(entity);
			map = JSON.parseObject(jsonStr, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != response2) {
				try {
					response2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return map;
	}

	public Map<String, Object> getConf() {

		try {
			HttpPost confRequest = new HttpPost(Api.conf);
			confRequest.setHeader("Host", HeaderSotre.host);
			confRequest.setHeader("Origin", Api.baseUrl);
			confRequest.setHeader("Referer", "https://kyfw.12306.cn/otn/resources/login.html");
			confRequest.setHeader("User-Agent", HeaderSotre.userAgent);

			CloseableHttpResponse confresponse = httpclient.execute(confRequest);

			HttpEntity confEntity = confresponse.getEntity();
			String confeResult = EntityUtils.toString(confEntity, "UTF-8");
			return JSON.parseObject(confeResult, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public void getdeviceId() {
		BasicClientCookie exp = new BasicClientCookie("BIGipServerotn", "921698826.64545.0000");
		exp.setDomain("kyfw.12306.cn");
		exp.setPath("/");
		cookieStore.addCookie(exp);
		httpclient = HttpClientUtil.getHttpClient(cookieStore);

		HttpGet hget = new HttpGet(
				"https://kyfw.12306.cn/otn/HttpZF/logdevice?algID=RrAeymgJIP&hashCode=pMUsJQTcY86599AwmmQjYKq86ILz21XLkZqQ22MOdsg&FMQw=0&q4f3=zh-CN&VPIf=1&custID=133&VEek=unknown&dzuS=32.0%20r0&yD16=0&EOQP=eea1c671b27b7f53fb4ed098696f3560&lEnu=3232235779&jp76=e8eea307be405778bd87bbc8fa97b889&hAqN=Win32&platform=WEB&ks0Q=2955119c83077df58dd8bb7832898892&TeRS=834x1536&tOHY=24xx864x1536&Fvje=i1l1o1s1&q5aJ=-8&wNLf=99115dfb07133750ba677d055874de87&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20WOW64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/56.0.2924.87%20Safari/537.36&E3gR=ed0a124813a73261b73349a4a7f2021f&timestamp=1553954405917");
		hget.addHeader("User-Agent", HeaderSotre.userAgent);
		hget.addHeader("Host", Api.baseUrl);
		hget.addHeader("Referer", "https://kyfw.12306.cn/otn/index/init");
		hget.addHeader("Upgrade-Insecure-Requests", "1");
		try {
			CloseableHttpResponse response = httpclient.execute(hget);
			HttpEntity entity = response.getEntity();

			String content = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.error("获取deviceId失败,", e);
		}
	}

	public static void main(String[] args) {
		new Login().getdeviceId();

	}

	/**
	 * 将cookie读取到cookiestore 查看是否可以用
	 */
	public static void readFile2Cookie() {
		Config config = new Config();
		String context = FileUtil.readByLines(config.getCookiePath() + config.getUserName() + "_12306Session.txt");

		if (null != context && !"".equals(context)) {
			String[] aCookie = context.split(";");
			for (String ck : aCookie) {
				String[] bCookie = ck.split(",");
				BasicClientCookie acookie = new BasicClientCookie(bCookie[0], bCookie[1]);
				acookie.setDomain(bCookie[2]);
				acookie.setPath(bCookie[3]);
				cookieStore.addCookie(acookie);
			}
		}
	}

	public static void getAllCookies(BasicCookieStore cookieStore) {
		List<Cookie> cookies = cookieStore.getCookies();
		if (!cookies.isEmpty()) {
			for (int i = 0; i < cookies.size(); i++) {
				// System.out.println("- " + cookies.get(i).toString());
			}
		}
	}

	/**
	 * 将cookie写入到文件
	 */
	public void writeCookies2File() {
		Config config = new Config();
		List<Cookie> cookies = cookieStore.getCookies();
		String c = "";
		for (int i = 0; i < cookies.size(); i++) {

			Cookie ck = cookies.get(i);
			c += ck.getName() + "," + ck.getValue() + "," + ck.getDomain() + "," + ck.getPath() + ";";
		}
		if (c.endsWith(";"))
			c = c.substring(0, c.length() - 1);
		FileUtil.saveAs(c, config.getCookiePath() + config.getUserName() + "_12306Session.txt");
	}

	public static void resetCookiesFile() {
		Config config = new Config();
		FileUtil.saveAs("", config.getCookiePath() + config.getUserName() + "_12306Session.txt");
	}

	public static void resetCookieStore() {
		cookieStore.clear();
	}

	public static Map checkUser() {
		Map<String, Object> map = new HashMap<String, Object>();
		HttpUriRequest getDevice = RequestBuilder.post().setUri(Api.uamtkStatic).addHeader("Host", "kyfw.12306.cn")
				.addHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
				.addHeader("Origin", "https://kyfw.12306.cn")
				.addHeader("Referer", "https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc")
				.addParameter("appid", "otn").build();

		CloseableHttpResponse response2 = null;// httpclient.execute(getDevice);
		try {
			response2 = httpclient.execute(getDevice);
			HttpEntity entity = response2.getEntity();

			String jsonStr = EntityUtils.toString(entity);
			map = JSON.parseObject(jsonStr, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != response2) {
				try {
					response2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return map;
	}
}
