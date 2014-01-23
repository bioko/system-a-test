package it.bioko.systema.http.scenarios;

import static it.bioko.http.matcher.Matchers.matchesSubjectAndContent;
import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import it.bioko.http.scenario.ExecutionScenarioStep;
import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.http.scenario.Scenario;
import it.bioko.http.scenario.mail.MailScenarioStep;
import it.bioko.system.entity.authentication.EmailConfirmation;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.system.service.currenttime.impl.TestCurrentTimeService;
import it.bioko.system.service.random.impl.TestRandomGeneratorService;
import it.bioko.systema.command.RequestEmailConfirmationCommand;
import it.bioko.systema.factory.SystemACommands;
import it.bioko.utils.domain.EntityBuilder;
import it.bioko.utils.fields.FieldValues;
import it.bioko.utils.fields.Fields;

import java.util.HashMap;
import java.util.Map;

public class EmailConfirmationStories {
	
	public static Scenario simpleEmailConfirmation() throws Exception {
		Scenario scenario = new Scenario("Simple email confirmation"); 
		
		final String token = "1234567890"; 
		
		scenario.addScenarioStep("Prepare time for password request reset", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				TestRandomGeneratorService.setSingleRandomValue(RequestEmailConfirmationCommand.EMAIL_CONFIRMATION_TOKEN, token);
				TestCurrentTimeService.setCalendar("2013-12-03T12:30:00+0100");
			}
		});
		
		EntityBuilder<Login> loginBuilder = new LoginBuilder().loadDefaultExample();
		String loginUserEmail = loginBuilder.get(Login.USER_EMAIL);
		
		scenario.addScenarioStep("Register user login", HttpScenarioFactory.postSuccessful(
				SystemACommands.LOGIN, 
				null, 
				null, 
				loginBuilder.build(false).toJSONString(), 
				matchesJSONString(JSonExpectedResponseBuilder.asArray(loginBuilder.build(true).toJSONString()))));
		
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put(Login.USER_EMAIL, loginUserEmail);
		scenario.addScenarioStep("Request email confirmation", HttpScenarioFactory.getSuccessful(
				SystemACommands.CONFIRMATION_EMAIL_REQUEST, 
				null, 
				queryMap, 
				null, 
				matchesJSONString("[ ]")));
		
		scenario.addScenarioStep("Receive the email that can be used to confirm email", new MailScenarioStep(
				loginUserEmail, matchesSubjectAndContent(
						"Email confirmation", 
						"<html>\n<body>\n"
						+ "Clicca sul link riportato sotto per confermare la tua mail\n"
						+ "<a href=\"http://www.example.net/confirm-email?token=" + token + "&userEmail=" + loginUserEmail + ">"
								+ "Conferma email</a>\n"
						+ "<body>\n</html>"))); 
		
		Fields fields = Fields.empty();
		fields.put(Login.USER_EMAIL, loginUserEmail);
		fields.put(EmailConfirmation.TOKEN, token);
		scenario.addScenarioStep("From the link contained in the mail confirm the address", HttpScenarioFactory.postSuccessful(
				SystemACommands.CONFIRMATION_EMAIL_RESPONSE, 
				null, 
				queryMap, 
				fields.toJSONString(), 
				matchesJSONString("[ ]")));
		
		EmailConfirmation confirmation = new EmailConfirmation(Fields.empty());
		confirmation.setId("1");
		confirmation.set(EmailConfirmation.LOGIN_ID, "1");
		confirmation.set(EmailConfirmation.CONFIRMED, FieldValues.TRUE);
		confirmation.set(EmailConfirmation.TOKEN, token);
		confirmation.set(EmailConfirmation.CONFIRMATION_TIMESTAMP, "2013-12-03T12:30:00+0100");
		scenario.addScenarioStep("The email is confirmed in the entity", HttpScenarioFactory.getSuccessful(
				SystemACommands.EMAIL_CONFIRMATION_TEST + "/1", 
				null, 
				null, 
				null, 
				matchesJSONString(JSonExpectedResponseBuilder.asArray(confirmation.toJSONString()))));
		
		return scenario;
	}

}
