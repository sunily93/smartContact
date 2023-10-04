package com.school.service;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String sub,String msg,String to)
	{
		boolean f=false;
		String from="sunily9310@gmail.com";
		
		//variable for email
		String host="smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		System.out.println("Properties "+properties);
		
		//setting important info
		
		//set host
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//step 1:- get the session obj
		Session session=Session.getInstance(properties,new Authenticator() {
		  @Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("sunily9310@gmail.com", "jyrtgpcdysgrbukc");
		}
		}); 
		session.setDebug(true);
		
		//step 2:- compose the msg(text,multi media)
		MimeMessage m=new MimeMessage(session);
		try {
			//from email
			m.setFrom(from);
			
			//adding recipient to msg
			m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			
			//adding sub to msg
			m.setSubject(sub);
			
			//m.setText(msg);
			m.setContent(msg,"text/html");
			//step 3:- send the  message using transport
			Transport.send(m);
			System.out.println("send success..........");
			f=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
