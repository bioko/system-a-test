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

import static org.biokoframework.http.matcher.Matchers.matchesAuthenticationResponse;
import static org.biokoframework.http.matcher.Matchers.matchesSubjectAndContent;
import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;

import java.util.HashMap;
import java.util.Map;

import org.biokoframework.http.rest.exception.HttpError;
import org.biokoframework.http.scenario.ExecutionScenarioStep;
import org.biokoframework.http.scenario.HttpScenarioFactory;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.http.scenario.mail.MailScenarioStep;
import org.biokoframework.system.command.authentication.RequestPasswordResetCommand;
import org.biokoframework.system.entity.authentication.PasswordReset;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.service.currenttime.impl.TestCurrentTimeService;
import org.biokoframework.system.service.random.impl.TestRandomGeneratorService;
import org.biokoframework.systema.factory.SystemACommands;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.fields.Fields;
import org.json.simple.JSONValue;

public class PasswordResetStoriesFactory {

//	private static Map<String, String> _tokenMap;

	private static Map<String, String> _tokenMap = new HashMap<String, String>();

	public static Scenario userResetsItsPassword() throws Exception {
		final String resetToken = "1234-5678-90-abcde";
		
		Scenario scenario = new Scenario("User resets his password");
		
		scenario.addScenarioStep("Prepare time for password request reset", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestCurrentTimeService.setCalendar("2013-10-19T11:33:00+0100");
				TestRandomGeneratorService.setSingleRandomValue(RequestPasswordResetCommand.PASSWORD_RESET_TOKEN, resetToken);
			}
		});
				
		scenario.addScenario(AuthenticationStoriesFactory.registerTestUsers());
		
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadExample(LoginBuilder.GENERIC_USER_WITHOUT_ROLE).set(Login.PASSWORD, "aWrongPassword");
		String userEmail = loginBuilder.get(Login.USER_EMAIL);
		
		HttpError error = JSonExpectedResponseBuilder.invalidLogin();
					
		scenario.addScenarioStep("Login with wrong password", HttpScenarioFactory.postFailed(
				SystemACommands.ENGAGED_CHECK_IN, 
				null, 
				null, 
				loginBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD), 
				error.status(),
				matchesJSONString(JSONValue.toJSONString(error.body()))));
		
		Map<String, String> queryString = new HashMap<String, String>();
		queryString.put(Login.USER_EMAIL, userEmail);
		scenario.addScenarioStep("Request password reset", HttpScenarioFactory.getSuccessful(
				SystemACommands.REQUEST_PASSWORD_RESET,
				null,
				queryString,
				null,
				matchesJSONString("[]")));
		
		scenario.addScenarioStep("Receive mail with the new password ", new MailScenarioStep(userEmail, matchesSubjectAndContent("Password reset", 
				"Abbiamo ricevuto una richiesta di reset della password. "
				+ "Clicca <a href=\"http://local.engaged.it/password-reset&token=" + resetToken + "\">qui</a> per resettare la tua password")));
		
		String newPassword = "myNewPassword";
		loginBuilder.set(Login.PASSWORD, newPassword);

		scenario.addScenarioStep("Password reset will happen after 1 hour", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestCurrentTimeService.setCalendar("2013-10-19T12:33:00+0100");
			}
		});
		
		Fields passwordResetFields = new Fields();
		passwordResetFields.put(Login.PASSWORD, newPassword);
		passwordResetFields.put(PasswordReset.TOKEN, resetToken);
		scenario.addScenarioStep("Set the new password", HttpScenarioFactory.postSuccessful(
				SystemACommands.APPLY_PASSWORD_RESET,
				null,
				null,
				passwordResetFields.toJSONString(),
				matchesJSONString("[]")));
		
		scenario.addScenarioStep("Login with the new password", HttpScenarioFactory.postSuccessful(
				SystemACommands.ENGAGED_CHECK_IN, 
				null, 
				null, 
				loginBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD), 
				matchesAuthenticationResponse(_tokenMap)));
		
		return scenario;
	}
	
	public static Scenario userTriesToResetAnUnexistingLogin() throws Exception {
		String wrongUserEmail = "aWrongEmail@example.it";

		Scenario scenario = new Scenario("User tries to reset an unexisting login");
		
		scenario.addScenario(AuthenticationStoriesFactory.registerTestUsers());
		
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample().set(Login.USER_EMAIL, wrongUserEmail);
		
		HttpError error = JSonExpectedResponseBuilder.invalidLogin();
		
		scenario.addScenarioStep("Login with wrong password", HttpScenarioFactory.postFailed(
				SystemACommands.ENGAGED_CHECK_IN, 
				null, 
				null, 
				loginBuilder.getJsonForFields(Login.USER_EMAIL, Login.PASSWORD), 
				error.status(),
				matchesJSONString(JSONValue.toJSONString(error.body()))));
		
		error = JSonExpectedResponseBuilder.entityNotFound(Login.class, Login.USER_EMAIL, wrongUserEmail);
				
		Map<String, String> queryString = new HashMap<String, String>();
		queryString.put(Login.USER_EMAIL, wrongUserEmail);
		scenario.addScenarioStep("Request password reset", HttpScenarioFactory.getFailed(
				SystemACommands.REQUEST_PASSWORD_RESET,
				null,
				queryString,
				null,
				error.status(),
				matchesJSONString(JSONValue.toJSONString(error.body()))));
		
		return scenario;
	}
	
}
