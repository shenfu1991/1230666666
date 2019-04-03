package com.easyticket.notice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyticket.core.Config;
import com.easyticket.core.HeaderSotre;
import com.easyticket.util.HttpClientUtil;
import com.jfinal.kit.PropKit;

/**
 * 语音通知
 * 
 * @author lenovo
 *恭喜您抢到#code1#从#code2#开往#code3#的#code4#次列车，请去官网及时支付！ 
 */
public class Notice {
	
	private static final Logger logger = Logger.getLogger(Notice.class);
	
	

	public void run(String content) {
		try {
			if(StringUtils.isBlank(Config.getVoiceNoticePhone()) || StringUtils.isBlank(Config.getVoiceAppcode()) || StringUtils.isBlank(Config.getVoiceNoticeUrl())){
				return;
			}
			Map<String, String> params = new HashMap<>();
			params.put("tpl_id", "TP19040316");
			params.put("phone", Config.getVoiceNoticePhone());
			params.put("param", content);
			Header[] headers = new BasicHeader[1];
			headers[0] = new BasicHeader("Authorization", "APPCODE " + Config.getVoiceAppcode());
			String result = HttpClientUtil.sendPost(Config.getVoiceNoticeUrl(), params, headers);
			JSONObject jsonObject = JSON.parseObject(result);
			if(jsonObject!=null && jsonObject.getString("return_code").equals("00000")){
				logger.error("购票成功语音通知发送成功，手机："+Config.getVoiceNoticePhone());
			}
		} catch (Exception e) {
			logger.error("发送语音通知失败！",e);
		}

	}
	
	

}
