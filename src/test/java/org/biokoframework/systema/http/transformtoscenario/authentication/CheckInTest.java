/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.biokoframework.systema.http.transformtoscenario.authentication;


import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.biokoframework.utils.matcher.Matchers.matchesPattern;
import static org.biokoframework.utils.matcher.Matchers.substringMatchesPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.systema.http.SystemATestAbstract;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.domain.ErrorEntity;
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
		_checkInUrl = getLocalHostUrl()  + CHECKIN_COMMAND;
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