package com.school.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.school.entity.User;
import com.school.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository repo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.getUserByEmail(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("User not found");
		}
		CustomUserDetails userDetails = new CustomUserDetails(user);
		return userDetails;
	}

}
