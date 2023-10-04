package com.school.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.school.entity.Contact;
import com.school.entity.User;
import com.school.repository.ContactRepostiory;
import com.school.repository.UserRepository;

@RestController
public class SearchController {

	@Autowired
	private UserRepository repo;
	@Autowired
	private ContactRepostiory contRepo;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{
		User user = repo.getUserByEmail(principal.getName());
		
		List<Contact> list = contRepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(list);
	}
}
