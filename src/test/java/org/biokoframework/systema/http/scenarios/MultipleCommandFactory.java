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


public class MultipleCommandFactory {
	
// Commented because unique checker is deprecated with annotated commands	

//	public static Scenario createMultipleCommandPostDummy1IfNotExistingWithValueSucceds() throws Exception {
//		Scenario collector = new Scenario("With multiple command check if dummy 2 with value exists, if not post - succedes");
//		
//		EntityBuilder<DummyEntity2> dummy2Builder = new DummyEntity2Builder().loadDefaultExample();
//		collector.addScenarioStep("Post a dummy entity2", HttpScenarioFactory.postSuccessful(
//				"dummy-entity2",
//				null, 
//				null, 
//				dummy2Builder.build(false).toJSONString(), 
//				equalTo(JSonExpectedResponseBuilder.asArray(dummy2Builder.build(true).toJSONString()))));
//		
//		dummy2Builder.set(DummyEntity2.VALUE, "multiple-dummy").set(DummyEntity2.ENTITY1_ID, "3").setId("2");
//		
//		collector.addScenarioStep("Multiple command use", HttpScenarioFactory.postSuccessful(
//				SystemACommandNames.MULTIPLE_EXAMPLE,
//				null,
//				null, 
//				dummy2Builder.build(false).toJSONString(),
//				equalTo(JSonExpectedResponseBuilder.asArray(dummy2Builder.build(true).toJSONString()))));
//		
//		collector.addScenarioStep("Get the dummy2 saved using the multiple command", HttpScenarioFactory.getSuccessful(
//				"dummy-entity2/2",
//				null,
//				null, 
//				null,
//				equalTo(JSonExpectedResponseBuilder.asArray(dummy2Builder.build(true).toJSONString()))));
//		
//		return collector;
//	}
//	
//	public static Scenario createMultipleCommandPostDummy1IfNotExistingWithValueFails() throws Exception {
//		Scenario collector = new Scenario("With multiple command check if dummy 2 with value exists, if not post - fails");
//		
//		EntityBuilder<DummyEntity2> dummy2Builder = new DummyEntity2Builder().loadDefaultExample();
//		collector.addScenarioStep("Post a dummy entity2", HttpScenarioFactory.postSuccessful(
//				"dummy-entity2",
//				null, 
//				null, 
//				dummy2Builder.build(false).toJSONString(), 
//				equalTo(JSonExpectedResponseBuilder.asArray(dummy2Builder.build(true).toJSONString()))));
//		
//		DummyEntity2Builder anOtherDummy2Builder = new DummyEntity2Builder(); 
//		anOtherDummy2Builder.set(DummyEntity2.VALUE, "multiple-dummy").set(DummyEntity2.ENTITY1_ID, "3");
//		
//		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
//		HttpError error = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createAlreadyExistingEntity("DummyEntity2"));
//		collector.addScenarioStep("Multiple command use, it should fail",  HttpScenarioFactory.postFailed(
//				SystemACommandNames.MULTIPLE_EXAMPLE,
//				null,
//				null, 
//				dummy2Builder.build(false).toJSONString(),
//				error.status(),
//				equalTo(JSONValue.toJSONString(error.body()))));
//		
//		error = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound("DummyEntity2","2"));
//		collector.addScenarioStep("Get the dummy2 saved using the multiple command, it should fail", HttpScenarioFactory.getFailed(
//				"dummy-entity2/2",
//				null,
//				null, 
//				null,
//				error.status(),
//				equalTo(JSONValue.toJSONString(error.body()))));
//		
//		return collector;
//	}
	
}
