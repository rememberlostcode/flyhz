
package com.flyhz.framework.lang.mail;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class MailRepositoryImpl implements MailRepository {
	@Resource
	private JavaMailSender	mailSender;
	@Resource
	private VelocityEngine	velocityEngine;
	@Value(value = "${smile.mail.username}")
	private String			from;
	@Value(value = "${smile.mail.chname}")
	private String			chname;

	@SuppressWarnings("rawtypes")
	public void sendWithTemplate(String to, String subject, String templateName, Map model) {
		StringBuffer fromEmail = new StringBuffer();
		fromEmail.append(chname).append("<").append(from).append(">");

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setFrom(fromEmail.toString());
		simpleMailMessage.setSubject(subject);
		String result = null;
		try {
			result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateName,
					"UTF-8", model);
		} catch (Exception e) {

		}
		simpleMailMessage.setText(result);
		mailSender.send(simpleMailMessage);
	}

	public void sendText(String to, String subject, String text) {
		StringBuffer fromEmail = new StringBuffer();
		fromEmail.append(chname).append("<").append(from).append(">");

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(fromEmail.toString());
		simpleMailMessage.setTo(to);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(text);
		mailSender.send(simpleMailMessage);
	}

	public void sendHtml(String to, String subject, String text) {
		StringBuffer fromEmail = new StringBuffer();
		fromEmail.append(chname).append("<").append(from).append(">");

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
		try {
			messageHelper.setTo(to);
			messageHelper.setFrom(fromEmail.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(text, true);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(mimeMessage);
	}

	public void sendHtmlWithImage(String to, String subject, String text, String imagePath) {
		StringBuffer fromEmail = new StringBuffer();
		fromEmail.append(chname).append("<").append(from).append(">");

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setTo(to);
			messageHelper.setFrom(fromEmail.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(text, true);
			// Content="<html><head></head><body><img src=\"cid:image\"/></body></html>";
			// 图片必须这样子：<img src='cid:image'/>
			FileSystemResource img = new FileSystemResource(new File(imagePath));
			messageHelper.addInline("image", img);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(mimeMessage);
	}

	public void sendHtmlWithAttachment(String to, String subject, String text, String filePath) {
		StringBuffer fromEmail = new StringBuffer();
		fromEmail.append(chname).append("<").append(from).append(">");

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setTo(to);
			messageHelper.setFrom(fromEmail.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(text, true);
			FileSystemResource file = new FileSystemResource(new File(filePath));
			messageHelper.addAttachment(file.getFilename(), file);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(mimeMessage);
	}

	public static void main(String[] args) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setPassword("syqf1027");
		mailSender.setUsername("service@tiantianhaigou.com");
		mailSender.setHost("smtp.ym.163.com");
		mailSender.setDefaultEncoding("utf-8");
		mailSender.setPort(25);
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		// simpleMailMessage.setFrom("service@tiantianhaigou.com");
		simpleMailMessage.setFrom("天天海狗<service@tiantianhaigou.com>");
		simpleMailMessage.setTo("syqf1027@yeah.net");
		simpleMailMessage.setSubject("测试邮件");
		simpleMailMessage.setText("testEmail");
		simpleMailMessage.setSentDate(new Date());
		mailSender.send(simpleMailMessage);
	}
}
