package com.school.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.school.entity.User;
import com.school.helper.Message;
import com.school.repository.UserRepository;
import com.school.service.EmailService;

@Controller
public class ForgotController {

	Random random=new Random(1000);

	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private BCryptPasswordEncoder password;
	
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot-email";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("Email "+email);
		//genrating otp of 4 digit
		
		int otp = random.nextInt(999999);
		
		System.out.println("Otp "+otp);
		
		//write code for send otp to email
		String sub="OTP from SCM";
		String msg="<div style='border:1px solid #e2e2e2;padding:20px'>"
				+ "<h1>"
				+ "OTP is "
				+ "<b>"+otp				
				+ "</b>"
				+ "/n"
				+ "</h1>"
				+ "</div>";
		
		String to=email;
		boolean flag = emailService.sendEmail(sub, msg, to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verifyOtp";
		}else {
			session.setAttribute("message", "Check Your Email....");
			return "forgot-email";
		}
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session)
	{
		int myOtp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myOtp==otp)
		{
			//password change form
			User user = userRepo.getUserByEmail(email);
			if(user==null)
			{
				//send error msg
				session.setAttribute("message", "Check Your Email....");
				return "forgot-email";
			}else {
				//send password change
			}
			return "password_change_form";
		}else {
			session.setAttribute("message", "You have entered wrong otp");
			return "verifyOtp";
		}
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPass")String newPass,HttpSession session)
	{
		String email=(String)session.getAttribute("email");
		User user = userRepo.getUserByEmail(email);
		user.setPassword(password.encode(newPass));
		userRepo.save(user);
		return "redirect:/signin?change=password changed successfully....";
	}
	
}
