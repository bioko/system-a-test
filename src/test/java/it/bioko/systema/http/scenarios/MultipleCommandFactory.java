package it.bioko.systema.http.scenarios;


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
