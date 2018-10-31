package com.okihouse.security.common.repository.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RememberToken {

	private String username;

	private String series;

	private String tokenValue;

	private Date date;

}
