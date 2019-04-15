package com.easyticket.notice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfplugin.mail.MailKit;

public class EmailNotice {

	public void run(String content) {
		if (StringUtils.isBlank(PropKit.get("email.notice.mall")) || StringUtils.isBlank(PropKit.get("username"))
				|| StringUtils.isBlank(PropKit.get("password"))) {
			return;
		}
		List<String> list = new ArrayList<>();
		list.add(PropKit.get("email.notice.mall"));
		MailKit.send(PropKit.get("email.notice.mall"), list, "购票成功提醒", content);
		LogKit.info("邮件发送成功！");
	}

}
