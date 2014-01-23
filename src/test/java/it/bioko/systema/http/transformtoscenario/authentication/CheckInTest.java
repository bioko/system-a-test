package it.bioko.systema.http.transformtoscenario.authentication;


import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static it.bioko.utils.matcher.Matchers.matchesPattern;
import static it.bioko.utils.matcher.Matchers.substringMatchesPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.systema.http.SystemATestAbstract;
import it.bioko.utils.domain.EntityBuilder;
import it.bioko.utils.domain.ErrorEntity;

import java.util.List;

import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

public class CheckInTest extends SystemATestAbstract {
	
	private static final String CHECKIN_COMMAND = "engaged-check-in";
	private String _checkInUrl;

	private EntityBuilder<Login> _builder;
	
	@Before
	public void createCheckInUrl() {
		_checkInUrl = getSystemAUrl()  + CHECKIN_COMMAND;
	}
	
	@Before
	public void setUpLoginEntity() {
		_builder = new LoginBuilder().loadDefaultExample();
		_builder.set(Login.USER_EMAIL, "pino@home");
		_builder.set(Login.PASSWORD, "secret");
	}
	
	@Test
	public void failureLoginNotExisting() {
		LoginBuilder anOtherBuilder = new LoginBuilder();
		given().
		request().
		body(
				anOtherBuilder.build(false).toJSONString()
		).
		post(getLoginUrl());
		
		List<ErrorEntity> errors = CommandExceptionsFactory.createInvalidLoginException().getErrors();
		expect().
		statusCode(401).
		body(
				equalTo(JSONValue.toJSONString(errors))
		).
		when().
		given().
		contentType(ContentType.JSON).
		body (
				_builder.build(false).toJSONString()
		).
		post(_checkInUrl);
	}
	
	@Test
	public void successfullLoginWithEngagedAuthentication() {
		
		given().
		request().
		body(
				_builder.build(false).toJSONString()
		).
		post(getLoginUrl());
		
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
		body(
				_builder.build(false).toJSONString()
		).
		post(_checkInUrl);
	}
	
	@Test
	public void failureWrongPassword() {
		
		given().
		request().
		body(
				_builder.build(false).toJSONString()
		).
		post(getLoginUrl());
		
		_builder.set(Login.PASSWORD, "WrongPassword");
		
		List<ErrorEntity> errors = CommandExceptionsFactory.createInvalidLoginException().getErrors();
		expect().
		statusCode(401).
		body(
				equalTo(JSONValue.toJSONString(errors))
		).
		when().
		given().
		contentType(ContentType.JSON).
		body(
				_builder.build(false).toJSONString()
		).
		post(_checkInUrl);
	}
	
	@Test
	public void successfullLoginOfAdmin() {
		EntityBuilder<Login> adminBuilder = new LoginBuilder().loadExample(LoginBuilder.GENERIC_USER_WITH_ADMIN_ROLE);
		given().
		request().
		body(
				adminBuilder.build(false).toJSONString()
		).
		post(getLoginUrl());
		
		expect().
		statusCode(200).
		header("Engaged-Auth-Token", matchesPattern("(\\d|a|b|c|d|e|f|-)+?")).
		header("Engaged-Auth-Token-Expire", matchesPattern("\\d+?")).
		body(
				allOf(
						substringMatchesPattern("\"authToken\":\"(\\d|a|b|c|d|e|f|-)+?\""),
						substringMatchesPattern("\"authTokenExpire\":\"\\d+?\""),
						substringMatchesPattern("\"roles\":\"admin\"")
				)
		).
		when().
		given().
		contentType(ContentType.JSON).
		body(
				adminBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD)
		).
		post(_checkInUrl);
	}
}