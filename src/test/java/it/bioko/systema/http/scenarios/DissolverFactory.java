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

import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.http.rest.exception.HttpError;
import it.bioko.http.rest.exception.HttpResponseBuilder;
import it.bioko.http.rest.exception.HttpResponseExceptionFactory;
import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.http.scenario.Scenario;
import it.bioko.system.entity.EntityClassNameTranslator;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.systema.commons.SystemACommandNames;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy1.DummyEntity1Builder;
import it.bioko.systema.entity.dummy2.DummyEntity2;
import it.bioko.systema.entity.dummy2.DummyEntity2Builder;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.EntityBuilder;

import java.util.Arrays;

import org.hamcrest.Matcher;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class DissolverFactory {

	public static Scenario createPostDummy1AndDummy2GetDummy1GetDummy2() throws Exception {
		Scenario collector = new Scenario("Post dummy1 and 2, get 1, get 2");
		
		DummyEntity1Builder entity1Builder = new DummyEntity1Builder();
		entity1Builder.loadDefaultExample().setId("1");
		
		DummyEntity2Builder entity2Builder = new DummyEntity2Builder();
		entity2Builder.loadDefaultExample().setId("1");
		
		collector.addScenarioStep("Post Dummy1 and Dummy2", HttpScenarioFactory.postSuccessful(
				SystemACommandNames.DISSOLVED_EXAMPLE, 
				null, 
				null, 
				dissolvableBodyRequest(entity1Builder, entity2Builder).toJSONString(), 
				dissolvedExpectedResponse(entity1Builder, entity2Builder)));
		
		collector.addScenarioStep("Get Dummy1", HttpScenarioFactory.getSuccessful(
				EntityClassNameTranslator.toHyphened(DummyEntity1.class.getSimpleName()) + "/",
				null, 
				null, 
				null, 
				equalTo(JSonExpectedResponseBuilder.asArray(entity1Builder.build(true).toJSONString()))));
		
		collector.addScenarioStep("Get Dummy2", HttpScenarioFactory.getSuccessful(
				EntityClassNameTranslator.toHyphened(DummyEntity2.class.getSimpleName()) + "/",
				null, 
				null, 
				null, 
				equalTo(JSonExpectedResponseBuilder.asArray(entity2Builder.build(true).toJSONString()))));
		
		return collector;
	}
	
	public static Scenario createPostDummy2AndFailBecauseNoDummy1() throws Exception {
		Scenario collector = new Scenario("Post dummy2 and fail because no dummy1");
		
		DummyEntity1Builder entity1Builder = new DummyEntity1Builder();
		entity1Builder.loadDefaultExample().setId("1");
		
		DummyEntity2Builder entity2Builder = new DummyEntity2Builder();
		entity2Builder.loadDefaultExample().setId("1");
		
		JSONObject request = dissolvableBodyRequest(entity1Builder, entity2Builder);
		request.remove(EntityClassNameTranslator.toFieldName(DummyEntity1.class.getSimpleName()));
		
		HttpResponseBuilder builder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError error = builder.buildFrom(CommandExceptionsFactory.createDissolutionIncompleteException(
				Arrays.asList("dummyEntity2")));
		
		collector.addScenarioStep("Post Dummy2 and fail", HttpScenarioFactory.postFailed(
				SystemACommandNames.DISSOLVED_EXAMPLE, 
				null, 
				null, 
				request.toJSONString(), 
				error.status(), matchesJSONString(JSONValue.toJSONString(error.body()))));
		
		return collector;
	}

	@SuppressWarnings("unchecked")
	private static Matcher<String> dissolvedExpectedResponse(EntityBuilder<?>... builders) {
		JSONObject expected = new JSONObject(); 
		for (EntityBuilder<?> aBuilder : builders) {
			DomainEntity entity = aBuilder.build(true);
			String entityName = entity.getClass().getSimpleName();
			entityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
			expected.put(entityName, entity);
		}
		return equalTo(JSonExpectedResponseBuilder.asArray(expected.toJSONString()));
	}

	@SuppressWarnings("unchecked")
	private static JSONObject dissolvableBodyRequest(EntityBuilder<?>... builders) {
		JSONObject container = new JSONObject();
		
		for (EntityBuilder<?> aBuilder : builders) {
			DomainEntity entity = aBuilder.build(false);
			JSONObject dissolvableEntity = new JSONObject();
			for (String aKey : entity.fields().keys()) {
				if (!aKey.matches(".*Id$")) {
					dissolvableEntity.put(aKey, entity.get(aKey));
				}
			}
			String entityName = entity.getClass().getSimpleName();
			entityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
			container.put(entityName, dissolvableEntity);
		}
		
		return container;
	}
	
}
