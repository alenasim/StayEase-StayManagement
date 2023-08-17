package com.laioffer.staybooking.util;
import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/* this JwtUtil class encapsulates the logic required for JWT generation, extraction of information from JWTs,
and token validation. It abstracts away the JWT-related operations, making it easier to work with JWTs in various parts of the application.
The choice between using @Component or @Service often depends on the context and the role the class plays in the application.
In this case, since the class is providing utility-like functionality, @Component is a suitable choice.*/
@Component        // @Service 也可以。怎么决定用哪个？假如里面的操作简单，不跟其他database和前端交互，简简单单做internal 操作的话，component就可以
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;    // 别人把token发过来，用这个secret去check token有没有改过。secret存在哪里？在application.properties里面

    public String generateToken(String subject) {    // construct a token
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000))
                .signWith(SignatureAlgorithm.HS256, secret)       // HS256是secret长度。
                .compact();              // 压缩后加米再把token用string的方式发出去（给前端）
    }

    /*This method extracts the subject (usually representing the username) from a given JWT token.
    It calls the extractClaims() method to get the claims and retrieves the subject from the claims.*/
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {

        return extractClaims(token).getExpiration();
    }

    public Boolean validateToken(String token) {
        return extractExpiration(token).after(new Date());
    }

    /*This is a private helper method that extracts the claims (payload) from a given JWT token.
    It uses the Jwts.parser() API with the provided secret key to parse and verify the token.
    The claims are then returned as a Claims object.*/
    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
