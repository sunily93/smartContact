package com.school.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.school.entity.User;
import com.school.helper.Message;
import com.school.repository.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder password;
	@Autowired
	private UserRepository userRepo;
	
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	@RequestMapping("/singup")
	public String signup(Model model)
	{
		model.addAttribute("title","Signup - Smart Contact Manager");
		model.addAttribute("user",new User());
		return "singup";
	}
	
	@RequestMapping(value="/do_register",method=RequestMethod.POST)	
	public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result,
			@RequestParam(value="aggrement",defaultValue="false")boolean aggrement,
			Model model,HttpSession session)
	{
		try {
			if(!aggrement)
			{
				throw new Exception("you have not aggred");
			}
			if(result.hasErrors())
			{
				System.out.println("ERROR "+result.toString());
				model.addAttribute("user",user);
				return "singup";
			}
			
			System.out.println("aggrement "+aggrement);
			System.out.println("user "+user);
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(password.encode(user.getPassword()));
			User save = userRepo.save(user);
			model.addAttribute("user",save);
			session.setAttribute("message", new Message("Successfully Register", "alert-success"));
			return "singup";
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "alert-danger"));
			return "singup";
		}
	}
	
	@RequestMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "Login Page");
		return "login";
	}
}
