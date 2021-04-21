package com.taaha.photopia.rest;

import java.util.List;

import com.taaha.photopia.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.taaha.photopia.entity.User;


@RestController
@RequestMapping("/api")
public class UserRestController {

	private UserServiceImpl userService;

	@Autowired
	public UserRestController(UserServiceImpl theUserService) {
		userService = theUserService;
	}

	@GetMapping("/users")
	public List<User> findAll() {
		return userService.findAll();
	}


	@GetMapping("/users/{userId}")
	public User getUser(@PathVariable int userId) {
		
		User theUser = userService.findById(userId);
		if (theUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}
		return theUser;

	}


	@PostMapping("/users")
	public User addUser(@RequestBody User theUser) {

		theUser.setId(0);
		userService.save(theUser);
		return theUser;

	}


	@PutMapping("/users")
	public User updateEmployee(@RequestBody User theUser) {

		userService.save(theUser);
		return theUser;

	}


	@DeleteMapping("/users/{userId}")
	public String deleteUser(@PathVariable int userId) {
		
		User tempUser = userService.findById(userId);
		if (tempUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}
		userService.deleteById(userId);
		return "Deleted user id - " + userId;

	}

	
}










