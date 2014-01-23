package it.bioko.systema.http.transformtoscenario.authentication;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static it.bioko.utils.matcher.Matchers.matchesPattern;
import static it.bioko.utils.matcher.Matchers.substringMatchesPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.systema.http.SystemATestAbstract;
import it.bioko.utils.domain.EntityBuilder;
import it.bioko.utils.domain.ErrorEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

public class BasicAuthCheckInTest extends SystemATestAbstract {
		
	private static final String BASIC_CHECKIN_COMMAND = "engaged-check-in";
	private String _basicCheckInUrl;
	private Login _login;

	@Before
	public void createCheckInUrl() {
		_basicCheckInUrl = getSystemAUrl()  + BASIC_CHECKIN_COMMAND;
	}
		
	@Before
	public void setUpLoginEntity() {
		EntityBuilder<Login> builder = new LoginBuilder().loadDefaultExample();
		builder = new LoginBuilder();
		builder.set(Login.USER_EMAIL, "pino@home");
		builder.set(Login.PASSWORD, "secret");
		_login = builder.build(false);
		
		expect().
		statusCode(200).
		given().
		request().
		body(
			_login.toJSONString()
		).
		post(getLoginUrl());
	}

	@Test
	public void failureLoginNotExisting() throws Exception {
		Login anUnexistingLogin = new LoginBuilder().loadDefaultExample().build(false);
		
		String authentication = anUnexistingLogin.get(GenericFieldNames.USER_EMAIL) 
				+ ':' + anUnexistingLogin.get(GenericFieldNames.PASSWORD);
		String base64Authentication = Base64.encodeBase64String(authentication.getBytes("utf-8"));
		
		List<ErrorEntity> erros = CommandExceptionsFactory.createInvalidLoginException().getErrors();		
		expect().
		statusCode(401).
		body(
				equalTo(JSONValue.toJSONString(erros))
		).
		when().
		given().
		header("Authorization", "Basic " + base64Authentication).
		contentType(ContentType.JSON).
		post(_basicCheckInUrl);
	}
		
	@Test
	public void successfullLoginWithBasicAuthentication() throws UnsupportedEncodingException {
			
		String authentication = _login.get(GenericFieldNames.USER_EMAIL) 
								+ ':' + _login.get(GenericFieldNames.PASSWORD);
		String base64Authentication = Base64.encodeBase64String(authentication.getBytes("utf-8"));
		
		expect().
		statusCode(200).
		header("Engaged-Auth-Token", matchesPattern("(\\d|a|b|c|d|e|f|-)+?")).
		header("Engaged-Auth-Token-Expire", matchesPattern("\\d+?")).
		body(
			allOf(
				substringMatchesPattern("\"authToken\":\"(\\d|a|b|c|d|e|f|-)+?\""),
				substringMatchesPattern("\"authTokenExpire\":\"\\d+?\"")
			)
		).
		when().
		given().
		contentType(ContentType.JSON).
		header("Authorization", "Basic " + base64Authentication).
		post(_basicCheckInUrl);
	}
	
	@Test
	public void failureWrongPassword() throws UnsupportedEncodingException {
			
		given().
		request().
		body(
				_login.toJSONString()
		).
		post(getLoginUrl());
		
		_login.set(GenericFieldNames.PASSWORD, "WrongPassword");
			
		String authentication = _login.get(GenericFieldNames.USER_EMAIL) 
					+ ':' + _login.get(GenericFieldNames.PASSWORD);
		String base64Authentication = Base64.encodeBase64String(authentication.getBytes("utf-8"));
			
		List<ErrorEntity> errors = CommandExceptionsFactory.createInvalidLoginException().getErrors();
		expect().
		statusCode(401).
		body(
				equalTo(JSONValue.toJSONString(errors))
		).
		when().
		given().
		contentType(ContentType.JSON).
		header("Authorization", "Basic " + base64Authentication).
		post(_basicCheckInUrl);
	}
	
}
