
package com.flyhz.framework.lang.mail;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

/**
 * 邮件发送组件
 * 
 * @author fuwb 20140411
 */
public interface MailRepository {
	/**
	 * 发送文本邮件
	 * 
	 * @param simpleMailMessage
	 * @return
	 */
	public void sendTextMail(SimpleMailMessage simpleMailMessage);

	/**
	 * 发送HTML邮件
	 * 
	 * @param mimeMessage
	 * @return
	 */
	public void sendHtmlMail(MimeMessage mimeMessage);
}
