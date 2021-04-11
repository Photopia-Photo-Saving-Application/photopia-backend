package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.dao.UserDAOHibernateImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.UserDAO;
import com.example.demo.entity.User;

@Service
public class UserServiceImpl implements UserService,UserDetailsService{

	private UserDAOHibernateImpl userDAO;


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
//		System.out.println("inside loadUser");
		User user = userDAO.getUserByName(theName); //userDAO == null Causing NPE
//		System.out.println("inside loadUser user not null");
		if (user == null)
			throw new UsernameNotFoundException("Oops!");

		return new org.springframework.security.core.userdetails
				.User(user.getName(), user.getPassword(), new ArrayList<>());
	}

	@Override
	@Transactional
	public void insertToken(String theUsername, String theToken) {

//		System.out.println("before insertToken service");

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
	public void removeTokenForUser(String theToken) {

		userDAO.removeTokenForUser(theToken);

	}
}






