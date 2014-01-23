package it.bioko.systema.http.transformtoscenario;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.systema.commons.SystemACommandNames;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy1.DummyEntity1Builder;
import it.bioko.systema.entity.dummy2.DummyEntity2;
import it.bioko.systema.entity.dummy2.DummyEntity2Builder;
import it.bioko.systema.http.SystemATestAbstract;
import it.bioko.utils.domain.EntityBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class EntityDissolverTest extends SystemATestAbstract{
	
	private String _dissolvedCommandUrl;

	@Before
	public void createDissolvedCommandUrl() {
		_dissolvedCommandUrl = getSystemAUrl() + SystemACommandNames.DISSOLVED_EXAMPLE;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void simpleTest() {
		EntityBuilder<DummyEntity1> dummy1Builder = new DummyEntity1Builder().loadDefaultExample();
		dummy1Builder.set(DummyEntity1.VALUE, "pino1");
		
		EntityBuilder<DummyEntity2> dummy2Builder = new DummyEntity2Builder().loadDefaultExample();
		dummy2Builder.set(DummyEntity2.VALUE, "gino2");

		JSONObject container = new JSONObject();
		DummyEntity2 dummy2 = dummy2Builder.build(false);
		dummy2.fields().remove(DummyEntity2.ENTITY1_ID);
		container.put("dummyEntity1", dummy1Builder.build(false));
		container.put("dummyEntity2", dummy2);
		
		JSONObject expected = new JSONObject();
		expected.put("dummyEntity1", dummy1Builder.build(true));
		expected.put("dummyEntity2", dummy2Builder.build(true));
		
		JSONArray expectedArray = new JSONArray();
		expectedArray.add(expected);
				
		expect().
		statusCode(200).
		body(
				equalTo(expectedArray.toJSONString())
		).
		when().
		given().
		body(
				container.toJSONString()
		).
		post(_dissolvedCommandUrl);
		
		System.out.println(container.toJSONString());
		
		expectedArray.clear();
		expectedArray.add(dummy1Builder.build(true));
		
		expect().
		body(
				equalTo(expectedArray.toJSONString())
		).
		when().
		given().
		get(getEntity1Url() + "1");
		
		expectedArray.clear();
		expectedArray.add(dummy2Builder.build(true));
		
		expect().
		body(
				equalTo(expectedArray.toJSONString())
		).
		when().
		given().
		get(getEntity2Url() + "1");
		
	}
	
}
