package com.laioffer.staybooking.service;

import com.laioffer.staybooking.exception.UserNotExistException;
import com.laioffer.staybooking.model.Token;
import com.laioffer.staybooking.model.User;
import com.laioffer.staybooking.model.UserRole;
import com.laioffer.staybooking.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/*this AuthenticationService encapsulates the logic for user authentication based on the provided credentials and role.
It uses the AuthenticationManager to perform the authentication and the JwtUtil to generate a JWT token upon successful
authentication. If the authentication fails or the role requirement is not met, a custom exception is thrown.
*/

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    public AuthenticationService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    /*This method, named authenticate, takes a User object and a UserRole enum as parameters and returns a Token object.
    It's responsible for authenticating the user based on the provided credentials and role.*/
    public Token authenticate(User user, UserRole role) throws UserNotExistException {
        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (AuthenticationException exception) {
            throw new UserNotExistException("User Doesn't Exist");
        }

        if (auth == null || !auth.isAuthenticated() || !auth.getAuthorities().contains(new SimpleGrantedAuthority(role.name()))) {
            throw new UserNotExistException("User Doesn't Exist");
        }
        /*If authentication is successful and the user has the required role, this line generates a JWT token using the jwtUtil and
        the authenticated user's username, and returns a Token object containing the generated token*/
        return new Token(jwtUtil.generateToken(user.getUsername()));
    }
}