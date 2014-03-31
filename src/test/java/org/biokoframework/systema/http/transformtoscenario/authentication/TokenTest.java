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

import com.jayway.restassured.http.ContentType;
import org.biokoframework.http.authentication.AuthenticationUtils;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.services.currenttime.impl.TestCurrentTimeService;
import org.biokoframework.systema.factory.SystemACommands;
import org.biokoframework.systema.http.SystemATestAbstract;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.Fields;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.biokoframework.utils.matcher.Matchers.after;
import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TokenTest extends SystemATestAbstract {
	
	private static final String ENGAGED_AUTH_TOKEN_EXPIRE = "Engaged-Auth-Token-Expire";
	private static final String ENGAGED_AUTH_TOKEN = "Engaged-Auth-Token";
	
	private String fAuthenticationCommandUrl;
	private String fAuthenticationOptionalCommandUrl;
	private String fAuthenticationUrl;
	private AuthenticationUtils fAuthUtils;
    private DateTime fNow;

    @Before
	public void createUrls() {
		fAuthenticationCommandUrl = getLocalHostUrl() + SystemACommands.LOGIN_AUTHENTICATED;
		fAuthenticationOptionalCommandUrl = getLocalHostUrl() + SystemACommands.LOGIN_AUTHENTICATED_OPTIONAL;
		fAuthenticationUrl = getLocalHostUrl() + "authentication";
		fAuthUtils = new AuthenticationUtils(fAuthenticationUrl);
	}

    @Before
    public void setCurrentTime() {
        TestCurrentTimeService.setCalendar("2013-12-03T11:45:00+01:00");
        fNow = ISODateTimeFormat.dateTimeNoMillis().parseDateTime("2013-12-03T11:45:00+01:00");
    }
	
	@Test
	public void successfulExecutionOfSecuredCommandWithTokenInBody() {
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample();
		given().
        contentType(ContentType.JSON).
        body(loginBuilder.build(false).toJSONString()).
		post(getLocalHostUrl() + SystemACommands.LOGIN);
		
		String validToken = "00000000-0000-0000-0000-000000000000";
		fAuthUtils.postValidToken(validToken);
		
		EntityBuilder<Login> anOtherLoginBuilder = new LoginBuilder().loadExample(LoginBuilder.SIMONE);
		
		Fields requestBody = new Fields(
				GenericFieldNames.AUTH_TOKEN, validToken,
				Login.USER_EMAIL, anOtherLoginBuilder.get(Login.USER_EMAIL),
				Login.PASSWORD, anOtherLoginBuilder.get(Login.PASSWORD));
		
		String actualTokenExpire = expect().
		statusCode(200).
		body(
                equalTo(JSonExpectedResponseBuilder.asArray(anOtherLoginBuilder.build(true).toJSONString()))
        ).
		when().
		given().
        contentType(ContentType.JSON).
		body(
                requestBody.toJSONString()
        ).
		post(fAuthenticationCommandUrl).
		header(ENGAGED_AUTH_TOKEN_EXPIRE);

        assertThat(ISODateTimeFormat.dateTimeNoMillis().parseDateTime(actualTokenExpire), is(after(fNow)));
	}
	
	@Test
	public void successfulExecutionOfSecuredCommandWithTokenInHeader() {
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample();
		given().
        contentType(ContentType.JSON).
		body(loginBuilder.build(false).toJSONString()).
		post(getLocalHostUrl() + "login");
		
		String validToken = "00000000-0000-0000-0000-000000000000";
		fAuthUtils.postValidToken(validToken);
		
		EntityBuilder<Login> anOtherLoginBuilder = new LoginBuilder().loadExample(LoginBuilder.SIMONE);
		
		String actualTokenExpire = expect().
		statusCode(200).
		body(
				matchesJSONString(JSonExpectedResponseBuilder.asJSONArray(anOtherLoginBuilder.build(true)))
		).
		when().
		given().
        contentType(ContentType.JSON).
		body(
				anOtherLoginBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD)
		).
		header(ENGAGED_AUTH_TOKEN, validToken).
		post(fAuthenticationCommandUrl).
		header(ENGAGED_AUTH_TOKEN_EXPIRE);
		
		assertThat(ISODateTimeFormat.dateTimeNoMillis().parseDateTime(actualTokenExpire), is(after(fNow)));
	}
	
	@Test
	public void successfulExecutionOfOptionalSecuredCommandWithTokenInHeader() {
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadExample(LoginBuilder.MATTIA);

        expect().
        statusCode(200).
        when().
		given().
        contentType(ContentType.JSON).
		body(loginBuilder.build(false).toJSONString()).
		post(getLocalHostUrl() + SystemACommands.LOGIN);
		
		String validToken = "00000000-0000-0000-0000-000000000000";
		fAuthUtils.postValidToken(validToken);
		
		EntityBuilder<Login> anOtherLoginBuilder = new LoginBuilder().loadExample(LoginBuilder.SIMONE);
		
		String actualTokenExpireStr = expect().
		statusCode(200).
		body(
                matchesJSONString(JSonExpectedResponseBuilder.asJSONArray(anOtherLoginBuilder.build(true)))
        ).
		when().
		given().
        contentType(ContentType.JSON).
		body(
				anOtherLoginBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD)
		).
		header(ENGAGED_AUTH_TOKEN, validToken).
		post(fAuthenticationOptionalCommandUrl).
		header(ENGAGED_AUTH_TOKEN_EXPIRE);

        DateTime actualTokenExpire = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(actualTokenExpireStr);
		assertThat(actualTokenExpire, is(after(fNow)));
	}



    @Test
	public void successfulExecutionOfOptionalSecuredCommandWithoutToken() {
		String validToken = "00000000-0000-0000-0000-000000000000";
		fAuthUtils.postValidToken(validToken);
		
		expect().
		statusCode(200).
		body(
				equalTo(createExpectedResponseJSON().toJSONString())
		).
		when().
		given().
        contentType(ContentType.JSON).
		body(
				createLoginEntity().toJSONString()
		).
		post(fAuthenticationOptionalCommandUrl);
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
        contentType(ContentType.JSON).
		body(
                new Fields(GenericFieldNames.AUTH_TOKEN, unexistingToken).toJSONString()
        ).
		post(fAuthenticationCommandUrl);
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
		post(fAuthenticationCommandUrl);
	}
	

	@Test
	public void failedExecutionBecauseOfTimeOut() {
		String expiredToken = "00000000-0000-0000-0000-000000000002";
		fAuthUtils.postToken(expiredToken, ISODateTimeFormat.dateTimeNoMillis().parseDateTime("2013-05-20T08:20:00Z"));
		
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
		post(fAuthenticationCommandUrl);
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
	private JSONObject createLoginEntity() {
		JSONObject expectedLogin = new JSONObject();
		expectedLogin.put(GenericFieldNames.USER_EMAIL, "pino@home");
		expectedLogin.put(GenericFieldNames.PASSWORD, "1234");
		return expectedLogin;
	}
	
}
