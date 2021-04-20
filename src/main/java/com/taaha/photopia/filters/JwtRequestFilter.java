package com.taaha.photopia.filters;

import com.taaha.photopia.entity.Response;
import com.taaha.photopia.entity.User;
import com.taaha.photopia.error.ErrorResponse;
import com.taaha.photopia.error.UserNotFoundException;
import com.taaha.photopia.service.UserService;
import com.taaha.photopia.service.UserServiceImpl;
import com.taaha.photopia.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    private String username=null;
    private String jwt=null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException,IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        username = null;
        jwt = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                try{
                    username = jwtUtil.extractUsername(jwt);
                }catch(Exception e){

                }

            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails user = this.userService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(jwt, user)) {

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
            }
           // System.out.println("before chain dofilter");
            chain.doFilter(request, response);
    }
    public String getToken(){
        return jwt;
    }
    public String getUsername() { return username;}
}
