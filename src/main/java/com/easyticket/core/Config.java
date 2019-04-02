package com.easyticket.core;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.kit.PropKit;

@SuppressWarnings("serial")
public class Config implements Serializable {

	private static String checkLoginStatus;

	private static String CookiePath;

	private static String userName;

	private static String password;

	private static String cdnEnabled;

	private static String stationDate;

	private static String stationLeft;

	private static String stationArrive;

	private static String[] menbers;

	private static String allowLessNumber;

	private  static String[] seats;

	private  static String[] trainNumber;

	private static String queryUrl;
	
	private static Map<String, Integer> trainSeatMap = new ConcurrentHashMap<String, Integer>();
	private static Map<String, Long> trainSeatTimeMap = new ConcurrentHashMap<String, Long>();
	
	private static Map<String, String> blacklistMap =  new ConcurrentHashMap<>();

	public static String getQueryUrl() {
		return queryUrl;
	}

	public static void setQueryUrl(String queryUrl) {
		Config.queryUrl = queryUrl;
	}

	public String getCheckLoginStatus() {
		return PropKit.get("check.login.status");
	}

	public void setCheckLoginStatus(String checkLoginStatus) {
		this.checkLoginStatus = checkLoginStatus;
	}

	public String getCookiePath() {
		return PropKit.get("cookie.path");
	}

	public void setCookiePath(String cookiePath) {
		CookiePath = cookiePath;
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

	public String getCdnEnabled() {
		return PropKit.get("cdn.enabled");
	}

	public void setCdnEnabled(String cdnEnabled) {
		this.cdnEnabled = cdnEnabled;
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

	public String getAllowLessNumber() {
		return PropKit.get("allow.less.number");
	}

	public void setAllowLessNumber(String allowLessNumber) {
		this.allowLessNumber = allowLessNumber;
	}

	public static String[] getSeats() {
		return PropKit.get("seats").split(",");
	}

	public static void setSeats(String[] seats) {
		Config.seats = seats;
	}

	public static String[] getTrainNumber() {
		return PropKit.get("train.number").split(",");
	}

	public static void setTrainNumber(String[] trainNumber) {
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

}
