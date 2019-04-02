package com.easyticket.config;

import java.util.Timer;

import org.apache.log4j.Logger;

import com.easyticket.Main;
import com.easyticket.controller.IndexController;
import com.easyticket.core.CdnManage;
import com.easyticket.core.CheckCdn;
import com.easyticket.core.InitLeftQueryUrl;
import com.easyticket.job.CheckLogin;
import com.easyticket.job.ShuaPiaoJob;
import com.easyticket.query.QueryTicket;
import com.easyticket.station.Stations;
import com.easyticket.thread.SimpleThreadLocalPool;
import com.easyticket.user.Login;
import com.easyticket.util.HttpClientUtil;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.json.JFinalJsonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.quartz.QuartzPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

public class AppConfig extends JFinalConfig {
	
	

	private static final Logger logger = Logger.getLogger(AppConfig.class);

	@Override
	public void configConstant(Constants me) {
		PropKit.use("application.properties");
		me.setInjectDependency(true);
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setViewType(ViewType.JFINAL_TEMPLATE);
		me.setMaxPostSize(200 * 1024 * 1024);
		me.setJsonFactory(new JFinalJsonFactory());

		aopMapping();
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/index", IndexController.class);
	}

	public static DruidPlugin createDruidPlugin() {
		DruidPlugin mysqlPool = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		mysqlPool.setDriverClass(PropKit.get("className"));
		mysqlPool.setInitialSize(PropKit.getInt("initialPoolSize")).setMinIdle(PropKit.getInt("minPoolSize"))
				.setMaxActive(PropKit.getInt("maxPoolSize"));
		mysqlPool.setMaxWait(PropKit.getLong("maxIdleTime"));
		mysqlPool.setRemoveAbandonedTimeoutMillis(PropKit.getLong("removeAbandonedTimeoutMillis"));
		mysqlPool.setRemoveAbandoned(PropKit.getBoolean("removeAbandoned"));
		mysqlPool.setLogAbandoned(PropKit.getBoolean("logAbandoned"));
		mysqlPool.setTestWhileIdle(true);
		mysqlPool.setTestOnBorrow(false);
		mysqlPool.setTestOnReturn(false);
		mysqlPool.setMaxPoolPreparedStatementPerConnectionSize(100);
		return mysqlPool;
	}

	@Override
	public void configPlugin(Plugins me) {
		QuartzPlugin quartzPlugin = new QuartzPlugin();
		  quartzPlugin.addJob(new CheckLogin().cron("0 0/2 * * * ?"));
		// quartzPlugin.addJob(new ShuaPiaoJob().cron("*/1 * * * * ?"));
		  me.add(quartzPlugin);
	}

	@Override
	public void configInterceptor(Interceptors me) {

	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx"));
	}

	@Override
	public void configEngine(Engine me) {

	}

	@Override
	public void afterJFinalStart() {

	}

	// 系统启动完成后回调
	@Override
	public void onStart() {
		new Main().main();

	}

	// 系统关闭之前回调
	@Override
	public void onStop() {

	}

	private void aopMapping() {

	}

}
