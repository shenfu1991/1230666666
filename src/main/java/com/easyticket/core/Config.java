package com.easyticket.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.kit.PropKit;

@SuppressWarnings("serial")
public class Config implements Serializable {

	/**
	 * cookie保存的路径
	 */
	private static String cookiePath;

	/**
	 * 账号
	 */
	private static String userName;

	/**
	 * 密码
	 */
	private static String password;

	/**
	 * 出发时间
	 */
	private static String stationDate;

	/**
	 * 出发地
	 */
	private static String stationLeft;

	/**
	 * 目的地
	 */
	private static String stationArrive;

	/**
	 * 乘车人
	 */
	private static String[] menbers;

	/**
	 * 购买的席别
	 */
	private static String[] seats;

	/**
	 * 指定车次，不指定就是全部
	 */
	private static List<String> trainNumber;

	/**
	 * 余票查询地址
	 */
	private static String queryUrl;
	
	/**
	 * 语音通知地址
	 */
	private static String voiceNoticeUrl;
	
	/**
	 * 语音通知 appcode
	 */
	private static String voiceAppcode;
	
	/**
	 * 需要通知的手机号码
	 */
	private static String voiceNoticePhone;

	private static Map<String, Integer> trainSeatMap = new ConcurrentHashMap<String, Integer>();
	private static Map<String, Long> trainSeatTimeMap = new ConcurrentHashMap<String, Long>();

	private static Map<String, String> blacklistMap = new ConcurrentHashMap<>();

	public static String getQueryUrl() {
		return queryUrl;
	}

	public static void setQueryUrl(String queryUrl) {
		Config.queryUrl = queryUrl;
	}

	public static String getCookiePath() {
		return PropKit.get("cookie.path");
	}

	public static void setCookiePath(String cookiePath) {
		cookiePath = cookiePath;
	}

	public static String getUserName() {
		return PropKit.get("12306.user.name");
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public static String getPassword() {
		return PropKit.get("12306.user.password");
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static String getStationDate() {
		return PropKit.get("station.date");
	}

	public void setStationDate(String stationDate) {
		this.stationDate = stationDate;
	}

	public static String getStationLeft() {
		return PropKit.get("station.left");
	}

	public void setStationLeft(String stationLeft) {
		this.stationLeft = stationLeft;
	}

	public static String getStationArrive() {
		return PropKit.get("station.arrive");
	}

	public void setStationArrive(String stationArrive) {
		this.stationArrive = stationArrive;
	}

	public static String[] getMenbers() {
		return PropKit.get("menbers").split(",");
	}

	public static void setMenbers(String[] menbers) {
		Config.menbers = menbers;
	}

	public static String[] getSeats() {
		return PropKit.get("seats").split(",");
	}

	public static void setSeats(String[] seats) {
		Config.seats = seats;
	}

	public static List<String> getTrainNumber() {
		return Arrays.asList(PropKit.get("train.numbers").replaceAll(" ", "").split(","));
	}

	public static void setTrainNumber(List<String> trainNumber) {
		Config.trainNumber = trainNumber;
	}

	public static Map<String, Integer> getTrainSeatMap() {
		return trainSeatMap;
	}

	public static void setTrainSeatMap(Map<String, Integer> trainSeatMap) {
		Config.trainSeatMap = trainSeatMap;
	}

	public static Map<String, Long> getTrainSeatTimeMap() {
		return trainSeatTimeMap;
	}

	public static void setTrainSeatTimeMap(Map<String, Long> trainSeatTimeMap) {
		Config.trainSeatTimeMap = trainSeatTimeMap;
	}

	public static Map<String, String> getBlacklistMap() {
		return blacklistMap;
	}

	public static void setBlacklistMap(Map<String, String> blacklistMap) {
		Config.blacklistMap = blacklistMap;
	}

	public static String getVoiceNoticeUrl() {
		return PropKit.get("voice.notice.url");
	}

	public static void setVoiceNoticeUrl(String voiceNoticeUrl) {
		Config.voiceNoticeUrl = voiceNoticeUrl;
	}

	public static String getVoiceAppcode() {
		return PropKit.get("voice.notice.appcode");
	}

	public static void setVoiceAppcode(String voiceAppcode) {
		Config.voiceAppcode = voiceAppcode;
	}

	public static String getVoiceNoticePhone() {
		return PropKit.get("voice.notice.phone");
	}

	public static void setVoiceNoticePhone(String voiceNoticePhone) {
		Config.voiceNoticePhone = voiceNoticePhone;
	}
	
	

}
