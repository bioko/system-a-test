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
