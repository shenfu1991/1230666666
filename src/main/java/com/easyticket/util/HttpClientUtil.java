package com.easyticket.util;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClientUtil {

	private static final Logger logger = Logger.getLogger(HttpClientUtil.class);

	public static CloseableHttpClient getClient() {
		CloseableHttpClient httpClient = null;
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();

			httpClientBuilder.setSSLContext(sslContext);
			httpClient = httpClientBuilder.build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return httpClient;
	}

	public static CloseableHttpClient getHttpClient(BasicCookieStore cookieStore) {
		return HttpClients.custom().setDefaultCookieStore(cookieStore).build();
	}

	/**
	 * 发送HttpGet请求
	 * 
	 * @param url
	 * @return
	 */
	public static String sendGet(String url) {

		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = getClient().execute(httpget);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String result = null;
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity);
			}
		} catch (ParseException | IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * 发送HttpPost请求，参数为map
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> map) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = getClient().execute(httppost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity1 = response.getEntity();
		String result = null;
		try {
			result = EntityUtils.toString(entity1);
		} catch (ParseException | IOException e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 发送HttpPost请求，参数为map
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> map, Header[] headers) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost(url);
		for (Header header : headers) {
			httppost.addHeader(header);
		}
		httppost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = getClient().execute(httppost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity1 = response.getEntity();
		String result = null;
		try {
			result = EntityUtils.toString(entity1);
		} catch (ParseException | IOException e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 发送不带参数的HttpPost请求
	 * 
	 * @param url
	 * @return
	 */
	public static String sendPost(String url) {
		HttpPost httppost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			response = getClient().execute(httppost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		String result = null;
		try {
			result = EntityUtils.toString(entity);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param json
	 * @return
	 */
	public static String sendPost(String url, String json, String contentType) {
		CloseableHttpClient httpclient = getClient();
		HttpPost post = new HttpPost(url);
		try {
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			if (StringUtils.isBlank(contentType)) {
				s.setContentType("application/json");
			} else {
				s.setContentType(contentType);
			}
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			System.err.println(res);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(res.getEntity());// 返回json格式：
				return result;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}
}
