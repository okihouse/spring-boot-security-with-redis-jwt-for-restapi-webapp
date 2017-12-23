package com.okihouse.security.common.repository.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RememberToken {

	private final String username;

	private String series;

	private String tokenValue;

	private Date date;

}
