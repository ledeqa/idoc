package com.idoc.util.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;

import com.idoc.constant.LogConstant;

public class MailUtil {
	
	// 定义发件人、收件人、SMTP服务器、用户名、密码、主题、内容等
	private String displayName;
	private String to;
	private String cc;
	private String from;
	private String smtpServer;
	private String username;
	private String password;
	private String subject;
	private String content;
	private boolean ifAuth; // 服务器是否要身份认证
	private String filename = "";
	private List<String> attachments = new ArrayList<String>(); // 用于保存发送附件的文件名的集合
	
	/**
	 * 设置SMTP服务器地址
	 */
	public void setSmtpServer(String smtpServer) {

		this.smtpServer = smtpServer;
	}
	
	/**
	 * 设置发件人的地址
	 */
	public void setFrom(String from) {

		this.from = from;
	}
	
	/**
	 * 设置显示的名称
	 */
	public void setDisplayName(String displayName) {

		this.displayName = displayName;
	}
	
	/**
	 * 设置服务器是否需要身份认证
	 */
	public void setIfAuth(boolean ifAuth) {

		this.ifAuth = ifAuth;
	}
	
	/**
	 * 设置E-mail用户名
	 */
	public void setUserName(String username) {

		this.username = username;
	}
	
	/**
	 * 设置E-mail密码
	 */
	public void setPassword(String password) {

		this.password = password;
	}
	
	/**
	 * 设置接收者
	 */
	public void setTo(String to) {

		this.to = to;
	}
	
	/**
	 * 设置抄送
	 */
	public void setCc(String cc) {

		this.cc = cc;
	}
	
	/**
	 * 设置主题
	 */
	public void setSubject(String subject) {

		this.subject = subject;
	}
	
	/**
	 * 设置主体内容
	 */
	public void setContent(String content) {

		this.content = content;
	}
	
	/**
	 * 该方法用于收集附件名
	 */
	public void addAttachfile(String fname) {

		attachments.add(fname);
	}
	
	public void setAttachfiles(List<String> fileNames) {

		attachments = fileNames;
	}
	
	public MailUtil() {

		ConfigResource cfg = new ConfigResource("mailConfig");
		this.smtpServer = cfg.getConfigValueFromKey("mail.smtp.host");
		this.ifAuth = cfg.getConfigValueFromKey("mail.smtp.auth").equals("true") ? true : false;
		this.username = cfg.getConfigValueFromKey("mail.smtp.user");
		this.password = cfg.getConfigValueFromKey("mail.smtp.pass");
		this.from = cfg.getConfigValueFromKey("mail.from.addr");
		this.displayName = cfg.getConfigValueFromKey("mail.from.display");
		this.to = cfg.getConfigValueFromKey("mail.to");
		this.cc = cfg.getConfigValueFromKey("mail.cc");
		this.subject = cfg.getConfigValueFromKey("mail.subject");
	}
	
	/**
	 * 初始化SMTP服务器地址、发送者E-mail地址、用户名、密码、接收者、主题、内容
	 */
	public MailUtil(String smtpServer, String from, String displayName, String username, String password, String to, String cc, String subject, String content) {

		this.smtpServer = smtpServer;
		this.from = from;
		this.displayName = displayName;
		this.ifAuth = true;
		this.username = username;
		this.password = password;
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.content = content;
	}
	
	/**
	 * 初始化SMTP服务器地址、发送者E-mail地址、接收者、主题、内容
	 */
	public MailUtil(String smtpServer, String from, String displayName, String to, String cc, String subject, String content) {

		this.smtpServer = smtpServer;
		this.from = from;
		this.displayName = displayName;
		this.ifAuth = false;
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.content = content;
	}
	
	/**
	 * 发送邮件
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public HashMap send() {

		HashMap map = new HashMap();
		map.put("state", "success");
		String message = "邮件发送成功！";
		Session session = null;
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpServer);
		if (ifAuth) { // 服务器需要身份认证
			props.put("mail.smtp.auth", "true");
			SmtpAuth smtpAuth = new SmtpAuth(username, password);
			session = Session.getDefaultInstance(props, smtpAuth);
		} else {
			props.put("mail.smtp.auth", "false");
			session = Session.getDefaultInstance(props, null);
		}
		session.setDebug(true);
		try {
			Message msg = new MimeMessage(session);
			try {
				Address from_address = new InternetAddress(from, displayName);
				msg.setFrom(from_address);
			} catch (java.io.UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			msg.addRecipients(Message.RecipientType.TO, pakckgeEmail(to));
			if (StringUtils.isNotBlank(cc)) {
				msg.addRecipients(Message.RecipientType.CC, pakckgeEmail(cc));
			}
			msg.setSubject(subject);
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setContent(content.toString(), "text/html;charset=utf-8");
			mp.addBodyPart(mbp);
			if (null != attachments && !attachments.isEmpty()) {// 有附件
				Iterator<String> it = attachments.iterator();
				while (it.hasNext()) {
					mbp = new MimeBodyPart();
					filename = it.next(); // 选择出每一个附件名
					FileDataSource fds = new FileDataSource(filename); // 得到数据源
					mbp.setDataHandler(new DataHandler(fds)); // 得到附件本身并至入BodyPart
					mbp.setFileName(fds.getName()); // 得到文件名同样至入BodyPart
					mp.addBodyPart(mbp);
				}
				attachments.clear();
			}
			msg.setContent(mp); // Multipart加入到信件
			msg.setSentDate(new Date()); // 设置信件头的发送日期
			// 发送信件
			msg.saveChanges();
			// trans = session.getTransport("smtp");
			// trans.connect(smtpServer, username, password);
			// trans.sendMessage(msg, msg.getAllRecipients());
			// trans.close();
			Transport.send(msg);
			
		} catch (AuthenticationFailedException e) {
			map.put("state", "failed");
			message = "邮件发送失败！错误原因：\n" + "身份验证错误!";
			e.printStackTrace();
		} catch (MessagingException e) {
			message = "邮件发送失败！错误原因：\n" + e.getMessage();
			map.put("state", "failed");
			e.printStackTrace();
			Exception ex = null;
			if ((ex = e.getNextException()) != null) {
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
		}
		map.put("message", message);
		return map;
	}
	
	private InternetAddress[] pakckgeEmail(String to) {

		String[] toArr = to.split(";");
		InternetAddress[] address = new InternetAddress[toArr.length];
		for (int i = 0; i < toArr.length; i++) {
			try {
				address[i] = new InternetAddress(toArr[i]);
			} catch (AddressException e) {
				LogConstant.debugLog.error("pakckgeEmail------AddressException", e);
			}
		}
		return address;
	}
	
}
