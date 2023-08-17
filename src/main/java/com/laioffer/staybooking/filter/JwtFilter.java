package com.laioffer.staybooking.filter;

import com.laioffer.staybooking.model.Authority;
import com.laioffer.staybooking.repository.AuthorityRepository;
import com.laioffer.staybooking.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
/*This code defines a custom Spring Security filter named JwtFilter that is responsible for handling JSON Web Token (JWT)
 authentication for incoming HTTP requests. The filter extracts JWT tokens from the "Authorization" header, validates them,
 and sets up authentication if the token is valid.

 this JwtFilter class intercepts incoming requests, extracts and validates JWT tokens, and if the token is valid,
 it sets up authentication for the user. This filter is part of the Spring Security framework and ensures that users are
 properly authenticated using JWT tokens before they can access protected resources in the application.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {     // OncePerRequestFilter is an abstract class (是用来override的). It has the core logic, but we need to override to implement method
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";  // token is "Bearer " 后面的内容


    private final AuthorityRepository authorityRepository;
    private final JwtUtil jwtUtil;


    public JwtFilter(AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        this.authorityRepository = authorityRepository;
        this.jwtUtil = jwtUtil;
    }

    /*the doFilterInternal method is responsible for intercepting incoming HTTP requests, extracting and validating JWT tokens,
    setting up user authentication if the token is valid, and then allowing the request to continue its processing through the
    filter chain. This process ensures that only authenticated users with valid tokens can access protected resources in the application.*/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // token extraction
        final String authorizationHeader = request.getHeader(HEADER);   // Retrieve authentication header from incoming HTTP request. this input header should contain the JWT token.

        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) {
            jwt = authorizationHeader.substring(PREFIX.length());   // extracts the actual JWT token from the header by removing the prefix
        }

        // token validation - This method checks the token's validity, including its expiration and integrity.
        if (jwt != null && jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtil.extractUsername(jwt);   // 为什么jwtUtil可以找出username？ token is encoded version, after decoding it, we can extract the username.
            Authority authority = authorityRepository.findById(username).orElse(null);
            if (authority != null) {          //假如authority == null，那你的token不是valid
                List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(authority.getAuthority())});
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);   // Spring官方doc抄过来的。主要目的是为了准备信息set authentication later
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 最后authenticate之前的filter，假如有些IP address想filter的话可以写在这里。
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);  // principal is created here. Authentication extends Principal. when setting Authentication, principle is created
            }
        }
        // This method is called to continue processing the request. This step ensures that the request goes through other filters and eventually reaches the appropriate controller or resource handler.
        filterChain.doFilter(request, response);
    }
}
