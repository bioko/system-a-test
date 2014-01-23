package it.bioko.systema.http.scenarios;

import static org.hamcrest.Matchers.equalTo;
import it.bioko.http.scenario.ExecutionScenarioStep;
import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.Scenario;
import it.bioko.systema.commons.SystemACommandNames;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.misc.Dummy1Mock;
import it.bioko.utils.fields.Fields;

public class CodeExecutionScenarioStepFactory {

	static DummyEntity1 quadrato = new DummyEntity1(Fields.single(DummyEntity1.VALUE, "quadrato"));
	static DummyEntity1 triangolo = new DummyEntity1(Fields.single(DummyEntity1.VALUE, "triangolo"));
	
	static {		
		quadrato.setId("1");
		triangolo.setId("2");
	}

	public static Scenario testWithoutExecutionStep() throws Exception {
		
		
		Scenario scenario = new Scenario("Test execution without executionStep");
		
		// I want a triangolo
		Dummy1Mock.setShape(Dummy1Mock.TRIANGOLO);
		// but I will get a quadrato because of the second static setShape below.
		scenario.addScenarioStep("getting wrong quadrato", HttpScenarioFactory.getSuccessful(
						SystemACommandNames.DUMMY1_MOCK_COMMAND, 
						null, null, null, equalTo("["+quadrato.toJSONString()+"]")));

		
		Dummy1Mock.setShape(Dummy1Mock.QUADRATO);
		
		scenario.addScenarioStep("use mock dependent command to get quadrato", HttpScenarioFactory.getSuccessful(
				SystemACommandNames.DUMMY1_MOCK_COMMAND, 
				null, null, null, equalTo("["+quadrato.toJSONString()+"]")));
		
		return scenario;
	}
	
	
	
	public static Scenario testWithExecutionStep() throws Exception {
		
		Scenario scenario = new Scenario("Test execution without executionStep");
		
		// I want a triangolo, so I will use ExecutionStep to execute code NOW
		scenario.addScenarioStep("setting mock to tirangolo", new ExecutionScenarioStep() {
			
			@Override
			public void execute() {
				Dummy1Mock.setShape(Dummy1Mock.TRIANGOLO);				
			}
		});
		
		
		// now I will get a correct triangolo
		scenario.addScenarioStep("getting correct triangolo", HttpScenarioFactory.getSuccessful(
						SystemACommandNames.DUMMY1_MOCK_COMMAND, 
						null, null, null, equalTo("["+triangolo.toJSONString()+"]")));
 

		// Now I set a quadrato for the second test
		scenario.addScenarioStep("setting mock to quadrato", new ExecutionScenarioStep() {

			@Override
			public void execute() {
				Dummy1Mock.setShape(Dummy1Mock.QUADRATO);				
			}
		});

		scenario.addScenarioStep("use mock dependent command to get quadrato", HttpScenarioFactory.getSuccessful(
				SystemACommandNames.DUMMY1_MOCK_COMMAND, 
				null, null, null, equalTo("["+quadrato.toJSONString()+"]")));
		
		return scenario;
	}

}