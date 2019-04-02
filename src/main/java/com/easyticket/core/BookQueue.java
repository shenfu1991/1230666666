package com.easyticket.core;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BookQueue {

	public static final BlockingQueue<Map<String, String>> bookQueue = new LinkedBlockingQueue<Map<String, String>>(10);

}
