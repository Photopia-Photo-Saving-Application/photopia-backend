package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
public class UserDAOHibernateImpl implements UserDAO {

	// define field for entitymanager
	private EntityManager entityManager ;
	// set up constructor injection
	@Autowired
	public UserDAOHibernateImpl(EntityManager theEntityManager) {
		entityManager = theEntityManager;
	}

	@Override
	public List<User> findAll() {

		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		// create a query
		Query<User> theQuery =
				currentSession.createQuery("from User");
		
		// execute query and get result list
		List<User> users = theQuery.getResultList();
		
		// return the results		
		return users;
	}


	@Override
	public User findById(int theId) {

		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		// get the employee
		User theUser =
				currentSession.get(User.class, theId);
		
		// return the employee
		return theUser;
	}


	@Override
	public void save(User theUser) {

		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
		
		// save employee
		currentSession.saveOrUpdate(theUser);
	}


	@Override
	public void deleteById(int theId) {
		
		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);
				
		// delete object with primary key
		Query theQuery = 
				currentSession.createQuery(
						"delete from User where id=:userId");
		theQuery.setParameter("userId", theId);
		
		theQuery.executeUpdate();
	}

	@Override
	public User getUserByName(String theName) {

		Session currentSession = entityManager.unwrap(Session.class);

		Query theQuery =
				currentSession.createQuery(
						"from User where name=:userName");
		theQuery.setParameter("userName", theName);
		User theUser= (User) theQuery.uniqueResult();

		return theUser;
	}

	@Override
	public void insertToken(String theUsername, String theToken) {

		Session currentSession = entityManager.unwrap(Session.class);
		Query theQuery =
				currentSession.createNativeQuery(
						"insert into usertoken (username,token) values (:Username,:Token)");
		theQuery.setParameter("Username", theUsername);
		theQuery.setParameter("Token",theToken);

		theQuery.executeUpdate();
	}

	@Override
	public User getUserByToken(String theToken) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query theQuery =
				currentSession.createNativeQuery(
						"select distinct(username) from usertoken where token=:Token");
		theQuery.setParameter("Token", theToken);
		String theName= (String) theQuery.uniqueResult();
		User theUser= getUserByName(theName);
		return theUser;
	}

	@Override
	public void removeToken(String theToken) {

		Session currentSession = entityManager.unwrap(Session.class);

		// delete object with primary key
		Query theQuery =
				currentSession.createNativeQuery(
						"delete from usertoken where token=:Token");
		theQuery.setParameter("Token", theToken);

		theQuery.executeUpdate();

	}

	@Override
	public void removeTokenForUser(String theUsername) {

		Session currentSession = entityManager.unwrap(Session.class);

		Query theQuery =
				currentSession.createNativeQuery(
						"delete from usertoken where username=:Username");
		theQuery.setParameter("Username",theUsername);

		theQuery.executeUpdate();

	}

	@Override
	public void changePasswordForUser(String theUsername, String theOldPassword, String theNewPassword){
		System.out.println("inside changepasowrdforuser dao");
		Session currentSession = entityManager.unwrap(Session.class);
		String oldPassword = (String) currentSession.createQuery("select password from User  where name = :Username").setString("Username",theUsername).uniqueResult();
		System.out.println("oldpassword: "+oldPassword);
		if(oldPassword!=theOldPassword){
//			 new Exception("Old password didnot match");
		}
		Query theQuery=currentSession.createNativeQuery("update user set password where name = :Username");
		theQuery.setParameter("Username",theUsername).executeUpdate();
	}
}







