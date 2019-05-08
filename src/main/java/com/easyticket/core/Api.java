package com.easyticket.core;

/**
 * 12306订票接口
 * 
 * @author lenovo
 *
 */
public interface Api {

	String baseUrl = "https://kyfw.12306.cn";

	String leftTicketByCdn = "http://%s/otn/%s?leftTicketDTO.train_date=%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=ADULT";

	String leftTicket = baseUrl
			+ "/otn/leftTicket/%s?leftTicketDTO.train_date=%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=ADULT";

	String login = baseUrl + "/passport/web/login";

	String userLogin = baseUrl + "/otn/login/userLogin";

	String conf = baseUrl + "/otn/login/conf";

	String captchaImage = baseUrl + "/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&_=%s";

	String captchaCheck = baseUrl + "/passport/captcha/captcha-check?answer=%s&rand=sjrand&login_site=E&_=%s";

	String uamtk = baseUrl + "/passport/web/auth/uamtk";

	String uamauthclient = baseUrl + "/otn/uamauthclient";

	String uamtkStatic = baseUrl + "/passport/web/auth/uamtk-static";

	String userInfo = baseUrl + "/otn/modifyUser/initQueryUserInfoApi";

	String getPassengerDTOs = baseUrl + "/otn/confirmPassenger/getPassengerDTOs";

	String submitOrderRequest = baseUrl + "/otn/leftTicket/submitOrderRequest";

	String checkOrderInfo = baseUrl + "/otn/confirmPassenger/checkOrderInfo";

	String initDc = baseUrl + "/otn/confirmPassenger/initDc"; // 生成订单时需要先请求这个页面

	String getQueueCount = baseUrl + "/otn/confirmPassenger/getQueueCount";

	String confirmSingleForQueue = baseUrl + "/otn/confirmPassenger/confirmSingleForQueue";

	String queryOrderWaitTime = baseUrl + "/otn/confirmPassenger/queryOrderWaitTime?%s";

	String queryInitPage = baseUrl + "/otn/leftTicket/init";

	String loginInitPage = baseUrl + "/otn/login/init";

	String browserDeviceId = baseUrl + "/otn/HttpZF/logdevice";

	String initMy12306 = baseUrl + "/otn/index/initMy12306";

	String getBrowserDeviceId = baseUrl + "/otn/HttpZF/logdevice";

	String loginAysnSuggest = baseUrl + "/otn/login/loginAysnSuggest";

	String autoSubmitOrderRequest = baseUrl + "/otn/confirmPassenger/autoSubmitOrderRequest";

	// 360验证码识别
	String getCheck = "http://120.27.239.134:9082/api/v2/getCheck";

	String getPositionBy360 = "http://check.huochepiao.360.cn/img_vcode";

	// 验证码识别 机器学习
	String getPositionByAi = "https://12306.jiedanba.cn/api/v2/img_vcode";

}
