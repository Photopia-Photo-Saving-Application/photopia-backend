package com.taaha.photopia.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taaha.photopia.entity.Response;
import com.taaha.photopia.entity.User;
import com.taaha.photopia.error.ErrorResponse;
import com.taaha.photopia.error.UserNotFoundException;
import com.taaha.photopia.filters.JwtRequestFilter;
import com.taaha.photopia.models.AuthenticationRequest;
import com.taaha.photopia.models.AuthenticationResponse;
import com.taaha.photopia.service.UserServiceImpl;
import com.taaha.photopia.util.JwtUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @Autowired
    public AuthenticationRestController(UserServiceImpl theUserService) {
        userService = theUserService;
    }

    @PostMapping("/signIn")
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails theUser = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(theUser);
        userService.insertToken(theUser.getUsername(),jwt);
        Map<String,String> payload=new HashMap<>();
        payload.put("jwt",jwt);
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "Sign In Complete",payload),HttpStatus.OK);
    }

    @PostMapping( "/signIn/auto")
    public ResponseEntity<Object> getUserByToken() throws Exception{
        Map<String,String> payload=new HashMap<>();
        payload.put("jwt",jwtRequestFilter.getToken());
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "Token is valid",payload),HttpStatus.OK);
    }

    @PostMapping( "/signOut")
    public ResponseEntity<Object> removeToken() throws UserNotFoundException{
        try{
            userService.removeToken(jwtRequestFilter.getToken());
        }catch(UserNotFoundException e){
            throw new UserNotFoundException("Could not delete the token", e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "User logged out from this device",payload),HttpStatus.OK);
    }

    @PostMapping( "/signOutAllDevices")
    public ResponseEntity<Object> removeTokenForUser() throws UsernameNotFoundException{
        try{
            userService.removeTokenForUser(jwtRequestFilter.getUsername());
        }catch(UsernameNotFoundException e){
            throw new UserNotFoundException("Could not find the user", e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "User logged out of all devices",payload),HttpStatus.OK);
    }

    @PatchMapping("/passwordChange")
    public ResponseEntity<Object> changeUserPassword(@RequestBody LinkedHashMap theRequest) throws Exception{
        try{
            boolean result=userService.changePasswordForUser(jwtRequestFilter.getUsername(),(String) theRequest.get("oldpassword"),(String) theRequest.get("newpassword"));
            if(!result){
                throw new Exception("You have entered wrong password");
            }
        }catch(Exception e){
            throw new Exception("Could not find the user", e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "User changed password successfully",payload),HttpStatus.OK);
    }

    @PostMapping("/signUp")
    public ResponseEntity<Object> registerUser(@RequestBody User theUser, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
        userService.registerUser(theUser, getSiteURL(request));
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "User changed password successfully",payload),HttpStatus.OK);
    }

    @GetMapping("/signUp/verify")
    public ResponseEntity<Object> verifyUser(@RequestParam("code") String theVerificationCode) {
        Map<String,String> payload=new HashMap<>();
        if (userService.verifyUser(theVerificationCode)) {
            return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "User verification successful",payload),HttpStatus.OK);
        } else {
            return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "User verification error",payload),HttpStatus.OK);
        }
    }

    @PatchMapping("/forgotPassword")
    public ResponseEntity<Object> forgotUserPassword(@RequestBody LinkedHashMap theRequest, HttpServletRequest request) throws Exception{
        Map<String,String> payload=new HashMap<>();
        try{
            User theUser=userService.forgotPasswordForUser((String) theRequest.get("email"), getSiteURL(request));
            if(theUser==null){
                throw new Exception("No user is registered with this email");
            }
            if(!theUser.isEnabled()){
                throw new Exception("User is not verified yet to do this operation");
            }
        }catch(Exception e){
            throw new Exception("Could not find the user", e);
        }
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(),"Check your email to recover account",payload),HttpStatus.OK);
    }

    @PatchMapping("/recoverAccount")
    public ResponseEntity<Object> recoverAccount(@RequestBody LinkedHashMap theRequest,@RequestParam("code") String theVerificationCode, HttpServletRequest request) throws Exception{
        Map<String,String> payload=new HashMap<>();
        try{
            User theUser= userService.verifyAccountRecovery(theVerificationCode);
            if(theUser==null){
                throw new Exception("User verification code didnot match");
            }
            userService.changePasswordForAccountVerification(theUser, (String) theRequest.get("password"));
        }catch(Exception e){
            throw new Exception("Could not find the user", e);
        }
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(),"Password changed for account recovery",payload),HttpStatus.OK);
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
