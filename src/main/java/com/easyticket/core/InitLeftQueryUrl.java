package com.easyticket.core;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.easyticket.util.HttpClientUtil;

/**
 * 动态获取查询的url
 * 
 * @author lenovo
 *
 */
public class InitLeftQueryUrl {

	private static final Logger logger = Logger.getLogger(InitLeftQueryUrl.class);

	public static void init() {
		Header[] headers = new BasicHeader[7];
		headers[0] = new BasicHeader("User-Agent", HeaderSotre.userAgent);
		headers[1] = new BasicHeader("Host", HeaderSotre.host);
		headers[2] = new BasicHeader("Referer", Api.loginInitPage);
		headers[3] = new BasicHeader("Accept", "*/*");
		headers[4] = new BasicHeader("Accept-Encoding", "gzip, deflate");
		headers[5] = new BasicHeader("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3");
		headers[6] = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
		String responseBody = "";
		try {
			HttpUriRequest confirm = RequestBuilder.post().setUri(new URI(Api.queryInitPage)).addHeader(headers[0])
					.addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
					.addHeader(headers[5]).addHeader(headers[6]).addParameter("_json_att", "").build();
			CloseableHttpResponse response = HttpClientUtil.getClient().execute(confirm);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				responseBody = EntityUtils.toString(entity);

				Pattern p = Pattern.compile("CLeftTicketUrl \\= '(.*?)';");
				Matcher m = p.matcher(responseBody);
				while (m.find()) {
					String queryUrl = m.group(1);
					
					Config.setQueryUrl(queryUrl);
					logger.info("初始化查询地址完成，地址是：[" + queryUrl + "]");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
