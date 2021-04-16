package com.taaha.photopia.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.taaha.photopia.entity.User;

import javax.mail.MessagingException;

public interface UserService {

	List<User> findAll();
	
	User findById(int theId);
	
	void save(User theEmployee);
	
	void deleteById(int theId);

	void insertToken(String theUsername, String theToken);

	User getUserByToken(String theToken);

	void removeToken(String theToken);

	void removeTokenForUser(String theToken);

	Boolean changePasswordForUser(String theUsername, String theOldPassword, String theNewPassword);

    void registerUser(User theUser, String siteURL) throws UnsupportedEncodingException, MessagingException;

	boolean verifyUser(String theVerificationCode);

    User forgotPasswordForUser(String theEmail, String siteURL) throws UnsupportedEncodingException, MessagingException;

	User verifyAccountRecovery(String theVerificationCode);

	Boolean changePasswordForAccountVerification(User theUser, String theNewPassword);
}
