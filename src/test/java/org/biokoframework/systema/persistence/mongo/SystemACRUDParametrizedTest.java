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

package org.biokoframework.systema.persistence.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.biokoframework.http.AbstractSystemServletInterfaceTest;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.http.scenario.ScenarioRunner;
import org.biokoframework.http.scenario.parametrized.CrudScenariosParametrizedFactory;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy1.DummyEntity1Builder;
import org.biokoframework.systema.injection.SystemAServletConfig;
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
		
		private static HashMap<String, Object> dummyEntity1UpdateMap() {
			HashMap<String, Object> result = new HashMap<String, Object>();
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
