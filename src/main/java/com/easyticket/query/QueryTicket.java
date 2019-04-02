package com.easyticket.query;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyticket.book.TicketBook;
import com.easyticket.core.Api;
import com.easyticket.core.BookQueue;
import com.easyticket.core.CdnManage;
import com.easyticket.core.Config;
import com.easyticket.core.CookieStore;
import com.easyticket.core.HeaderSotre;
import com.easyticket.station.Stations;
import com.easyticket.util.HttpClientUtil;

/**
 * 车票查询
 * 
 * @author lenovo
 *
 */
public class QueryTicket implements Runnable{

	private static final Logger logger = Logger.getLogger(QueryTicket.class);

	BasicCookieStore cookieStore = CookieStore.cookieStore;
	CloseableHttpClient httpclient = null;


	

	@Override
	public void run() {
		
			try {
				Map<String, Integer> trainSeatMap = Config.getTrainSeatMap();
				Map<String, Long> trainSeatTimeMap = Config.getTrainSeatTimeMap();
				long startTime = System.currentTimeMillis();
				httpclient = HttpClientUtil.getHttpClient(cookieStore);
				RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)
						.setConnectionRequestTimeout(3000).setSocketTimeout(3000).build();
				String cdnIp = CdnManage.getIp();

				String queryResult = query(cdnIp, requestConfig, Config.getStationDate());
				Map rsmap = JSON.parseObject(queryResult, Map.class);
				Map data = (Map) rsmap.get("data");
				if (data.size() > 0) {
					List<String> arr = (List<String>) data.get("result");
					Map<String, Map<String, String>> map = new ConcurrentHashMap<String, Map<String, String>>();
					// 解析车次信息
					analysisTicket(arr, map);
					List<String> trainNumber = new ArrayList<>();
					Set<String> keys = map.keySet();
					for (String key : keys) {
						trainNumber.add(key);
					}
					List<Map<String, String>> youpiao = getSecretStr(map, trainNumber, Config.getSeats());
					if (youpiao.size() > 0) {
						for (Map<String, String> map1 : youpiao) {
							String chehao = map1.get("chehao");
							String tobuySeat = map1.get("toBuySeat");
							Long shijian = trainSeatTimeMap.get(chehao + "_" + tobuySeat);
							Integer cishu = trainSeatMap.get(chehao + "_" + tobuySeat);
							if (null != shijian) {
								if (System.currentTimeMillis() > shijian) {
									trainSeatMap.put(chehao + "_" + tobuySeat, 0);
									cishu = 0;
								}
							}
							if (null != cishu) {
								if (cishu > 3) {
									// 放入小黑屋15秒
									trainSeatTimeMap.put(chehao + "_" + tobuySeat,
											System.currentTimeMillis() + 15 * 1000);
									trainSeatMap.put(chehao + "_" + tobuySeat, 0);
									logger.info(chehao + "_" + tobuySeat + "放入小黑屋15秒");
								} else {
									trainSeatMap.put(chehao + "_" + tobuySeat, cishu + 1);
									map1.put("cdn", cdnIp);
									BookQueue.bookQueue.put(map1);
									logger.info(String.format("查询到车票信息，车次%s有余票， [CDN轮查 %s ]", chehao,cdnIp));
									return ;
									
								}
							} else {
								BookQueue.bookQueue.put(map1);
								map1.put("cdn", cdnIp);
								logger.info(String.format("查询到车票信息，车次%s有余票， [CDN轮查 %s ]", chehao,cdnIp));
								trainSeatMap.put(chehao + "_" + tobuySeat, 0);
								return ;
							}

						}
						
						
					}else {
						logger.info(String.format("未查询到匹配需求的车票信息 [CDN轮查 %s]", cdnIp));
					}

				} else {
					logger.info(String.format("未查询到匹配需求的车票信息 [CDN轮查 %s]", cdnIp));
				}

			} catch (Exception e) {

			}
			
		
	}

	/**
	 * 订票
	 */
	public void bookTicket() {

	}

	private String query(String cdn, RequestConfig requestConfig, String date) {
		httpclient = HttpClientUtil.getHttpClient(cookieStore);
		String left = Stations.getStation(Config.getStationLeft());
		if (StringUtils.isBlank(left)) {
			logger.error("出发地错误！");
		}

		String arrive = Stations.getStation(Config.getStationArrive());

		if (StringUtils.isBlank(arrive)) {
			logger.error("目的地错误！");
		}

		String url = String.format(Api.leftTicketByCdn, cdn, Config.getQueryUrl(), date, left, arrive);
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.setHeader("Host", HeaderSotre.host);
			httpget.setHeader("User-Agent", HeaderSotre.userAgent);
			httpget.setHeader("X-Requested-With", "XMLHttpRequest");
			httpget.setHeader("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
			httpget.setConfig(requestConfig);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity, "UTF-8");
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return content;
			} else {
				logger.error("网络错误，状态码：" + response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			logger.error("网络错误");
		}
		return "";
	}

	/**
	 * 解析车次信息
	 * 
	 * @param checi
	 * @param map
	 */
	public void analysisTicket(List<String> checi, Map<String, Map<String, String>> map) {
		map.clear();

		/*
		 * 车次：3 train_no 2 start_station_code:起始站：4 end_station_code终点站：5
		 * from_station_code:出发站：6 to_station_code:到达站：7
		 * 
		 * start_time:出发时间：8 arrive_time:达到时间：9
		 * 
		 * 历时：10 leftTicket 12 train_location 15
		 * 
		 * 商务特等座：32 一等座：31 二等座：30 高级软卧：21 软卧：23 动卧：33 硬卧：28 软座：24 硬座：29 无座：26
		 * 其他：22 备注：1
		 * 
		 * start_train_date:车票出发日期：13
		 * 
		 * secretStr：0
		 */
		for (String a : checi) {
			Map<String, String> map1 = new ConcurrentHashMap<String, String>();
			String[] b = a.split("\\|");

			String secret = b[0];
			secret = URLDecoder.decode(secret);// 解码
			String chehao = b[3];
			map1.put("二等座", b[30]);
			map1.put("一等座", b[31]);
			map1.put("硬卧", b[28]);
			map1.put("硬座", b[29]);
			map1.put("secret", secret);
			map1.put("leftTicket", b[12]);
			map1.put("train_no", b[2]);
			map1.put("fromStationTelecode", b[6]);
			map1.put("toStationTelecode", b[7]);
			map1.put("train_location", b[15]);
			map1.put("chehao", chehao);
			map.put(chehao, map1);

		}
	}

	/**
	 * 获取车次str
	 * 
	 * @param map
	 * @param trains
	 * @param seats
	 *            二等 硬座等
	 * @return
	 */
	public List<Map<String, String>> getSecretStr(Map<String, Map<String, String>> map, List<String> trains,
			String[] seats) {
		String[] xb = seats;
		List<Map<String, String>> youpiaoList = new ArrayList<Map<String, String>>();
		for (String c : trains) {
			Map<String, String> xibiemap = map.get(c);
			if (null != xibiemap) {
				for (String xbie : xb) {
					String cnt = xibiemap.get(xbie);
					String secretStr = xibiemap.get("secret");
					if (null != cnt && !"无".equals(cnt) && !"".equals(cnt)) {
						if ("".equals(secretStr)) {
							System.out.println(c + " " + xbie + "未开售");
						} else if (!"有".equals(cnt) && Integer.parseInt(cnt) < Config.getMenbers().length) {
							System.out.println(c + " " + xbie + " 有票：" + cnt + "但是不够" + Config.getMenbers().toString()
									+ " " + Config.getMenbers().length + "个,忽略");
						} else {
							xibiemap.put("toBuySeat", xbie);
							youpiaoList.add(xibiemap);
						}
					}
				}

			}
		}
		return youpiaoList;
	}

}
