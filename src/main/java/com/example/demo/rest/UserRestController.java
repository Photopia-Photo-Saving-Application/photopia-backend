package com.example.demo.rest;

import java.util.List;

import com.example.demo.models.AuthenticationRequest;
import com.example.demo.models.AuthenticationResponse;
import com.example.demo.service.UserServiceImpl;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api")
public class UserRestController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;


	private UserServiceImpl userService;

	@Autowired
	public UserRestController(UserServiceImpl theUserService) {
		userService = theUserService;
	}

	//mapping for GET/users and return list of  subusers
	//need modification
	//work on this later
	@GetMapping("/users")
	public List<User> findAll() {
		return userService.findAll();
	}

	//mapping for GET/users/{userId} and return subuser with that id
	//need modification
	//work on this later
	@GetMapping("/users/{userId}")
	public User getUser(@PathVariable int userId) {
		
		User theUser = userService.findById(userId);
		
		if (theUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}
		
		return theUser;
	}
	
	//mapping for POST/users - to create new sub user
	//need modification
	//work on this route later
	@PostMapping("/users")
	public User addUser(@RequestBody User theUser) {
		
		// also just in case they pass an id in JSON ... set id to 0
		// this is to force a save of new item ... instead of update
		
		theUser.setId(0);
		
		userService.save(theUser);
		
		return theUser;
	}
	
	//mapping for PUT/users - update user info
	//need modification
	//work on this later
	@PutMapping("/users")
	public User updateEmployee(@RequestBody User theUser) {

		userService.save(theUser);
		
		return theUser;
	}
	
	//mapping for DELETE/users/{userId} - delete  a sub user
	//need modification
	//work on this later
	@DeleteMapping("/users/{userId}")
	public String deleteUser(@PathVariable int userId) {
		
		User tempUser = userService.findById(userId);
		
		// throw exception if null
		
		if (tempUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}

		userService.deleteById(userId);
		
		return "Deleted user id - " + userId;
	}

	@RequestMapping(value = "/signIn", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		System.out.println("inside api/signIn");
		try {
			System.out.println("username: "+authenticationRequest.getUsername());
			System.out.println("userpassword: "+authenticationRequest.getPassword());
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
			);
			System.out.println("inside try");
		}
		catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}


		final UserDetails theUser = userService.loadUserByUsername(authenticationRequest.getUsername());

		final String jwt = jwtTokenUtil.generateToken(theUser);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
}










