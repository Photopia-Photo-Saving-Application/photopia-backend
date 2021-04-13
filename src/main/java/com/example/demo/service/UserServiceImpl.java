package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dao.UserDAOHibernateImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;

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

		User user = userDAO.getUserByName(theName); //userDAO == null Causing NPE

		if (user == null)
			throw new UsernameNotFoundException("No user with the username!");
		if(!user.isEnabled()) throw new UsernameNotFoundException("User is not verified");
		return new org.springframework.security.core.userdetails
				.User(user.getName(), user.getPassword(), new ArrayList<>());
	}

	@Override
	@Transactional
	public void insertToken(String theUsername, String theToken) {



		userDAO.insertToken(theUsername,theToken);
	}

	@Override
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
		System.out.println(theUser.getPassword());
		if(!passwordEncoder.matches(theOldPassword,theUser.getPassword())){
			return false;
		}
		return userDAO.changePasswordForUser(theUser,passwordEncoder.encode( theNewPassword));
	}

	@Override
	@Transactional
	public void registerUser(User theUser, String siteURL) throws UnsupportedEncodingException, MessagingException {

		userDAO.registerUser(theUser,siteURL);

		sendVerificationEmail(theUser, siteURL);
	}

	@Override
	@Transactional
	public boolean verifyUser(String theVerificationCode) {

		return userDAO.verifyUser(theVerificationCode);
	}

	private void sendVerificationEmail(User theUser, String siteURL)
			throws MessagingException, UnsupportedEncodingException {
		String toAddress = theUser.getEmail();
		String fromAddress="demoSpring@gmail.com";
		String senderName = "DemoSpringAuth";
		String subject = "Please verify your registration";
		String content = "Dear [[name]],<br>"
				+ "Please click the link below to verify your registration:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
				+ "Thank you,<br>"
				+ "DemoSpring";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", theUser.getName());
		String verifyURL = siteURL + "/auth/signUp/verify?code=" + theUser.getVerificationCode();

		content = content.replace("[[URL]]", verifyURL);

		helper.setText(content, true);

		mailSender.send(message);

	}
}






