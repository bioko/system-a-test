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

import static org.hamcrest.Matchers.equalTo;

import org.biokoframework.http.scenario.HttpScenarioFactory;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

public class FailureScenarioParametrizedFactory {

	private static final String UNKNOWN = "unknown";

	public static Scenario createUnknownPost() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
					ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
					ErrorEntity.ERROR_MESSAGE, "Route POST /unknown/ not found"
				));

		Scenario collector = new Scenario("Unknown post");
		collector.addScenarioStep("Unknown post", HttpScenarioFactory.postFailed(UNKNOWN, null, null, "{ }", 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
	
	public static Scenario createUnknownPostWithId() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, "Route POST /unknown/1/ not found"
			));
		
		Scenario collector = new Scenario("Unknown post with id");
		collector.addScenarioStep("Unknown post with id", HttpScenarioFactory.postFailed(UNKNOWN + "/1", null, null, "{ }", 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
	
	public static Scenario createUnknownGet() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, "Route GET /unknown/ not found"
			));
		
		Scenario collector = new Scenario("Unknown get");
		collector.addScenarioStep("Unknown get", HttpScenarioFactory.getFailed(UNKNOWN, null, null, null, 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
	
	public static Scenario createUnknownGetWithId() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, "Route GET /unknown/1/ not found"
			));
		
		Scenario collector = new Scenario("Unknown get with id");
		collector.addScenarioStep("Unknown get with id", HttpScenarioFactory.getFailed(UNKNOWN + "/1", null, null, null, 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
	
	public static Scenario createUnknownPut() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, "Route PUT /unknown/ not found"
			));
		
		Scenario collector = new Scenario("Unknown put");
		collector.addScenarioStep("Unknown put", HttpScenarioFactory.putFailed(UNKNOWN, null, null, "{ }", 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
	
	public static Scenario createUnknownPutWithId() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, "Route PUT /unknown/1/ not found"
			));
		
		Scenario collector = new Scenario("Unknown put with id");
		collector.addScenarioStep("Unknown put with id", HttpScenarioFactory.putFailed(UNKNOWN + "/1", null, null, "{ }", 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
	
	public static Scenario createUnknownDelete() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, "Route DELETE /unknown/ not found"
			));
		
		Scenario collector = new Scenario("Unknown delete");
		collector.addScenarioStep("Unknown delete", HttpScenarioFactory.deleteFailed(UNKNOWN, null, null, null, 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
	
	public static Scenario createUnknownDeleteWithId() throws Exception {
		ErrorEntity error = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, "Route DELETE /unknown/1/ not found"
			));
		
		Scenario collector = new Scenario("Unknown delete with id");
		collector.addScenarioStep("Unknown delete with id", HttpScenarioFactory.deleteFailed(UNKNOWN + "/1", null, null, null, 404, equalTo("[" + error.toJSONString() + "]")));
		return collector;
	}
}
