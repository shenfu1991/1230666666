package com.easyticket.notice;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfplugin.mail.MailKit;

public class EmailNotice {

	public void run(String content) {
		List<String> list = new ArrayList<>();
		list.add(PropKit.get("email.notice.mall"));
		MailKit.send(PropKit.get("email.notice.mall"), list, "购票成功提醒", content);
		LogKit.info("邮件发送成功！");
	}

}
