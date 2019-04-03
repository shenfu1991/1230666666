package com.easyticket.core;

import java.util.HashMap;
import java.util.Map;
/**
 * 座位代码
 * 商务座(9),特等座(P),一等座(M),二等座(O),高级软卧(6),软卧(4),硬卧(3),软座(2),硬座(1),无座(1)
 * @author lenovo
 *
 */
public class SeatType {
	
	public static Map<String, String> seatMap = new HashMap<String, String>();

	public static void inint() {
		seatMap.put("二等座", "O");
		seatMap.put("一等座", "M");
		seatMap.put("硬卧", "3");
		seatMap.put("硬座", "1");
		seatMap.put("商务座", "9");
		seatMap.put("特等座", "P");
		seatMap.put("高级软卧", "6");
		seatMap.put("软卧", "4");
		seatMap.put("软座", "2");
		seatMap.put("无座", "1");
	}
	
	public static String getSeat(String seat){
		return seatMap.get(seat);
	}

}
