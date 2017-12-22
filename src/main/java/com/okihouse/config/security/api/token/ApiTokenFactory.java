package com.okihouse.config.security.api.token;

import com.okihouse.config.security.api.token.data.ApiTokenData;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
@Component
public class ApiTokenFactory {

    private static final Logger logger = LoggerFactory.getLogger(ApiTokenFactory.class);

    @Autowired
    private ApiTokenData apiTokenData;

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("Can't create token without username");
        }

        @SuppressWarnings("unchecked")
		List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) authentication.getAuthorities();
        if (grantedAuthorities == null || grantedAuthorities.isEmpty()) {
            throw new BadCredentialsException("User doesn't have any privileges");
        }

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("scopes", grantedAuthorities.stream().map(s -> s.toString()).collect(Collectors.toList()));

        LocalDateTime currentTime = LocalDateTime.now();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(apiTokenData.getIssuer())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(apiTokenData.getExpired())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, apiTokenData.getSigningKey())
                .compact();
        return token;
    }

    public String createRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("Can't create token without username");
        }

        @SuppressWarnings("unchecked")
        List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) authentication.getAuthorities();
        if (grantedAuthorities == null || grantedAuthorities.isEmpty()) {
            throw new BadCredentialsException("User doesn't have any privileges");
        }

        Claims claims = Jwts.claims().setSubject(username);
        List<String> scopes  = grantedAuthorities.stream().map(s -> s.toString()).collect(Collectors.toList());
        scopes.add(ApiTokenData.AUTHORIZATION_REFRESH_TOKEN_NAME);
        claims.put("scopes", scopes);

        LocalDateTime currentTime = LocalDateTime.now();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(apiTokenData.getIssuer())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(apiTokenData.getRefreshExpired())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, apiTokenData.getSigningKey())
                .compact();
        return token;
    }

    public Map<String, Object> getBody(String token){
        return parseClaims(token).getBody();
    }

    public String extract(String header) {
        if (StringUtils.isBlank(header)) {
            throw new AuthenticationServiceException("Authorization header cannot be blank!");
        }

        if (header.length() <= ApiTokenData.AUTHORIZATION_HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Invalid authorization header size.");
        }
        return header.substring(ApiTokenData.AUTHORIZATION_HEADER_PREFIX.length(), header.length());
    }

    private Jws<Claims> parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(apiTokenData.getSigningKey())
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            logger.warn("Request token is expired. error={}", e.getMessage());
            throw new BadCredentialsException("Request token is expired.");
        } catch (Exception e) {
            logger.error("Request token is invalid. token={}, error={}", token, e.getMessage());
            throw new BadCredentialsException("Request token is invalid.");
        }
    }

}
