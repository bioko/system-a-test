package it.bioko.systema;

import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.bioko.system.command.ValidationException;
import it.bioko.system.entity.resolution.AnnotatedEntityResolver;
import it.bioko.system.entity.resolution.EntityResolver;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.RepositoryException;
import it.bioko.system.repository.memory.InMemoryRepository;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy1.DummyEntity1Builder;
import it.bioko.systema.entity.dummy2.DummyEntity2;
import it.bioko.systema.entity.dummy2.DummyEntity2Builder;
import it.bioko.systema.entity.dummy3.DummyEntity3;
import it.bioko.systema.entity.dummy3.DummyEntity3Builder;
import it.bioko.systema.entity.dummy4.DummyEntity4;
import it.bioko.systema.entity.dummy5.DummyEntity5;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.EntityBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EntityResolverTest {

	private InMemoryRepository<DummyEntity1> _repository1;
	private InMemoryRepository<DummyEntity2> _repository2;
	private Repository<DummyEntity3> _repository3;
	private Repository<DummyEntity4> _repository4;
	private Repository<DummyEntity5> _repository5;
	
	@Before
	public void populateRepositories() throws ValidationException, RepositoryException {
		
		_repository1 = new InMemoryRepository<DummyEntity1>(DummyEntity1.class);
		_repository2 = new InMemoryRepository<DummyEntity2>(DummyEntity2.class);
		_repository3 = new InMemoryRepository<DummyEntity3>(DummyEntity3.class);
		_repository4 = new InMemoryRepository<DummyEntity4>(DummyEntity4.class);
		_repository5 = new InMemoryRepository<DummyEntity5>(DummyEntity5.class);
		
		
		
		EntityBuilder<DummyEntity1> builder1 = new DummyEntity1Builder().loadDefaultExample();
		_repository1.save(builder1.build(false));
		
		EntityBuilder<DummyEntity2> builder2 = new DummyEntity2Builder().loadDefaultExample();
		_repository2.save(builder2.build(false));
		
		EntityBuilder<DummyEntity3> builder3 = new DummyEntity3Builder().loadDefaultExample();
		_repository3.save(builder3.build(false));
		

//	ARRAYS now are not supported from the sqlrepo
//		DummyEntity4Builder builder4 = new DummyEntity4Builder();
//		builder4.loadDefaultExample();
//		builder1.set(DummyEntity1.VALUE, "pino1_bis");
//		_repository1.call(builder1.build(false), CrudMethod.POST.value());
//		List<DummyEntity1> list = _repository1.getAll();
//		ArrayList<String> references = new ArrayList<String>();
//		for (DummyEntity1 aDummy : list) {
//			references.add(aDummy.getId());
//		}
//		builder4.setEntity1(references);
//		_repository4.call(builder4.build(false), CrudMethod.POST.value());
//		
//		DummyEntity5Builder builder5 = new DummyEntity5Builder();
//		builder5.loadDefaultExample();
//		ArrayList<DummyEntity4> entityList = new ArrayList<DummyEntity4>();
//		entityList.addAll(_repository4.getAll());
//		builder5.setList(entityList);
//		_repository5.call(builder5.build(false), CrudMethod.POST.value());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void simpleResolutionTest() throws Exception {
		DummyEntity3 entity3 = _repository3.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity3 solved3 = resolver.with(_repository1, DummyEntity1.class)
									   .with(_repository2, DummyEntity2.class)
									   .solve(entity3, DummyEntity3.class);
		
		JSONObject expectedSolved2 = new JSONObject();
		expectedSolved2.put("id", "1");
		expectedSolved2.put("dummyEntity1Id", new DummyEntity1Builder().loadDefaultExample().build(true));
		expectedSolved2.put("value", "gino2");
		
		JSONObject expectedSolved3 = new JSONObject();
		expectedSolved3.put("dummyEntity2Id", expectedSolved2);
		expectedSolved3.put("id", "1");
		expectedSolved3.put("value", "tino3");
		
		assertThat(solved3.toJSONString(), equalTo(expectedSolved3.toJSONString()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void missingRepository() throws Exception {
		DummyEntity3 entity3 = _repository3.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity3 solved3 = resolver.with(_repository2, DummyEntity2.class)
									   .solve(entity3, DummyEntity3.class);
		
		JSONObject expectedSolved2 = new JSONObject();
		expectedSolved2.put("id", "1");
		expectedSolved2.put("dummyEntity1Id", "1");
		expectedSolved2.put("value", "gino2");
		
		JSONObject expectedSolved3 = new JSONObject();
		expectedSolved3.put("dummyEntity2Id", expectedSolved2);
		expectedSolved3.put("id", "1");
		expectedSolved3.put("value", "tino3");
		
		assertThat(solved3.toJSONString(), equalTo(expectedSolved3.toJSONString()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void depthLimitedResolution() throws Exception {		
		DummyEntity3 entity3 = _repository3.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity3 solved3 = resolver.with(_repository2, DummyEntity2.class)
									   .with(_repository1, DummyEntity1.class)
									   .maxDepth(1)
									   .solve(entity3, DummyEntity3.class);
		
		JSONObject expectedSolved2 = new JSONObject();
		expectedSolved2.put("id", "1");
		expectedSolved2.put("dummyEntity1Id", "1");
		expectedSolved2.put("value", "gino2");
		
		JSONObject expectedSolved3 = new JSONObject();
		expectedSolved3.put("dummyEntity2Id", expectedSolved2);
		expectedSolved3.put("id", "1");
		expectedSolved3.put("value", "tino3");
		
		assertThat(solved3.toJSONString(), equalTo(expectedSolved3.toJSONString()));
	}
	
	@SuppressWarnings("unchecked")
	@Ignore("Arrays are not supported")
	@Test
	public void resolutionWithList() throws Exception {
		DummyEntity4 entity4 = _repository4.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity4 solved4 = resolver.with(_repository1, DummyEntity1.class)
										.solve(entity4, DummyEntity4.class);
		
		JSONObject expectedSolved1a = new JSONObject();
		expectedSolved1a.put(DomainEntity.ID, "1");
		expectedSolved1a.put(DummyEntity1.VALUE, "pino1");

		JSONObject expectedSolved1b = new JSONObject();
		expectedSolved1b.put(DomainEntity.ID, "2");
		expectedSolved1b.put(DummyEntity1.VALUE, "pino1_bis");
		
		JSONArray resolvedList = new JSONArray();
		resolvedList.add(expectedSolved1a);
		resolvedList.add(expectedSolved1b);

		JSONObject expectedSolved = new JSONObject();
		expectedSolved.put("id", "1");
		expectedSolved.put("dummyEntity1Id", resolvedList);
		expectedSolved.put("value", "lino4");
		
		assertThat(solved4.toJSONString(), equalTo(expectedSolved.toJSONString()));
	}
	
	@SuppressWarnings("unchecked")
	@Ignore("Arrays are not supported")
	@Test
	public void resolutionWithListOfEntitiesContainingListOfIds() throws Exception {
		DummyEntity5 entity5 = _repository5.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity5 solved5 = resolver.with(_repository1, DummyEntity1.class)
										 .solve(entity5, DummyEntity5.class);
		
		JSONObject expectedSolved1a = new JSONObject();
		expectedSolved1a.put("id", "1");
		expectedSolved1a.put(DummyEntity1.VALUE, "pino1");

		JSONObject expectedSolved1b = new JSONObject();
		expectedSolved1b.put("id", "2");
		expectedSolved1b.put(DummyEntity1.VALUE, "pino1_bis");
		
		JSONArray expectedSolved1List = new JSONArray();
		expectedSolved1List.add(expectedSolved1a);
		expectedSolved1List.add(expectedSolved1b);
		
		JSONObject expectedSolved4 = new JSONObject();
		expectedSolved4.put("id", "1");
		expectedSolved4.put("value", "lino4");
		expectedSolved4.put("dummyEntity1Id", expectedSolved1List);
		
		JSONArray expectedSolved4List = new JSONArray();
		expectedSolved4List.add(expectedSolved4);
		
		JSONObject expectedSolved5 = new JSONObject();
		expectedSolved5.put("id", "1");
		expectedSolved5.put("list", expectedSolved4List);
		expectedSolved5.put("value", "vino5");
		
		assertThat(solved5.toJSONString(), matchesJSONString(expectedSolved5.toJSONString()));
		
	}
}
