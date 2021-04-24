package com.taaha.photopia.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.taaha.photopia.dao.UserDAOHibernateImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taaha.photopia.entity.User;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService,UserDetailsService{

	@Autowired
	private JavaMailSender mailSender;

	private UserDAOHibernateImpl userDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserDAOHibernateImpl theUserDAO) {
		userDAO = theUserDAO;
	}

	@Override
	@Transactional
	public List<User> findAll() {
		return userDAO.findAll();
	}

	@Override
	@Transactional
	public User findById(int theId) {
		return userDAO.findById(theId);
	}

	@Override
	@Transactional
	public void save(User theEmployee) {
		userDAO.save(theEmployee);
	}

	@Override
	@Transactional
	public void deleteById(int theId) {
		userDAO.deleteById(theId);
	}


	@Override
	@Transactional
	public UserDetails loadUserByUsername(String theName) throws UsernameNotFoundException {

		User theUser = userDAO.getUserByName(theName); //userDAO == null Causing NPE

		if (theUser == null)
			throw new UsernameNotFoundException("No user with the username!");
		if(!theUser.isEnabled()) throw new UsernameNotFoundException("User is not verified");
		return new org.springframework.security.core.userdetails
				.User(theUser.getName(), theUser.getPassword(), new ArrayList<>());
	}

	@Override
	@Transactional
	public void insertToken(String theUsername, String theToken) {



		userDAO.insertToken(theUsername,theToken);
	}

	@Override
	@Transactional
	public User getUserByToken(String theToken) {
		return userDAO.getUserByToken(theToken);
	}

	@Override
	@Transactional
	public void removeToken(String theToken) {

		userDAO.removeToken(theToken);

	}

	@Override
	@Transactional
	public void removeTokenForUser(String theUsername) {
		userDAO.removeTokenForUser(theUsername);
	}

	@Override
	@Transactional
	public Boolean changePasswordForUser(String theUsername, String theOldPassword, String theNewPassword) {

		User theUser=userDAO.getUserByName(theUsername);
		//System.out.println(theUser.getPassword());
		if(!passwordEncoder.matches(theOldPassword,theUser.getPassword())){
			return false;
		}
		return userDAO.changePasswordForUser(theUser,passwordEncoder.encode( theNewPassword));

	}

	@Override
	@Transactional
	public void registerUser(User theUser, String siteURL) throws Exception {

		User theUserByName= userDAO.getUserByName(theUser.getName());
		User theUserByEmail= userDAO.getUserByEmail(theUser.getEmail());
		if(theUserByName==null && theUserByEmail==null){
			theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
			userDAO.registerUser(theUser);
			sendVerificationEmail(theUser,siteURL+ "/signUp/verify?code=" + theUser.getVerificationCode(), "Please verify your registration");
		}
		else{
			throw new Exception("User with same name or email already exists");
		}

	}

	@Override
	@Transactional
	public boolean verifyUser(String theVerificationCode) {

		return userDAO.verifyUser(theVerificationCode);
	}

	@Override
	@Transactional
	public User forgotPasswordForUser(String theEmail, String siteURL) throws UnsupportedEncodingException, MessagingException {

		User theUser = userDAO.getUserByEmail(theEmail);
		if(theUser==null){
			return null;
		}
		userDAO.registerUser(theUser);
		sendVerificationEmail(theUser, siteURL+"/auth/recoverAccount?code="+theUser.getVerificationCode(), "Please recover your account");
		return theUser;

	}

	@Override
	@Transactional
	public User verifyAccountRecovery(String theVerificationCode) {

		return userDAO.verifyAccountRecovery(theVerificationCode);

	}

	@Override
	@Transactional
	public Boolean changePasswordForAccountVerification(User theUser, String theNewPassword) {

		return userDAO.changePasswordForUser(theUser,passwordEncoder.encode(theNewPassword));

	}

	@Override
	@Transactional
	public void removeUser(String theName) {
		userDAO.deleteByName(theName);
	}

	private void sendVerificationEmail(User theUser, String verifyURL, String subject) throws MessagingException, UnsupportedEncodingException {

		String toAddress = theUser.getEmail();
		String fromAddress="photopia@gmail.com";
		String senderName = "Photopia Admin";
		String content = "Dear [[name]],<br>"
				+ "Click the link below: <br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
				+ "Thank you,<br>"
				+ "Photopia";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", theUser.getName());

		content = content.replace("[[URL]]", verifyURL);

		helper.setText(content, true);

		mailSender.send(message);

	}
}






