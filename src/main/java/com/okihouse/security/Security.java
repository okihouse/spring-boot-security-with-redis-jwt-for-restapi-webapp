package com.okihouse.security;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.okihouse.repository.UserRepository;
import com.okihouse.security.api.filter.ApiTokenAuthenticationProcessingFilter;
import com.okihouse.security.api.token.ApiTokenFactory;
import com.okihouse.security.common.data.SecurityUrlData;
import com.okihouse.security.common.entrypoint.SecurityUserAccessEntryPoint;
import com.okihouse.security.common.filter.SecurityUserLoginProcessingFilter;
import com.okihouse.security.common.handler.SecurityUserAcessDeniedHandler;
import com.okihouse.security.common.handler.SecurityUserLoginHandler;
import com.okihouse.security.common.provider.SecurityAuthenticationProvider;
import com.okihouse.security.common.repository.PersistTokenRepository;
import com.okihouse.security.web.handler.WebSecurityUserLogoutHandler;

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
        SkipMatcher skipMatcher = new SkipMatcher("/api/**");
        ApiTokenAuthenticationProcessingFilter filter = new ApiTokenAuthenticationProcessingFilter(
                skipMatcher
                ,userRepository
                ,securityUserLoginHandler
                ,apiTokenFactory);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    public static class SkipMatcher implements RequestMatcher {

        private OrRequestMatcher skipRequestMatcher;

        private AntPathRequestMatcher antPathRequestMatcher;

        public SkipMatcher(String processUrl) {
            skipRequestMatcher = new OrRequestMatcher(
                Arrays.asList(
                    new AntPathRequestMatcher("/api/login"),
                    new AntPathRequestMatcher("/api/token")
                )
            );
            antPathRequestMatcher = new AntPathRequestMatcher(processUrl);
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            if(skipRequestMatcher.matches(request)){
                return false;
            }
            return antPathRequestMatcher.matches(request);
        }
    }

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}

}
