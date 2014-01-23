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

import org.biokoframework.http.scenario.HttpScenarioFactory;
import org.biokoframework.http.scenario.OnlyGetScenarioFactory;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.http.scenario.ScenarioRunner;
import org.biokoframework.http.scenario.parametrized.CrudScenariosParametrizedFactory;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy1.DummyEntity1Builder;
import org.biokoframework.systema.entity.dummy2.DummyEntity2;
import org.biokoframework.systema.entity.dummy2.DummyEntity2Builder;
import org.biokoframework.systema.entity.dummy3.DummyEntity3;
import org.biokoframework.systema.entity.dummy3.DummyEntity3Builder;
import org.biokoframework.systema.entity.dummyComplex.DummyComplexDomainEntity;
import org.biokoframework.systema.entity.dummyComplex.DummyComplexDomainEntityBuilder;
import org.biokoframework.systema.http.SystemATestAbstract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith( value = Parameterized.class )
public class ParametrizedTests extends SystemATestAbstract {

	private ScenarioRunner _scenarioRunner;

	public ParametrizedTests(String scenarioCollectorName, Scenario collector) {
		_scenarioRunner = new ScenarioRunner(collector);
	}
	
	@Parameters(name = "{index}- {0}")
	public static Collection<Object[]> scenarios() throws Exception {
		
		List<Object[]> result = new ArrayList<Object[]>();

		result.addAll(Arrays.asList(CrudScenariosParametrizedFactory.createFrom(DummyEntity1.class, DummyEntity1Builder.class, dummyEntityUpdateMap(), "1")));
		result.addAll(Arrays.asList(CrudScenariosParametrizedFactory.createFrom(DummyEntity2.class, DummyEntity2Builder.class, dummyEntityUpdateMap(), "1")));
		result.addAll(Arrays.asList(CrudScenariosParametrizedFactory.createFrom(DummyEntity3.class, DummyEntity3Builder.class, dummyEntityUpdateMap(), "1")));
		
		result.addAll(Arrays.asList(CrudScenariosParametrizedFactory.createFrom(DummyComplexDomainEntity.class,  DummyComplexDomainEntityBuilder.class, dummyComplexEntityUpdateMap(), "1",
				new String[] {DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET})));
		
		result.addAll(HttpScenarioFactory.findScenarios(
				FailureScenarioParametrizedFactory.class,
				DissolverFactory.class,
				MultipleCommandFactory.class,
				CodeExecutionScenarioStepFactory.class,
				CronFactory.class,
				ValidatorFactory.class,
				PasswordResetStoriesFactory.class,
				EmailConfirmationStories.class
		));

		result.addAll(OnlyGetScenarioFactory.adaptToOnlyGet(result));

		result.addAll(HttpScenarioFactory.findScenarios(
				CommandInfoFactory.class, 
				MultipartHttpScenarioFactory.class,
				AuthenticationStoriesFactory.class
		));
		
		return result;
	
	}
	
	private static Map<String, String> dummyComplexEntityUpdateMap() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put(DummyComplexDomainEntity.A_STRING_MANDATORY_FIELD, "a modified string");		
		return result;
	}

	private static HashMap<String,String> dummyEntityUpdateMap() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put(DummyEntity1.VALUE, "aValue UPDATED");
		return result; 
	}
	
	@Test
	public void test() throws Exception {
		_scenarioRunner.test(getSystemAUrl());
	}
	
}