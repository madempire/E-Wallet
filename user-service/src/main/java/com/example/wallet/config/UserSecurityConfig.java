package com.example.wallet.config;


import com.example.wallet.constants.UserConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic().and().authorizeHttpRequests()
                .antMatchers(HttpMethod.POST,"/user/**").permitAll()    // adding or signUp can be done by any one
                .antMatchers("/user/**").hasAuthority(UserConstants.USER_AUTHORITY)
                .antMatchers("/admin/**").hasAnyAuthority(UserConstants.ADMIN_AUTHORITY,UserConstants.SERVICE_AUTHORITY)
                .and().formLogin();

    }


}
