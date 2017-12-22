package com.okihouse.config.security.api.token.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by okihouse16 on 2017. 12. 22..
 */
@Data
@Configuration
@ConfigurationProperties(prefix="security.token")
public class ApiTokenData {

    private String issuer;

    private Long expired;

    private Long refreshExpired;

    private String signingKey;

    public static final String AUTHORIZATION_REFRESH_TOKEN_NAME = "REFRESH_TOKEN";

    public static final String AUTHORIZATION_HEADER_NAME  = "Authorization";

    public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

}
