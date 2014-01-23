package it.bioko.systema.http.scenarios;

import static it.bioko.http.matcher.Matchers.matchesSubjectAndContent;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import it.bioko.http.scenario.ExecutionScenarioStep;
import it.bioko.http.scenario.Scenario;
import it.bioko.http.scenario.mail.MailScenarioStep;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.systema.commons.SystemACommandNames;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.misc.Dummy1Mock;

public class CronFactory {
	
	public static Scenario createSimpleCron() throws Exception {
		
		Scenario scenario = new Scenario("Simple cron use");

		scenario.addScenarioStep("Set the value of the mock service to triangolo", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				Dummy1Mock.setShape(Dummy1Mock.TRIANGOLO);
			}
		});
		
		scenario.addScenarioStep("Wait for cron to trigger, and set mock to quadrato", new ExecutionScenarioStep() {
			
			@Override
			public void execute() {
				try {
					Thread.sleep(20000);
					// In the mean time CRON will invoke CrudExampleCommand
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		});
		
		scenario.addScenarioStep("Check that cron actually worked, mock is set to quadrato", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				assertThat(Dummy1Mock.getShape().get(DummyEntity1.VALUE), is(equalTo(Dummy1Mock.QUADRATO)));
			}
		});
		
		return scenario;
		
	}
	
	public static Scenario createFailureCron() throws Exception {
		
		Scenario scenario = new Scenario("Simple cron failure");
		
		scenario.addScenarioStep("Wait for cron to trigger, and run the failing command", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				try {
					Thread.sleep(20000);
					// In the mean time CRON will invoke CrudFailingCommand
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		});
		
		scenario.addScenarioStep("Cron should notify the failure", new MailScenarioStep(
				GenericFieldValues.CRON_EMAIL, 
				matchesSubjectAndContent(
						equalTo("Cron task failure"),
						startsWith("Command " + SystemACommandNames.CRON_FAILING_EXAMPLE + " failed"))));
		
		return scenario;
	}

}
