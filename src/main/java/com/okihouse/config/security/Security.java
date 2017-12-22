package com.okihouse.config.security;

import com.okihouse.config.security.api.filter.ApiTokenAuthenticationProcessingFilter;
import com.okihouse.config.security.api.token.ApiTokenFactory;
import com.okihouse.config.security.common.data.SecurityUrlData;
import com.okihouse.config.security.common.entrypoint.SecurityUserAccessEntryPoint;
import com.okihouse.config.security.common.filter.SecurityUserLoginProcessingFilter;
import com.okihouse.config.security.common.handler.SecurityUserAcessDeniedHandler;
import com.okihouse.config.security.common.handler.SecurityUserLoginHandler;
import com.okihouse.config.security.common.provider.SecurityAuthenticationProvider;
import com.okihouse.config.security.common.repository.PersistTokenRepository;
import com.okihouse.config.security.web.handler.WebSecurityUserLogoutHandler;
import com.okihouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {

	@Autowired private AuthenticationManager authenticationManager;

    @Autowired private SecurityUserLoginHandler securityUserLoginHandler;

    @Autowired private SecurityUserAcessDeniedHandler securityUserAcessDeniedHandler;

	@Autowired private SecurityUserAccessEntryPoint securityUserAccessEntryPoint;

    @Autowired private WebSecurityUserLogoutHandler webSecurityUserLogoutHandler;

	@Autowired private PersistTokenRepository persistenceTokenRepository;

	@Autowired private UserDetailsService userDetailsService;

    @Autowired private SecurityAuthenticationProvider securityAuthenticationProvider;

    @Autowired private ApiTokenFactory apiTokenFactory;

    @Autowired private UserRepository userRepository;

	@Autowired private SecurityUrlData securityUrlData;

	private final String rememberKey = "remember-me";

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable();
		
		httpSecurity.sessionManagement();
        httpSecurity.authorizeRequests()
						.antMatchers(
                            securityUrlData.getLogin()
                            , securityUrlData.getLogout()
                            , securityUrlData.getRefreshToken()
                        ).permitAll()
						.anyRequest().authenticated()
						.and()
					.logout()
						.logoutUrl(securityUrlData.getLogout())
						.deleteCookies("JSESSIONID", rememberKey)
						.logoutSuccessHandler(webSecurityUserLogoutHandler)
						.and()
					.rememberMe()
						.key(rememberKey)
						.rememberMeParameter(rememberKey)
						.rememberMeServices(persistentTokenBasedRememberMeServices())
						.tokenValiditySeconds(3600)
						.and()
					.exceptionHandling()
						.authenticationEntryPoint(securityUserAccessEntryPoint)
						.accessDeniedHandler(securityUserAcessDeniedHandler)
						.and()
					.addFilterBefore(buildWebSecurityUserLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(buildApiSecurityUserLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(buildApiTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                    ;
	}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(securityAuthenticationProvider);
    }

	@Bean
	public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
	    PersistentTokenBasedRememberMeServices persistenceTokenBasedservice =
	    		new PersistentTokenBasedRememberMeServices(
                        rememberKey
                        ,userDetailsService
                        ,persistenceTokenRepository);
	    return persistenceTokenBasedservice;
	}

	protected SecurityUserLoginProcessingFilter buildWebSecurityUserLoginProcessingFilter() throws Exception {
        SecurityUserLoginProcessingFilter filter = new SecurityUserLoginProcessingFilter(
                "/loginProcess"
                ,securityUserLoginHandler
                ,persistentTokenBasedRememberMeServices());
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    protected SecurityUserLoginProcessingFilter buildApiSecurityUserLoginProcessingFilter() throws Exception {
        SecurityUserLoginProcessingFilter filter = new SecurityUserLoginProcessingFilter(
                "/api/login"
                ,securityUserLoginHandler);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    protected ApiTokenAuthenticationProcessingFilter buildApiTokenAuthenticationProcessingFilter() throws Exception {
        ApiTokenAuthenticationProcessingFilter filter = new ApiTokenAuthenticationProcessingFilter(
                "/api/**"
                ,userRepository
                ,securityUserLoginHandler
                ,apiTokenFactory);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}

}
