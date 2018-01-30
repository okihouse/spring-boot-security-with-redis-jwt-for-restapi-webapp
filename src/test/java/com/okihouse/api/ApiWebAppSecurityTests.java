package com.okihouse.api;

import com.okihouse.security.api.token.ApiTokenFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
public class ApiWebAppSecurityTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ApiTokenFactory apiTokenFactory;

    private MockMvc mvc;

    private String userAccessToken;
    private String adminAccessToken;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userAccessToken = apiTokenFactory.createToken(
                            new UsernamePasswordAuthenticationToken(
                                "user",
                                "password",
                                Arrays.asList(new SimpleGrantedAuthority("READ_MYPAGE"))));

        adminAccessToken = apiTokenFactory.createToken(
                            new UsernamePasswordAuthenticationToken(
                                "admin",
                                "password",
                                Arrays.asList(
                                    new SimpleGrantedAuthority("READ_MYPAGE"),
                                    new SimpleGrantedAuthority("READ_ADMIN")
                                )
                            ));
    }

	@Test
	public void user_test() throws Exception {
        mvc
            .perform(get("/api/mypage")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().is2xxSuccessful());
	}

	@Test
	public void user_access_denied_test() throws Exception {
        mvc
            .perform(get("/api/admin")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	}

	@Test
	public void admin_test() throws Exception {
        mvc
            .perform(get("/api/mypage")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().is2xxSuccessful());
	}
	@Test
	public void admin_access_test() throws Exception {
        mvc
            .perform(get("/api/admin")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().is2xxSuccessful());
	}

}
