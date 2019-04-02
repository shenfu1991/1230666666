package com.easyticket;

import com.easyticket.config.AppConfig;
import com.jfinal.server.undertow.UndertowServer;

public class Application {

	public static void main(String[] args) {
		UndertowServer.start(AppConfig.class);
	}

}