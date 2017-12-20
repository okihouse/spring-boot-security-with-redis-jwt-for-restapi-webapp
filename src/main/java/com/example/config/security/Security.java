package com.example.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.example.config.security.filter.SecurityUserLoginProcessingFilter;
import com.example.config.security.handler.SecurityUserAccessHandler;
import com.example.config.security.handler.SecurityUserLoginHandler;
import com.example.config.security.handler.SecurityUserLogoutHandler;
import com.example.config.security.implement.UserDetailServiceImpl;
import com.example.config.security.provider.SecurityUserAuthenticationProvider;
import com.example.config.security.repository.PersistTokenRepository;
import com.example.config.security.url.SecurityUrlInformation;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
@EnableAutoConfiguration
public class Security extends WebSecurityConfigurerAdapter {

	@Autowired private AuthenticationManager authenticationManager;

	@Autowired private SecurityUserLoginHandler loginHandler;

	@Autowired private SecurityUserLogoutHandler logoutHandler;

	@Autowired private SecurityUserAccessHandler accessHandler;

	@Autowired private PersistTokenRepository persistenceTokenRepository;

	@Autowired private UserDetailServiceImpl userDetailService;

	@Autowired private SecurityUserAuthenticationProvider securityUserAuthenticationProvider;

	@Autowired private SecurityUrlInformation urlInformation;

	private final String rememberKey = "remember-me";

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable();
		
		httpSecurity.sessionManagement();

		httpSecurity.authorizeRequests()
						.antMatchers(urlInformation.getLogin(), urlInformation.getLogout()).permitAll()
						.anyRequest().authenticated()
						.and()
					.logout()
						.logoutUrl(urlInformation.getLogout())
						.deleteCookies("JSESSIONID", rememberKey)
						.logoutSuccessHandler(logoutHandler)
						.and()
					.rememberMe()
						.key(rememberKey)
						.rememberMeParameter(rememberKey)
						.rememberMeServices(persistentTokenBasedRememberMeServices())
						.tokenValiditySeconds(3600)
						.and()
					.exceptionHandling()
						.authenticationEntryPoint(accessHandler)
						.accessDeniedHandler(loginHandler)
						.and()
					.addFilterBefore(buildSecurityUserLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
	    PersistentTokenBasedRememberMeServices persistenceTokenBasedservice =
	    		new PersistentTokenBasedRememberMeServices(rememberKey, userDetailService, persistenceTokenRepository);
	    return persistenceTokenBasedservice;
	}

	protected SecurityUserLoginProcessingFilter buildSecurityUserLoginProcessingFilter() throws Exception {
        SecurityUserLoginProcessingFilter filter = new SecurityUserLoginProcessingFilter(
        				"/loginProcess"
        				,loginHandler
        				,persistentTokenBasedRememberMeServices());
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(securityUserAuthenticationProvider);
    }

}
