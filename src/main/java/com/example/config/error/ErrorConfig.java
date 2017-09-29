package com.example.config.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.example.config.security.url.SecurityUrlInformation;

@Configuration
public class ErrorConfig implements EmbeddedServletContainerCustomizer {

	@Autowired private SecurityUrlInformation urlInformation;

	@Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, urlInformation.getLogin()));
    }

}
