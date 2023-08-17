package com.laioffer.staybooking.config;


import com.laioffer.staybooking.filter.JwtFilter;
import io.jsonwebtoken.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.sql.DataSource;


@EnableWebSecurity    // @Configuration + call Spring Security
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    private final JwtFilter jwtFilter;

    public SecurityConfig(DataSource dataSource, JwtFilter jwtFilter) {
        this.dataSource = dataSource;
        this.jwtFilter = jwtFilter;
    }

    @Bean      // 看看有没有原代码。没有原代码的话加bean，自己new完了再加进去。 This bean is responsible for encoding and verifying passwords securely.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*This method is overridden to configure the security settings for various endpoints and requests. It uses the HttpSecurity object
    to specify access rules, authentication requirements, and other security-related configurations.*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/register/*").permitAll()
                .antMatchers(HttpMethod.POST, "/authenticate/*").permitAll()
                .antMatchers("/stays").hasAuthority("ROLE_HOST")           //为了拜访stay，一定要Role as Host
                .antMatchers("/stays/*").hasAuthority("ROLE_HOST")
                .antMatchers("/search").hasAuthority("ROLE_GUEST")
                .antMatchers("/reservations").hasAuthority("ROLE_GUEST")
                .antMatchers("/reservations/*").hasAuthority("ROLE_GUEST")
                .anyRequest().authenticated()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /* Below code in configure function sets up JDBC-based authentication using Spring Security. It configures how to fetch user information
    and authorities from the database, and it also specifies the password encoder to use for comparing passwords securely.
    This is a common setup for authenticating users against a database in a Spring Boot application.*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {    //
        auth.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT username, authority FROM authority WHERE username = ?");
    }

    /*AuthenticationManager is a core component responsible for authenticating users. It's used when you need to perform tasks
    such as user login, handling authentication requests, and verifying user credentials. By making the AuthenticationManager
    available as a bean, you can easily inject and use it in various parts of your application.*/
    @Override
    @Bean       // SpringBoot里面已经有，但是没有用任何Bean或者dependency expose出来，我们要在这application用，所以把它做成一个@Bean，把他expose出来。
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
