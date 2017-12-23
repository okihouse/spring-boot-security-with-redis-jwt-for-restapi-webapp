package com.okihouse.security.api.token;

import com.okihouse.security.api.token.data.ApiTokenData;
import io.jsonwebtoken.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by okihouse16 on 2017. 12. 22..
 */
@RestController
public class ApiRefreshTokenController {

    @Autowired
    private ApiTokenFactory apiTokenFactory;

    @RequestMapping(value = "/api/token", method = RequestMethod.GET)
    @ResponseBody
    public RefreshTokenData token(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String accessToken = apiTokenFactory.extract(token);
        Assert.hasLength(accessToken, "Request token is invalid.");

        Map<String, Object> body = apiTokenFactory.getBody(accessToken);
        @SuppressWarnings("unchecked")
		List<String> scopes = (List<String>) body.get("scopes");
        if(scopes.isEmpty() || !scopes.contains(ApiTokenData.AUTHORIZATION_REFRESH_TOKEN_NAME)) {
            throw new IllegalArgumentException("Request token is not 'REFRESH_TOKEN'.");
        }
        scopes.remove(ApiTokenData.AUTHORIZATION_REFRESH_TOKEN_NAME);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        scopes.stream()
                .forEach(r -> grantedAuthorities.add(new SimpleGrantedAuthority(r.toString())));

        String username = (String) body.get("sub");

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        String newAccessToken = apiTokenFactory.createToken(authentication);
        String newRefreshAccessToken = apiTokenFactory.createRefreshToken(authentication);
        return new RefreshTokenData(newAccessToken, newRefreshAccessToken);
    }

    @Data
    @AllArgsConstructor
    public static class RefreshTokenData {

        private String newAccessToken;

        private String newRefreshAccessToken;

    }

}
