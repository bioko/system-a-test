package it.bioko.systema.http.scenarios;

import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.OnlyGetScenarioFactory;
import it.bioko.http.scenario.Scenario;
import it.bioko.http.scenario.ScenarioRunner;
import it.bioko.http.scenario.parametrized.CrudScenariosParametrizedFactory;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy1.DummyEntity1Builder;
import it.bioko.systema.entity.dummy2.DummyEntity2;
import it.bioko.systema.entity.dummy2.DummyEntity2Builder;
import it.bioko.systema.entity.dummy3.DummyEntity3;
import it.bioko.systema.entity.dummy3.DummyEntity3Builder;
import it.bioko.systema.entity.dummyComplex.DummyComplexDomainEntity;
import it.bioko.systema.entity.dummyComplex.DummyComplexDomainEntityBuilder;
import it.bioko.systema.http.SystemATestAbstract;

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
