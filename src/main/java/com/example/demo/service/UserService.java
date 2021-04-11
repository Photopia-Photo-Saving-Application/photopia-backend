package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;

public interface UserService {

	List<User> findAll();
	
	User findById(int theId);
	
	void save(User theEmployee);
	
	void deleteById(int theId);

	void insertToken(String theUsername, String theToken);

	User getUserByToken(String theToken);

	void removeToken(String theToken);

	void removeTokenForUser(String theToken);

	void changePasswordForUser(String theUsername);
}
