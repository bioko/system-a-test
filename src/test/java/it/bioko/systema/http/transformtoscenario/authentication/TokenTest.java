package it.bioko.systema.http.transformtoscenario.authentication;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import it.bioko.http.authentication.AuthenticationUtils;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.systema.factory.SystemACommands;
import it.bioko.systema.http.SystemATestAbstract;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.EntityBuilder;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.Fields;

import java.util.List;

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
		_authenticationCommandUrl = getSystemAUrl() + Login.class.getSimpleName().toLowerCase() + '-' + "authenticated";
		_authenticationOptionalCommandUrl = _authenticationCommandUrl + "-optional";
		_authenticationUrl = getSystemAUrl() + "authentication";
		_authUtils = new AuthenticationUtils(_authenticationUrl);
	
	}
	
	@Test
	public void successfulExecutionOfSecuredCommandWithTokenInBody() {
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample();
		given().
		body(loginBuilder.build(false).toJSONString()).
		post(getSystemAUrl() + SystemACommands.LOGIN);
		
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
		post(getSystemAUrl() + SystemACommands.LOGIN);
		
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
		post(getSystemAUrl() + SystemACommands.LOGIN);
		
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
