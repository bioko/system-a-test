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
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.http.scenario.ScenarioRunner;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy1.DummyEntity1Builder;
import org.biokoframework.systema.entity.dummyComplex.DummyComplexDomainEntity;
import org.biokoframework.systema.entity.dummyComplex.DummyComplexDomainEntityBuilder;
import org.biokoframework.systema.http.SystemATestAbstract;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;

import static org.biokoframework.http.scenario.parametrized.CrudScenariosParametrizedFactory.createFrom;

@RunWith( value = Parameterized.class )
public class ParametrizedTest extends SystemATestAbstract {

	private final ScenarioRunner fScenarioRunner;

	public ParametrizedTest(Scenario scenario) {
		fScenarioRunner = new ScenarioRunner(scenario);
	}
	
	@Parameters(name = "{index}- {0}")
	public static Collection<Object[]> scenarios() throws Exception {
		
		List<Scenario> result = new ArrayList<>();

        result.addAll(createFrom(DummyEntity1.class, DummyEntity1Builder.class, dummyEntityUpdateMap(), "1"));
//        result.addAll(createFrom(DummyEntity2.class, DummyEntity2Builder.class, dummyEntity2UpdateMap(), "1"));
//        result.addAll(createFrom(DummyEntity3.class, DummyEntity3Builder.class, dummyEntityUpdateMap(), "1"));

        result.addAll(createFrom(DummyComplexDomainEntity.class, DummyComplexDomainEntityBuilder.class, dummyComplexEntityUpdateMap(), "1",
                new String[]{DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET}));

		result.addAll(HttpScenarioFactory.findScenarios(
                FailureScenarioParametrizedFactory.class,
//				DissolverFactory.class,
//				MultipleCommandFactory.class,

                CodeExecutionScenarioStepFactory.class,

				CronFactory.class,
//				ValidatorFactory.class,
				PasswordResetStoriesFactory.class,
				EmailConfirmationStories.class
		));

//		result.addAll(OnlyGetScenarioFactory.adaptToOnlyGet(result));

		result.addAll(HttpScenarioFactory.findScenarios(
				CommandInfoFactory.class,
//				MultipartHttpScenarioFactory.class,

    			AuthenticationStoriesFactory.class
		));
		
		return toObjectArrayThingy(result);
	
	}

	private static Map<String, Object> dummyComplexEntityUpdateMap() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(DummyComplexDomainEntity.A_STRING_MANDATORY_FIELD, "a modified string");		
		return result;
	}

	private static HashMap<String, Object> dummyEntityUpdateMap() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(DummyEntity1.VALUE, "aValue UPDATED");
		return result; 
	}
	
	private static HashMap<String, Object> dummyEntity2UpdateMap() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(DummyEntity1.VALUE, 2);
		return result; 
	}
	
	@Test
	public void test() throws Exception {
		fScenarioRunner.test(getLocalHostUrl());
	}
	
}
