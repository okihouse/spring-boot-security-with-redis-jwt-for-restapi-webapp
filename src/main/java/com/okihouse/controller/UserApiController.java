package com.okihouse.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by okihouse16 on 2017. 12. 22..
 */
@RestController
public class UserApiController {

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @RequestMapping(value = "/api/mypage", method = RequestMethod.GET)
    @ResponseBody
    public UsernamePasswordAuthenticationToken mypage(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        return usernamePasswordAuthenticationToken;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/api/admin", method = RequestMethod.GET)
    @ResponseBody
    public UsernamePasswordAuthenticationToken admin(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        return usernamePasswordAuthenticationToken;
    }
}
