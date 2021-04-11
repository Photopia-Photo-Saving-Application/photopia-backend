package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;

public interface UserService {

	List<User> findAll();
	
	User findById(int theId);
	
	void save(User theEmployee);
	
	void deleteById(int theId);

	User getUserByName(String theName);
	
}
