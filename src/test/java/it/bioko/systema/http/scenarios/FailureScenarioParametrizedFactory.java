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
import it.bioko.http.rest.exception.HttpError;
import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.http.scenario.Scenario;

import org.json.simple.JSONValue;

public class FailureScenarioParametrizedFactory {

	private static final String UNKNOWN = "unknown";

	public static Scenario createUnknownPost() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("POST_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown post");
		collector.addScenarioStep("Unknown post", HttpScenarioFactory.postFailed(UNKNOWN, null, null, "{ }", error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
	
	public static Scenario createUnknownPostWithId() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("POST_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown post with id");
		collector.addScenarioStep("Unknown post with id", HttpScenarioFactory.postFailed(UNKNOWN + "/1", null, null, "{ }", error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
	
	public static Scenario createUnknownGet() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("GET_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown get");
		collector.addScenarioStep("Unknown get", HttpScenarioFactory.getFailed(UNKNOWN, null, null, null, error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
	
	public static Scenario createUnknownGetWithId() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("GET_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown get with id");
		collector.addScenarioStep("Unknown get with id", HttpScenarioFactory.getFailed(UNKNOWN + "/1", null, null, null, error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
	
	public static Scenario createUnknownPut() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("PUT_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown put");
		collector.addScenarioStep("Unknown put", HttpScenarioFactory.putFailed(UNKNOWN, null, null, "{ }", error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
	
	public static Scenario createUnknownPutWithId() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("PUT_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown put with id");
		collector.addScenarioStep("Unknown put with id", HttpScenarioFactory.putFailed(UNKNOWN + "/1", null, null, "{ }", error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
	
	public static Scenario createUnknownDelete() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("DELETE_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown delete");
		collector.addScenarioStep("Unknown delete", HttpScenarioFactory.deleteFailed(UNKNOWN, null, null, null, error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
	
	public static Scenario createUnknownDeleteWithId() throws Exception {
		HttpError error = JSonExpectedResponseBuilder.commandNotFound("DELETE_" + UNKNOWN.toLowerCase());
		Scenario collector = new Scenario("Unknown delete with id");
		collector.addScenarioStep("Unknown delete with id", HttpScenarioFactory.deleteFailed(UNKNOWN + "/1", null, null, null, error.status(), equalTo(JSONValue.toJSONString(error.body()))));
		return collector;
	}
}
