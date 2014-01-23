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
import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;
import static org.biokoframework.utils.matcher.Matchers.matchesPattern;
import static org.biokoframework.utils.matcher.Matchers.substringMatchesPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

import org.biokoframework.http.facebook.FacebookUtils;
import org.biokoframework.http.rest.exception.HttpError;
import org.biokoframework.http.rest.exception.HttpResponseBuilder;
import org.biokoframework.http.rest.exception.HttpResponseExceptionFactory;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.systema.commons.SystemAConstants;
import org.biokoframework.systema.http.SystemATestAbstract;

import java.util.HashMap;
import java.util.Properties;

import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.EntityBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FacebookCheckinTest extends SystemATestAbstract {

	private static String _mikolToken;

	private EntityBuilder<Login> _loginBuilder;

	@BeforeClass
	public static void loadTaxishareFBProps() throws Exception {
		Properties prop = new Properties();
		prop.load(FacebookCheckinTest.class.getClassLoader().getResourceAsStream("systemA.DEV.properties"));
		String taxishareId = (String) prop.get("appId");
		String taxishareAccessToken = (String) prop.get("appAccessToken");
		
		HashMap<String, String> tokenMap = FacebookUtils.retrieveTestUsersAccesTokens(taxishareId, taxishareAccessToken);
		_mikolToken = tokenMap.get(SystemAConstants.FB_MIKOL_ID);
	}
	
	@Before
	public void createFacebookUser() {
		_loginBuilder = new LoginBuilder().loadExample(LoginBuilder.MIKOL);
		_loginBuilder.set(Login.USER_EMAIL, SystemAConstants.FB_MIKOL_EMAIL);
		_loginBuilder.set(Login.FACEBOOK_ID, SystemAConstants.FB_MIKOL_ID);
			
		expect().
		statusCode(200).
		body(
			equalTo(JSonExpectedResponseBuilder.asJSONArray(
				_loginBuilder.setId("1").build(true)))).
		given().
		request().
		body(
			_loginBuilder.build(false).toJSONString()
		).
		post(getLoginUrl());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void doSimpleLogin() throws Exception {
		JSONObject request = new JSONObject();
		request.put(GenericFieldNames.FACEBOOK_TOKEN, _mikolToken);
		
		// Do Login into Facebook
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
		body(
			request.toJSONString()
		).
		post(getSystemAUrl() + GenericCommandNames.ENGAGED_CHECK_IN);

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void failBecauseOfWrongLogin() {
		String wrongToken = _mikolToken + "wrongToken";
		
		JSONObject request = new JSONObject();
		request.put(GenericFieldNames.FACEBOOK_TOKEN, wrongToken);
		
		HttpResponseBuilder builder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError error = builder.buildFrom(CommandExceptionsFactory.createFacebookAuthenticationFailure("OAuthException"));
		expect().
		statusCode(error.status()).
		body(
			matchesJSONString(JSONValue.toJSONString(error.body()))
		).
		when().
		given().
		body(
			request.toJSONString()
		).
		post(getSystemAUrl() + GenericCommandNames.ENGAGED_CHECK_IN);

	}
	
}
