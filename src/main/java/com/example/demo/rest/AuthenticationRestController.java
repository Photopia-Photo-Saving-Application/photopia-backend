package com.example.demo.rest;

import com.example.demo.entity.User;
import com.example.demo.filters.JwtRequestFilter;
import com.example.demo.models.AuthenticationRequest;
import com.example.demo.models.AuthenticationResponse;
import com.example.demo.service.UserServiceImpl;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

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
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
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

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping( "/signIn/auto")
    public String getUserByToken() throws Exception{

        return  "User found for the token";
    }

    @PostMapping( "/signOut")
    public String removeToken() throws Exception{

        try{
            String theToken= jwtRequestFilter.getToken();

            if(theToken == null){
                throw new Exception("No token exists");
            }

            userService.removeToken(theToken);
        }catch(Exception e){
            throw new Exception("Could not delete the token", e);
        }
        return  "User logged out of this device";
    }

    @PostMapping( "/signOutAllDevices")
    public String removeTokenForUser() throws Exception{

        try{
            String theUsername= jwtRequestFilter.getUsername();

            if(theUsername == null){
                throw new Exception("No username exists");
            }

            userService.removeTokenForUser(theUsername);
        }catch(Exception e){
            throw new Exception("Could not find the user", e);
        }
        return  "User logged out of all devices";
    }

    @PatchMapping("/passwordChange")
    public String changeUserPassword(@RequestBody LinkedHashMap theRequest) throws Exception{
        try{
            String theUsername= jwtRequestFilter.getUsername();

            if(theUsername == null){
                throw new Exception("No username exists");
            }
            boolean result=userService.changePasswordForUser(theUsername,(String) theRequest.get("oldpassword"),(String) theRequest.get("newpassword"));
            if(!result){
                return "You have entered wrong password";
            }
            return  "Password changed for the user";
        }catch(Exception e){
            throw new Exception("Could not find the user", e);
        }
    }

    @PostMapping("/signUp")
    public String registerUser(@RequestBody User theUser, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        //System.out.println("registerUser: "+theUser.toString());
        theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
        userService.registerUser(theUser, getSiteURL(request));
        return "register_success";
    }

    @GetMapping("/signUp/verify")
    public String verifyUser(@RequestParam("code") String theVerificationCode) {
        if (userService.verifyUser(theVerificationCode)) {
            return "User verification successful";
        } else {
            return "User verification failed";
        }
    }

    @PatchMapping("/forgotPassword")
    public String forgotUserPassword(@RequestBody LinkedHashMap theRequest, HttpServletRequest request) throws Exception{
        try{
            boolean result=userService.newPasswordForUser((String) theRequest.get("email"), getSiteURL(request));
            if(!result){
                return "No user is registered with this email";
            }
            return  "Check your email to change password";
        }catch(Exception e){
            throw new Exception("Could not find the user", e);
        }
    }
    @PatchMapping("/recoverAccount")
    public String recoverAccount(@RequestBody LinkedHashMap theRequest,@RequestParam("code") String theVerificationCode, HttpServletRequest request) throws Exception{
        try{
            User theUser= userService.verifyAccountRecovery(theVerificationCode);
            if(theUser==null){
                return "User verification code didnot match";
            }
            userService.changePasswordForAccountVerification(theUser, (String) theRequest.get("password"));
            return "Password changed for account recovery";
        }catch(Exception e){
            throw new Exception("Could not find the user", e);
        }
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
