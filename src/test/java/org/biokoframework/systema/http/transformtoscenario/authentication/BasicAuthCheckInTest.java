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

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
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

public class BasicAuthCheckInTest extends SystemATestAbstract {
		
	private static final String BASIC_CHECKIN_COMMAND = "engaged-check-in";
	private String _basicCheckInUrl;
	private Login _login;

	@Before
	public void createCheckInUrl() {
		_basicCheckInUrl = getLocalHostUrl()  + BASIC_CHECKIN_COMMAND;
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
		
		String authentication = anUnexistingLogin.get(GenericFieldNames.USER_EMAIL).toString() 
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
			
		String authentication = _login.get(GenericFieldNames.USER_EMAIL).toString()
								+ ':' + _login.get(GenericFieldNames.PASSWORD);
		String base64Authentication = Base64.encodeBase64String(authentication.getBytes("utf-8"));
		
		expect().
		statusCode(200).
		header("Engaged-Auth-Token", matchesPattern("(\\d|a|b|c|d|e|f|-)+?")).
		header("Engaged-Auth-Token-Expire", matchesPattern("\\d+?")).
		body(
			allOf(
				substringMatchesPattern("\"authToken\":\"(\\d|a|b|c|d|e|f|-)+?\""),
				substringMatchesPattern("\"authTokenExpire\":\\d+")
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
			
		String authentication = _login.get(GenericFieldNames.USER_EMAIL).toString()
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
