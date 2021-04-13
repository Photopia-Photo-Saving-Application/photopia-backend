package com.example.demo.dao;

import java.util.List;

import com.example.demo.entity.User;

public interface UserDAO {

	List<User> findAll();
	
	User findById(int theId);
	
	void save(User theUser);
	
	void deleteById(int theId);

	User getUserByName(String theName);

	void insertToken(String theUsername, String theToken);

	User getUserByToken(String theToken);

	void removeToken(String theToken);

	void removeTokenForUser(String theUsername);

	Boolean changePasswordForUser(User theUser, String theNewPassword);

    void registerUser(User theUser, String siteURL);

	boolean verifyUser(String theVerificationCode);
}
