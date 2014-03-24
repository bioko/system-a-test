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

package org.biokoframework.systema.http.scenarios;

import org.biokoframework.http.rest.exception.HttpError;
import org.biokoframework.http.scenario.ExecutionScenarioStep;
import org.biokoframework.http.scenario.HttpScenarioFactory;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.services.random.impl.TestRandomGeneratorService;
import org.biokoframework.systema.factory.SystemACommands;
import org.json.simple.JSONValue;

import java.util.HashMap;

import static org.biokoframework.http.matcher.Matchers.matchesAuthenticationResponse;
import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;

public class AuthenticationStoriesFactory {
		
	private static HashMap<String, String> sTokenMap = new HashMap<>();
	
	public static Scenario registerTestUsers() throws Exception {
		Scenario scenario = new Scenario("Register test users");

		LoginBuilder loginBuilder = new LoginBuilder();
		
		
		scenario.addScenarioStep("add generic user", HttpScenarioFactory.postSuccessful(SystemACommands.LOGIN, 
				null, null, loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITHOUT_ROLE).build(false).toJSONString(), 
				matchesJSONString("["+JSONValue.toJSONString(loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITHOUT_ROLE).setId("1").build(true))+"]")));
		
		scenario.addScenarioStep("add admin user", HttpScenarioFactory.postSuccessful(SystemACommands.LOGIN, 
				null, null, loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_ADMIN_ROLE).build(false).toJSONString(), 
				matchesJSONString("["+JSONValue.toJSONString(loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_ADMIN_ROLE).setId("2").build(true))+"]")));
		
		scenario.addScenarioStep("add user with another role", HttpScenarioFactory.postSuccessful(SystemACommands.LOGIN, 
				null, null, loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_ANOTHER_ROLE).build(false).toJSONString(), 
				matchesJSONString("["+JSONValue.toJSONString(loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_ANOTHER_ROLE).setId("3").build(true))+"]")));
		
		scenario.addScenarioStep("add user with both roles", HttpScenarioFactory.postSuccessful(SystemACommands.LOGIN, 
				null, null, loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_BOTH_ROLES).build(false).toJSONString(), 
				matchesJSONString("["+JSONValue.toJSONString(loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_BOTH_ROLES).setId("4").build(true))+"]")));
		
		
		return scenario;
	}

	
	public static Scenario loginAsUser() throws Exception{
		Scenario scenario = new Scenario("Login as user");
		
		scenario.addScenarioStep("Prepare random", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestRandomGeneratorService.setSingleRandomValue("authToken", "550e8400-e29b-41d4-a716-446655440000");
			}
		});
		
		LoginBuilder loginBuilder = new LoginBuilder();
		
		scenario.addScenario(registerTestUsers());
		
		String loginJson = loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITHOUT_ROLE).getJsonForFields(Login.USER_EMAIL,Login.PASSWORD);			
		scenario.addScenarioStep("login as user", HttpScenarioFactory.postSuccessful(SystemACommands.ENGAGED_CHECK_IN, 
				null, null, loginJson, matchesAuthenticationResponse(sTokenMap)));
				
		return scenario;		
	}
	
	public static Scenario loginAsAdmin() throws Exception {
		Scenario scenario = new Scenario("Login as admin");
		
		LoginBuilder loginBuilder = new LoginBuilder();
		
		scenario.addScenarioStep("Prepare random", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestRandomGeneratorService.setSingleRandomValue("authToken", "550e8400-e29b-41d4-a716-446655440000");
			}
		});
		
		scenario.addScenario(registerTestUsers());
		
		String loginJson = loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_ADMIN_ROLE).getJsonForFields(Login.USER_EMAIL,Login.PASSWORD);			
		scenario.addScenarioStep("login as admin", HttpScenarioFactory.postSuccessful(SystemACommands.ENGAGED_CHECK_IN, 
				null, null, loginJson, matchesAuthenticationResponse(sTokenMap)));
				
		return scenario;		
	}
	
	public static Scenario loginAsAnotherRole() throws Exception{
		Scenario scenario = new Scenario("Login as user with another role");		
		
		scenario.addScenarioStep("Prepare random", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestRandomGeneratorService.setSingleRandomValue("authToken", "550e8400-e29b-41d4-a716-446655440000");
			}
		});
		
		LoginBuilder loginBuilder = new LoginBuilder();
		
		scenario.addScenario(registerTestUsers());
		
		String loginJson = loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_ANOTHER_ROLE).getJsonForFields(Login.USER_EMAIL,Login.PASSWORD);			
		scenario.addScenarioStep("login as admin", HttpScenarioFactory.postSuccessful(SystemACommands.ENGAGED_CHECK_IN, 
				null, null, loginJson, matchesAuthenticationResponse(sTokenMap)));
				
		return scenario;		
	}
	
	public static Scenario loginAsBothRoles() throws Exception{
		Scenario scenario = new Scenario("Login as user with both roles");		
		
		LoginBuilder loginBuilder = new LoginBuilder();
		
		scenario.addScenarioStep("Prepare random", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestRandomGeneratorService.setSingleRandomValue("authToken", "550e8400-e29b-41d4-a716-446655440000");
			}
		});
		
		scenario.addScenario(registerTestUsers());
		
		String loginJson = loginBuilder.loadExample(LoginBuilder.GENERIC_USER_WITH_BOTH_ROLES).getJsonForFields(Login.USER_EMAIL,Login.PASSWORD);			
		scenario.addScenarioStep("login as admin", HttpScenarioFactory.postSuccessful(SystemACommands.ENGAGED_CHECK_IN, 
				null, null, loginJson, matchesAuthenticationResponse(sTokenMap)));
				
		return scenario;		
	}
	
	
	public static Scenario testAuthWithoutRole() throws Exception {
		Scenario scenario = new Scenario("test authentication without roles");
		
		String testJson = "{\"gino\":\"pino\"}";
		
		scenario.addScenarioStep("Prepare random", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestRandomGeneratorService.setSingleRandomValue("authToken", "550e8400-e29b-41d4-a716-446655440000");
			}
		});
		
		scenario.addScenarioStep("use public command without login and success", HttpScenarioFactory.postSuccessful(
				SystemACommands.DUMMY_COMMAND_NOT_AUTHENTICATED, null, null, testJson, matchesJSONString("[]")));
		
		
		HttpError expectedError = JSonExpectedResponseBuilder.authenticationRequired();
		
		scenario.addScenarioStep("use generic auth command with fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_WITHOUT_ROLES, 
				null, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		scenario.addScenarioStep("use admin auth command with fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ADMIN, 
				null, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		scenario.addScenario(loginAsUser());
		
		scenario.addScenarioStep("after login use generic auth command with success", HttpScenarioFactory.postSuccessful(
				SystemACommands.DUMMY_COMMAND_NOT_AUTHENTICATED, sTokenMap, null, testJson, matchesJSONString("[]")));
		
		
		return scenario;
	}
	
	public static Scenario testAuthWithRoleAndFail() throws Exception {
		Scenario scenario = new Scenario("test authentication with roles and fail because the user have not privileges");
		
		scenario.addScenarioStep("Prepare random", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestRandomGeneratorService.setSingleRandomValue("authToken", "550e8400-e29b-41d4-a716-446655440000");
			}
		});
		
		scenario.addScenario(loginAsUser());
		
		HttpError expectedError = JSonExpectedResponseBuilder.insufficientPrivileges();		
		String testJson = "{\"gino\":\"pino\"}";		
		scenario.addScenarioStep("", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ADMIN, 
				sTokenMap, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
	
	public static Scenario testAuthWithRoleAndSuccess() throws Exception {
		Scenario scenario = new Scenario("test authentication with roles and successs");
		
		scenario.addScenario(loginAsAdmin());		
				
		String testJson = "{\"gino\":\"pino\"}";		
		scenario.addScenarioStep("use command for admins", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ADMIN, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		
		return scenario;
	}
	
	
	public static Scenario testAuthWithRoleWithUserWithAdmin() throws Exception {
		Scenario scenario = new Scenario("test authentication with roles using combination of commands with a user with admin role");
		String testJson = "{\"gino\":\"pino\"}";
		HttpError expectedError = JSonExpectedResponseBuilder.insufficientPrivileges();
		
		scenario.addScenario(loginAsAdmin());
		scenario.addScenarioStep("use not auth command and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_NOT_AUTHENTICATED, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		scenario.addScenarioStep("use command for admins with admin and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ADMIN, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		scenario.addScenarioStep("use command for another with  admin and fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ANOTHER, 
				sTokenMap, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		scenario.addScenarioStep("use command for both with admin user and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_FOR_BOTH, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		
		return scenario;
	}
	
	
	public static Scenario testAuthWithRoleWithUserWithBoth() throws Exception {
		Scenario scenario = new Scenario("test authentication with roles using combination of commands with a user with both roles");
		String testJson = "{\"gino\":\"pino\"}";
		
		scenario.addScenario(loginAsBothRoles());
		scenario.addScenarioStep("use not auth command and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_NOT_AUTHENTICATED, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		scenario.addScenarioStep("use command for admins with both user and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ADMIN, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		scenario.addScenarioStep("use command for another with both user and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ANOTHER, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		scenario.addScenarioStep("use command for both with both user and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_FOR_BOTH, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		
		
		return scenario;
	}

	
	public static Scenario testAuthWithRoleWithUserWithAnother() throws Exception {
		Scenario scenario = new Scenario("test authentication with roles using combination of commands with a user with another role");
		String testJson = "{\"gino\":\"pino\"}";
		HttpError expectedError = JSonExpectedResponseBuilder.insufficientPrivileges();
		
		scenario.addScenario(loginAsAnotherRole());
		scenario.addScenarioStep("use not auth command and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_NOT_AUTHENTICATED, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		scenario.addScenarioStep("use command for another with another and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ANOTHER, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		scenario.addScenarioStep("use command for admin with  another and fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ADMIN, 
				sTokenMap, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		scenario.addScenarioStep("use command for both with another user and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_FOR_BOTH, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));
		
		return scenario;
	}
	
	public static Scenario testAuthWithRoleWithoutUser() throws Exception {
		Scenario scenario = new Scenario("test authentication with roles using combination of commands with a user with another role");
		String testJson = "{\"gino\":\"pino\"}";
		HttpError expectedError = JSonExpectedResponseBuilder.authenticationRequired();
				
		scenario.addScenarioStep("use not auth command and success", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMMAND_NOT_AUTHENTICATED, 
				sTokenMap, null, testJson,  matchesJSONString("[]")));		
		scenario.addScenarioStep("use auth command and fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_WITHOUT_ROLES, 
				sTokenMap, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		scenario.addScenarioStep("use command for admin and fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ADMIN, 
				sTokenMap, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		scenario.addScenarioStep("use command for another and fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_ONYL_FOR_ANOTHER, 
				sTokenMap, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		scenario.addScenarioStep("use command for both and fail", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMMAND_AUTHENTICATED_FOR_BOTH, 
				sTokenMap, null, testJson, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		
		return scenario;
	}
	
	
	public static Scenario checkAuthLoginIdPassed() throws Exception {
		Scenario scenario = new Scenario("check authLoginId passed");
		String testJson = "{\"gino\":\"pino\"}";
		String expectedJson = "{\"value\":\"1\"}";
		
		scenario.addScenario(loginAsUser());
		
		scenario.addScenarioStep("call non auth command", HttpScenarioFactory.postSuccessful(SystemACommands.CHECK_AUTH_LOGIN_ID_WITHOUT_AUTH_ANNOTATION,
				sTokenMap, null, testJson, matchesJSONString(expectedJson)));
		
		scenario.addScenarioStep("call  auth command", HttpScenarioFactory.postSuccessful(SystemACommands.CHECK_AUTH_LOGIN_ID_WITH_AUTH_ANNOTATION,
				sTokenMap, null, testJson, matchesJSONString(expectedJson)));
		
		return scenario;
		
	}
	
	

}
