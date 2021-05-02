package com.taaha.photopia.rest;

import com.taaha.photopia.model.Response;
import com.taaha.photopia.entity.User;
import com.taaha.photopia.error.UserNotFoundException;
import com.taaha.photopia.filter.JwtRequestFilter;
import com.taaha.photopia.model.ForgotPasswordRequest;
import com.taaha.photopia.model.PasswordChangeRequest;
import com.taaha.photopia.model.RecoverAccountRequest;
import com.taaha.photopia.model.SignInRequest;
import com.taaha.photopia.service.StorageService;
import com.taaha.photopia.service.UserServiceImpl;
import com.taaha.photopia.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthenticationRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Value("${frontend.url}")
    private String siteURL;

    @Autowired
    private StorageService storageService;

    private UserServiceImpl userService;

    @Autowired
    public AuthenticationRestController(UserServiceImpl theUserService) {
        userService = theUserService;
    }

//    @CrossOrigin(origins = "http://localhost:8081/signIn")
    @PostMapping("/signIn")
    public ResponseEntity<Object> createAuthenticationToken(@Valid @RequestBody SignInRequest theRequest) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(theRequest.getUsername(), theRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            throw new Exception("incorrect username or password / user not yet verified", e);
        }
        final UserDetails theUser = userService.loadUserByUsername(theRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(theUser);
        userService.insertToken(theUser.getUsername(),jwt);
        Map<String,String> payload=new HashMap<>();
        payload.put("jwt",jwt);
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/signIn: user sign in successful",payload),HttpStatus.OK);

    }

    @GetMapping( "/signIn/auto")
    public ResponseEntity<Object> getUserByToken() {

        Map<String,String> payload=new HashMap<>();
        payload.put("jwt",jwtRequestFilter.getToken());
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/signIn/auto: token is valid",payload),HttpStatus.OK);

    }

    @PostMapping( "/signOut")
    public ResponseEntity<Object> removeToken() throws UserNotFoundException{

        try{
            userService.removeToken(jwtRequestFilter.getToken());
        }catch(UserNotFoundException e){
            throw new UserNotFoundException("could not find user for the token", e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/signOut: user sign out successful",payload),HttpStatus.OK);

    }

    @PostMapping( "/signOutAllDevices")
    public ResponseEntity<Object> removeTokenForUser() throws UsernameNotFoundException{

        try{
            userService.removeTokenForUser(jwtRequestFilter.getUsername());
        }catch(UsernameNotFoundException e){
            throw new UserNotFoundException("could not find user for the token", e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/signOutAllDevices: user sign out all devices successful",payload),HttpStatus.OK);

    }

    @PatchMapping("/passwordChange")
    public ResponseEntity<Object> changeUserPassword(@Valid @RequestBody PasswordChangeRequest theRequest) throws Exception{

        try{
            boolean result=userService.changePasswordForUser(jwtRequestFilter.getUsername(),theRequest.getOldpassword(),theRequest.getNewpassword());
            if(!result){
                throw new Exception("old password did not match");
            }
        }catch(Exception e){
            throw new Exception("could not find user for the token and oldpassword", e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/passwordChange: user  password change successful",payload),HttpStatus.OK);

    }

    @PostMapping("/signUp")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody User theUser, HttpServletRequest request) throws Exception {
        try{
//            userService.registerUser(theUser, getSiteURL(request));
            userService.registerUser(theUser, siteURL);
            System.out.println("register user done");
            storageService.createFolder(theUser.getName());
            System.out.println("create folder done");
        }catch(Exception e){
            throw new Exception("user with same name or email exists",e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/signUp: user sign up successful - check email for verification",payload),HttpStatus.OK);

    }

    @GetMapping("/signUp/verify")
    public ResponseEntity<Object> verifyUser(@RequestParam(value = "code") String theVerificationCode) throws Exception {
        Map<String,String> payload=new HashMap<>();
        if (userService.verifyUser(theVerificationCode)) {
            return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/signUp/verify: user verification successful",payload),HttpStatus.OK);
        } else {
            throw new Exception("user sign up verification error");
        }

    }

    @PatchMapping("/forgotPassword")
    public ResponseEntity<Object> forgotUserPassword(@Valid @RequestBody ForgotPasswordRequest theRequest, HttpServletRequest request) throws Exception{
        Map<String,String> payload=new HashMap<>();
        try{
            User theUser=userService.forgotPasswordForUser(theRequest.getEmail(), siteURL);
            if(theUser==null){
                throw new Exception("no user registered with this email");
            }
            if(!theUser.isEnabled()){
                throw new Exception("user not verified to do this operation");
            }
        }catch(Exception e){
            throw new Exception("could not find the user for the email", e);
        }
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(),"auth/forgotPassword: check email to recover account",payload),HttpStatus.OK);
    }

    @PatchMapping("/recoverAccount")
    public ResponseEntity<Object> recoverAccount(@Valid @RequestBody RecoverAccountRequest theRequest, @RequestParam("code") String theVerificationCode, HttpServletRequest request) throws Exception{
        Map<String,String> payload=new HashMap<>();
        try{
            User theUser= userService.verifyAccountRecovery(theVerificationCode);
            if(theUser==null){
                throw new Exception("user verification code did not match");
            }
            userService.changePasswordForAccountVerification(theUser, theRequest.getPassword());
        }catch(Exception e){
            throw new Exception("could not find the user for the token", e);
        }
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(),"auth/recoverAccount: user password changed for account recovery successful",payload),HttpStatus.OK);
    }

    @DeleteMapping( "/deleteAccount")
    public ResponseEntity<Object> removeUser() throws UsernameNotFoundException{

        try{
            storageService.deleteFolder(jwtRequestFilter.getUsername());
            userService.removeUser(jwtRequestFilter.getUsername());
        }catch(UsernameNotFoundException e){
            throw new UsernameNotFoundException("could not find user for the token", e);
        }
        Map<String,String> payload=new HashMap<>();
        return  new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "auth/deleteAccount: account deletion successful",payload),HttpStatus.OK);

    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
