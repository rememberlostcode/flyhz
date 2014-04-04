
package com.flyhz.framework.util;

import javax.annotation.Resource;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 邮件发送工具类
 * 
 * @author fuwb 20140404
 */
public class MailUtil {
	@Resource(name = "mailSender")
	private JavaMailSender	mailSender;

	/**
	 * 发送文本文件
	 * 
	 * @param
	 * @return
	 */
	public void sendTextMail() {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom("付文斌163");
		simpleMailMessage.setTo("306168661@qq.com");
		simpleMailMessage.setSubject("First Email from qq");
		simpleMailMessage.setText("Test SendTextMail!");
		mailSender.send(simpleMailMessage);
	}

	public static void main(String[] args) {
		MailUtil mailUtil = new MailUtil();
		mailUtil.sendTextMail();
	}
}
