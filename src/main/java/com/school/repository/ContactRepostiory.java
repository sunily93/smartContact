package com.school.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.school.entity.Contact;
import com.school.entity.User;

public interface ContactRepostiory extends JpaRepository<Contact, Integer>{

	@Query("from Contact as c where c.user.id=:userId")
	Page<Contact> findContactByUser(@Param("userId")int userId,Pageable pageable);
	
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
