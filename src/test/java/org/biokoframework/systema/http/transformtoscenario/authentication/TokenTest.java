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
import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.biokoframework.http.authentication.AuthenticationUtils;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.systema.factory.SystemACommands;
import org.biokoframework.systema.http.SystemATestAbstract;

import java.util.List;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.Fields;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

public class TokenTest extends SystemATestAbstract {
	
	private static final String ENGAGED_AUTH_TOKEN_EXPIRE = "Engaged-Auth-Token-Expire";
	private static final String ENGAGED_AUTH_TOKEN = "Engaged-Auth-Token";
	
	private String _authenticationCommandUrl;
	private String _authenticationOptionalCommandUrl; 
	private String _authenticationUrl;
	private AuthenticationUtils _authUtils;
	
	@Before
	public void createUrls() {
		_authenticationCommandUrl = getLocalHostUrl() + Login.class.getSimpleName().toLowerCase() + '-' + "authenticated";
		_authenticationOptionalCommandUrl = _authenticationCommandUrl + "-optional";
		_authenticationUrl = getLocalHostUrl() + "authentication";
		_authUtils = new AuthenticationUtils(_authenticationUrl);
	
	}
	
	@Test
	public void successfulExecutionOfSecuredCommandWithTokenInBody() {
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample();
		given().
		body(loginBuilder.build(false).toJSONString()).
		post(getLocalHostUrl() + SystemACommands.LOGIN);
		
		String validToken = "00000000-0000-0000-0000-000000000000";
		long expire = _authUtils.postValidToken(validToken);
		
		EntityBuilder<Login> anOtherLoginBuilder = new LoginBuilder().loadExample(LoginBuilder.SIMONE);
		
		Fields requestBody = Fields.single(GenericFieldNames.AUTH_TOKEN, validToken);
		requestBody.put(Login.USER_EMAIL, anOtherLoginBuilder.get(Login.USER_EMAIL));
		requestBody.put(Login.PASSWORD, anOtherLoginBuilder.get(Login.PASSWORD));
		
		String actualTokenExpire = expect().
		statusCode(200).
		body(
				equalTo(JSonExpectedResponseBuilder.asArray(anOtherLoginBuilder.build(true).toJSONString()))
		).
		when().
		given().
		body(
				requestBody.toJSONString()
		).
		post(_authenticationCommandUrl).
		header(ENGAGED_AUTH_TOKEN_EXPIRE);
		
		assertThat(Long.parseLong(actualTokenExpire), greaterThan(expire));
	}
	
	@Test
	public void successfulExecutionOfSecuredCommandWithTokenInHeader() {
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample();
		given().
		body(loginBuilder.build(false).toJSONString()).
		post(getLocalHostUrl() + SystemACommands.LOGIN);
		
		String validToken = "00000000-0000-0000-0000-000000000000";
		long expire = _authUtils.postValidToken(validToken);
		
		EntityBuilder<Login> anOtherLoginBuilder = new LoginBuilder().loadExample(LoginBuilder.SIMONE);
		
		String actualTokenExpire = expect().
		statusCode(200).
		body(
				matchesJSONString(JSonExpectedResponseBuilder.asJSONArray(anOtherLoginBuilder.build(true)))
		).
		when().
		given().
		body(
				anOtherLoginBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD)
		).
		header(ENGAGED_AUTH_TOKEN, validToken).
		post(_authenticationCommandUrl).
		header(ENGAGED_AUTH_TOKEN_EXPIRE);
		
		assertThat(Long.parseLong(actualTokenExpire), greaterThan(expire));
	}
	
	@Test
	public void successfulExecutionOfOptionalSecuredCommandWithTokenInHeader() {
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample();
		given().
		body(loginBuilder.build(false).toJSONString()).
		post(getLocalHostUrl() + SystemACommands.LOGIN);
		
		String validToken = "00000000-0000-0000-0000-000000000000";
		long expire = _authUtils.postValidToken(validToken);
		
		EntityBuilder<Login> anOtherLoginBuilder = new LoginBuilder().loadExample(LoginBuilder.SIMONE);
		
		String actualTokenExpire = expect().
		statusCode(200).
		body(
				matchesJSONString(JSonExpectedResponseBuilder.asJSONArray(anOtherLoginBuilder.build(true)))
		).
		when().
		given().
		body(
				anOtherLoginBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD)
		).
		header(ENGAGED_AUTH_TOKEN, validToken).
		post(_authenticationOptionalCommandUrl).
		header(ENGAGED_AUTH_TOKEN_EXPIRE);
		
		assertThat(Long.parseLong(actualTokenExpire), greaterThan(expire));
	}
	
	@Test
	public void successfulExecutionOfOptionalSecuredCommandWithoutToken() {
		String validToken = "00000000-0000-0000-0000-000000000000";
		_authUtils.postValidToken(validToken);
		
		expect().
		statusCode(200).
		body(
				equalTo(createExpectedResponseJSON().toJSONString())
		).
		when().
		given().
		body(
				createLoginEntity().toJSONString()
		).
		post(_authenticationOptionalCommandUrl);
	}
	
	@Test
	public void failedExecutionBecauseOfBadToken() {		
		String unexistingToken = "00000000-0000-0000-0000-000000000001";
		
		List<ErrorEntity> errors = CommandExceptionsFactory.createTokenNotFoundException().getErrors();
		expect().
		statusCode(401).
		body(
				equalTo(JSONValue.toJSONString(errors))
		).
		when().
		given().
		body(
				createLoginWithToken(unexistingToken).toJSONString()
		).
		post(_authenticationCommandUrl);
	}
	
	@Test
	public void failedExecutionBecauseOfNoToken() {		
		List<ErrorEntity> errors = CommandExceptionsFactory.createUnauthorisedAccessException().getErrors();
		expect().
		statusCode(401).
		body(
				equalTo(JSONValue.toJSONString(errors))
		).
		when().
		given().
		contentType(ContentType.JSON).
		body(
				createLoginEntity().toJSONString()
		).
		post(_authenticationCommandUrl);
	}
	

	@Test
	public void failedExecutionBecauseOfTimeOut() {
		String expiredToken = "00000000-0000-0000-0000-000000000002";
		_authUtils.postToken(expiredToken, 1369038000); // 20/05/2013 - 08:20:00 GMT
		
		List<ErrorEntity> errors = CommandExceptionsFactory.createTokenExpiredException().getErrors();
		expect().
		statusCode(401).
		body(
				equalTo(JSONValue.toJSONString(errors))
		).
		when().
		given().
		contentType(ContentType.JSON).
		body(
				createLoginEntity().toJSONString()
		).
		headers(GenericFieldNames.TOKEN_HEADER, expiredToken).
		post(_authenticationCommandUrl);
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray createExpectedResponseJSON() {
		JSONArray expected = new JSONArray();
		JSONObject expectedLogin = createLoginEntity();
		expectedLogin.put(DomainEntity.ID, "1");
		expected.add(expectedLogin);
		return expected;
	}

	@SuppressWarnings("unchecked")
	private JSONObject createLoginWithToken(String token) {
		JSONObject loginAndToken = createLoginEntity();
		loginAndToken.put(GenericFieldNames.AUTH_TOKEN, token);
		return loginAndToken;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createLoginEntity() {
		JSONObject expectedLogin = new JSONObject();
		expectedLogin.put(GenericFieldNames.USER_EMAIL, "pino@home");
		expectedLogin.put(GenericFieldNames.PASSWORD, "1234");
		return expectedLogin;
	}
	
}
