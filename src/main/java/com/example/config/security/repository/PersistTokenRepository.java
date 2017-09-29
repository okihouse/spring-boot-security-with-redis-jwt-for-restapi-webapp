package com.example.config.security.repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import com.example.config.security.repository.vo.RememberToken;
import com.example.util.JsonUtils;

@Service
public class PersistTokenRepository implements PersistentTokenRepository {

	private static final Logger logger = LoggerFactory.getLogger(PersistTokenRepository.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		RememberToken rememberToken =
				new RememberToken(token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
		stringRedisTemplate.opsForValue().set(token.getSeries(), JsonUtils.toJson(rememberToken), 30, TimeUnit.DAYS);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		String payload = stringRedisTemplate.opsForValue().get(series);
		try {
			RememberToken rememberToken = JsonUtils.fromJson(payload, RememberToken.class);
			rememberToken.setTokenValue(tokenValue);
			rememberToken.setDate(lastUsed);
			stringRedisTemplate.opsForValue().set(series, JsonUtils.toJson(rememberToken), 30, TimeUnit.DAYS);
			logger.debug("Remember me token is updated. seried={}", series);
		} catch (JSONException e) {
			logger.error("Persistent token is not valid. payload={}, error={}", payload, e);
		}
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		String payload = stringRedisTemplate.opsForValue().get(seriesId);
		if(payload == null) return null;
		try {
			RememberToken rememberToken = JsonUtils.fromJson(payload, RememberToken.class);
			PersistentRememberMeToken token = new PersistentRememberMeToken(
					rememberToken.getUsername()
					,seriesId
					,rememberToken.getTokenValue()
					,rememberToken.getDate());
			return token;
		} catch (JSONException e) {
			logger.error("Persistent token is not valid. payload={}, error={}", payload, e);
			return null;
		}
	}

	@Override
	public void removeUserTokens(String username) {
		// Skip this scenario, because redis set only unique key.
	}

}
