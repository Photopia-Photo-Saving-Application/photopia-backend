package com.taaha.photopia.util;

import com.taaha.photopia.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("photopia")
    private String SECRET_KEY;

    public String extractUsername(String token) {
//        try{
//            System.out.println("hello from username");
           return extractClaim(token, Claims::getSubject);
//        } catch(UsernameNotFoundException e){
//            throw new UsernameNotFoundException("No username for this token",e);
//        }
    }

    public Date extractExpiration(String token) {
//        try{
//            System.out.println("hello from extractExpiration");
            return extractClaim(token, Claims::getExpiration);
//        } catch(UsernameNotFoundException e) {
//            throw new UsernameNotFoundException("No username for this token", e);
//        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        try{
//            System.out.println("hello from extractclaims");
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
//        } catch(UsernameNotFoundException e) {
//            throw new UsernameNotFoundException("No username for this token", e);
//        }
    }
    private Claims extractAllClaims(String token) {
//        try{
//            System.out.println("hello from extractAllclaims");
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//        } catch(UsernameNotFoundException e) {
//            System.out.println("hello from extractAllclaims exception");
//            throw new UsernameNotFoundException("No username for this token", e);
//        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails theUser) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, theUser.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}