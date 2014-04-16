
package com.flyhz.framework.lang.mail;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailRepositoryImpl implements MailRepository {
	@Resource
	private JavaMailSenderImpl	mailSender;

	@Override
	public void sendTextMail(SimpleMailMessage simpleMailMessage) {
		simpleMailMessage.setFrom(mailSender.getUsername());
		mailSender.send(simpleMailMessage);
	}

	@Override
	public void sendHtmlMail(MimeMessage mimeMessage) {
		mailSender.send(mimeMessage);
	}

	public static void main(String[] args) {
		// MailRepository mailRepository = new MailRepositoryImpl();
		// SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		// simpleMailMessage.setTo("306168661@qq.com");
		// simpleMailMessage.setSubject("Test mail send");
		// simpleMailMessage.setSentDate(new Date());
		// simpleMailMessage.setText("I will send mail to myself to test");
		// mailRepository.sendTextMail(simpleMailMessage);
	}
}
