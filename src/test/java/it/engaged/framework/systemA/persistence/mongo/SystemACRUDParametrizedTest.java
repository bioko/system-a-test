package it.engaged.framework.systemA.persistence.mongo;

import it.bioko.http.AbstractSystemServletInterfaceTest;
import it.bioko.http.scenario.Scenario;
import it.bioko.http.scenario.ScenarioRunner;
import it.bioko.http.scenario.parametrized.CrudScenariosParametrizedFactory;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy1.DummyEntity1Builder;
import it.bioko.systema.injection.SystemAServletConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@Ignore
@RunWith(value = Parameterized.class)
public class SystemACRUDParametrizedTest extends AbstractSystemServletInterfaceTest {
		private String _requestUrl;
		private ScenarioRunner _scenarioRunner;

		public SystemACRUDParametrizedTest(String scenarioCollectorName, Scenario collector) {
			_scenarioRunner = new ScenarioRunner(collector);
		}
		
		@Parameters(name = "{index}-{0}")
		public static Collection<Object[]> scenarios() throws Exception {		
			List<Object[]> result = new ArrayList<Object[]>();
			
			List<Object[]> dummyEntity1 = Arrays.asList(
					CrudScenariosParametrizedFactory.createFrom(DummyEntity1.class, DummyEntity1Builder.class, dummyEntity1UpdateMap(), "1"));
			
			result.addAll(dummyEntity1);
			
			return result;
		}
		
		private static HashMap<String,String> dummyEntity1UpdateMap() {
			HashMap<String, String> result = new HashMap<String, String>();
			result.put(DummyEntity1.VALUE, "aValue UPDATED");
			return result; 
		}

		@Test
		public void test() throws Exception {
			_scenarioRunner.test(_requestUrl);
		}

		@Override
		protected SystemAServletConfig getServletConfig() {
			return new SystemAServletConfig();
		}
		
//		@Before
//		public void setup() throws Exception {
//			initWithRandomPort();
//			addEngagedServlet("SystemBServlet", "/api/" + SystemNames.SYSTEM_A_MONGO + "/*", testSystemBMongo());
//			start();
//			_requestUrl = "http://localhost:" + getPort() + "/engagedServer/api/" + SystemNames.SYSTEM_A_MONGO + "/";
//		}

//		@After
//		public void tearDown() throws Exception {
//			stop();
//			MongoQL mongoQL = new MongoQL("myDb", "127.0.0.1", 271017);
//			mongoQL.dropDB();
//		}
}
