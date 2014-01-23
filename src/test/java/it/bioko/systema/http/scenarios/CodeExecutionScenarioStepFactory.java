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