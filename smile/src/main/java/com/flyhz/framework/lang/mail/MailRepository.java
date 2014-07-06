
package com.flyhz.framework.lang.mail;

import java.util.Map;

/**
 * 邮件发送组件
 * 
 * @author fuwb 20140411
 */
public interface MailRepository {
	/**
	 * 发送模板邮件
	 * 
	 * @param to
	 * @param subject
	 * @param templateName
	 * @param model
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public void sendWithTemplate(String to, String subject, String templateName, Map model);

	/**
	 * 发送普通文本邮件
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 * @return
	 */
	public void sendText(String to, String subject, String text);

	/**
	 * 发送普通HTML邮件
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 * @return
	 */
	public void sendHtml(String to, String subject, String text);

	/**
	 * 发送带图片的普通HTML邮件
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 * @param imagePath
	 * @return
	 */
	public void sendHtmlWithImage(String to, String subject, String text, String imagePath);

	/**
	 * 发送带附件的普通HTML邮件
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 * @param imagePath
	 * @return
	 */
	public void sendHtmlWithAttachment(String to, String subject, String text, String filePath);
}
