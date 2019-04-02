package com.easyticket.core;

import java.util.HashMap;
import java.util.Map;

public class SeatType {
	public static Map<String, String> seatMap = new HashMap<String, String>();

	public static void inint() {
		seatMap.put("二等", "O");
		seatMap.put("一等", "M");
		seatMap.put("硬卧", "3");
		seatMap.put("硬座", "1");
	}
	
	public static String getSeat(String seat){
		return seatMap.get(seat);
	}

}
