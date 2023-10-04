package com.school.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.school.entity.Contact;
import com.school.entity.User;
import com.school.helper.Message;
import com.school.repository.ContactRepostiory;
import com.school.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private ContactRepostiory contRepo;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String name = principal.getName();
		System.out.println(name);
		User user = repo.getUserByEmail(name);
		model.addAttribute("user",user);
		System.out.println(user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		model.addAttribute("title", "User Dashboard");
		return "normal/user-dashboard";
	}
	
	@GetMapping("/add-contact")
	public String addContact(Model model)
	{
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage")MultipartFile file,
			Principal principal,HttpSession session){
		try {
		String name = principal.getName();
		User user = repo.getUserByEmail(name);
		if(file.isEmpty())
		{
			//if file is empty then show msg
			System.out.println("file is empty");
		}else {
			contact.setImage(file.getOriginalFilename());
			
			File saveFile = new ClassPathResource("static/image").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded");
		}
		
		contact.setUser(user);
		user.getContacts().add(contact);
		repo.save(user);
		System.out.println(contact);
		session.setAttribute("message", new Message("Your contact is add", "success"));
		}catch(Exception e) {
			System.out.println("Error "+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong...", "danger"));
		}
		return "normal/add_contact";
	}
	
	//per page=5
	//current page=0
	@GetMapping("/show-contact/{page}")
	public String showContacts(@PathVariable("page")Integer page, Model model,Principal principal)
	{
		model.addAttribute("title", "Show Contact");
		String name = principal.getName();
		User user = repo.getUserByEmail(name);
		
		Pageable pageRequest = PageRequest.of(page, 5);
		
		Page<Contact> contactByUser = contRepo.findContactByUser(user.getId(),pageRequest);
		model.addAttribute("contacts",contactByUser);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", contactByUser.getTotalPages());
		return "normal/show-contact";
	}
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId")Integer cId,Model model,Principal principal)
	{
		Optional<Contact> findById = contRepo.findById(cId);
		Contact contact = findById.get();
		String name = principal.getName();
		User user = repo.getUserByEmail(name);
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		return "normal/contact_detail";
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")Integer cId,Model model,HttpSession session,Principal principal)
	{
		Optional<Contact> findById = contRepo.findById(cId);
		Contact contact = findById.get();
		//unlink user from contact
		//contact.setUser(null);
		User user = repo.getUserByEmail(principal.getName());
		user.getContacts().remove(contact);
		repo.save(user);
		//contRepo.delete(contact);
		session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
		return "redirect:/user/show-contact/0";
	}
	
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId")Integer cId,Model model)
	{
		model.addAttribute("title", "Update Form");
		Contact contact = contRepo.findById(cId).get();
		model.addAttribute("contact", contact);
		return "normal/updateForm";
	}
	
	@RequestMapping(value="/process-update",method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,Principal principal,
			@RequestParam("profileImage")MultipartFile file,Model model,HttpSession session)
	{
		try {
			//old contact details
			Contact oldContact = contRepo.findById(contact.getcId()).get();
			//image
			if(!file.isEmpty())
			{
				//delete old photo
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file2=new File(deleteFile, oldContact.getImage());
				file2.delete();
				//insert new photo
				File saveFile = new ClassPathResource("static/image").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}else {
				contact.setImage(oldContact.getImage());
			}
			String name = principal.getName();
			User user = repo.getUserByEmail(name);
			contact.setUser(user);
			contRepo.save(contact);
			session.setAttribute("message", new Message("Your contact is updated....", "success"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
	@GetMapping("/setting")
	public String openSetting()
	{
		return "normal/setting";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPass")String oldPass,
			@RequestParam("newPass")String newPass,Principal principal,HttpSession session)
	{
		
		System.out.println("old "+oldPass);
		System.out.println("new "+newPass);
		String name = principal.getName();
		User user = repo.getUserByEmail(name);
		if(passwordEncoder.matches(oldPass, user.getPassword()))
		{
			//change pass
			user.setPassword(passwordEncoder.encode(newPass));
			repo.save(user);
			session.setAttribute("message", new Message("Your password change successfully....", "success"));
		}else {
			//show error
			session.setAttribute("message", new Message("Your password wrong !! please try again....", "danger"));
			return "redirect:/user/setting";
		}
		
		return "redirect:/user/index";
	}
}
