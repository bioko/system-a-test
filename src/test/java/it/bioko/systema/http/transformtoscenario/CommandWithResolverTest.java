package it.bioko.systema.http.transformtoscenario;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy1.DummyEntity1Builder;
import it.bioko.systema.entity.dummy2.DummyEntity2;
import it.bioko.systema.entity.dummy2.DummyEntity2Builder;
import it.bioko.systema.entity.dummy3.DummyEntity3;
import it.bioko.systema.entity.dummy3.DummyEntity3Builder;
import it.bioko.systema.http.SystemATestAbstract;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.EntityBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class CommandWithResolverTest extends SystemATestAbstract {

	private String _resolvedCommandUrl;

	@Before
	public void populateRepositories() {
		_resolvedCommandUrl = getSystemAUrl() + "dummy-entity3-resolved/";
		
		EntityBuilder<DummyEntity1> builder1 = new DummyEntity1Builder().loadDefaultExample();
		doPost(getEntity1Url(), builder1.build(false).toJSONString());
		
		EntityBuilder<DummyEntity2> builder2 = new DummyEntity2Builder().loadDefaultExample();
		doPost(getEntity2Url(), builder2.build(false).toJSONString());
		
		EntityBuilder<DummyEntity3> builder3 = new DummyEntity3Builder().loadDefaultExample();
		doPost(getEntity3Url(), builder3.build(false).toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void simpleResolvedTest() {
		
		JSONObject resolved1 = new JSONObject();
		resolved1.put("id", "1");
		resolved1.put("value", "pino1");
		
		JSONObject resolved2 = new JSONObject();
		resolved2.put("id", "1");
		resolved2.put("value", "gino2");
		resolved2.put("dummyEntity1Id", resolved1);
		
		JSONArray resolvedArray = new JSONArray();
		JSONObject resolved3 = createUnresolvedEntity3();
		resolved3.put("dummyEntity2Id", resolved2);
		resolvedArray.add(resolved3);
		
		expect().
		statusCode(200).
		body(
				equalTo(resolvedArray.toJSONString())
		).
		when().
		given().
		parameter("resolveEntities", "true").
		get(_resolvedCommandUrl);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void notResolvedBecauseNotRequired() {
		JSONArray unresolvedArray = new JSONArray();
		unresolvedArray.add(createUnresolvedEntity3());
		
		expect().
		statusCode(200).
		body(
				equalTo(unresolvedArray.toJSONString())
		).
		when().
		given().
		get(_resolvedCommandUrl);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void notResolvedBecauseRequiredSetToFalse() {
		JSONArray unresolvedArray = new JSONArray();
		unresolvedArray.add(createUnresolvedEntity3());
		
		expect().
		statusCode(200).
		body(
				equalTo(unresolvedArray.toJSONString())
		).
		when().
		given().
		parameter("resolveEntities", "false").
		get(_resolvedCommandUrl);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createUnresolvedEntity3() {
		JSONObject unresolvedEntity3 = new JSONObject();
		unresolvedEntity3.put(DomainEntity.ID, "1");
		unresolvedEntity3.put(DummyEntity3.VALUE, "tino3");
		unresolvedEntity3.put(DummyEntity3.ENTITY2_ID, "1");
		return unresolvedEntity3;
	}
	
	private void doPost(String url, String body) {
		given().
		body(
				body
		).
		post(url);
	}
	
}
