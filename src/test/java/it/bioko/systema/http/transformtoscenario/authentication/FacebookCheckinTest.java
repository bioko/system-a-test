package it.bioko.systema.http.transformtoscenario.authentication;

import static com.jayway.restassured.RestAssured.expect;
import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import static it.bioko.utils.matcher.Matchers.matchesPattern;
import static it.bioko.utils.matcher.Matchers.substringMatchesPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.http.facebook.FacebookUtils;
import it.bioko.http.rest.exception.HttpError;
import it.bioko.http.rest.exception.HttpResponseBuilder;
import it.bioko.http.rest.exception.HttpResponseExceptionFactory;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.systema.commons.SystemAConstants;
import it.bioko.systema.http.SystemATestAbstract;
import it.bioko.utils.domain.EntityBuilder;

import java.util.HashMap;
import java.util.Properties;

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
