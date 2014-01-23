package it.bioko.systema.http.scenarios;

import static it.bioko.http.matcher.Matchers.matchesAuthenticationResponse;
import static it.bioko.http.matcher.Matchers.matchesSubjectAndContent;
import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import it.bioko.http.rest.exception.HttpError;
import it.bioko.http.scenario.ExecutionScenarioStep;
import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.http.scenario.Scenario;
import it.bioko.http.scenario.mail.MailScenarioStep;
import it.bioko.system.command.authentication.RequestPasswordResetCommand;
import it.bioko.system.entity.authentication.PasswordReset;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.system.service.currenttime.impl.TestCurrentTimeService;
import it.bioko.system.service.random.impl.TestRandomGeneratorService;
import it.bioko.systema.factory.SystemACommands;
import it.bioko.utils.domain.EntityBuilder;
import it.bioko.utils.fields.Fields;

import java.util.HashMap;
import java.util.Map;

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
		
		Fields passwordResetFields = Fields.empty();
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
