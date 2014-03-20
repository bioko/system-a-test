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

package org.biokoframework.systema;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.entity.resolution.AnnotatedEntityResolver;
import org.biokoframework.system.entity.resolution.EntityResolver;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy1.DummyEntity1Builder;
import org.biokoframework.systema.entity.dummy2.DummyEntity2;
import org.biokoframework.systema.entity.dummy2.DummyEntity2Builder;
import org.biokoframework.systema.entity.dummy3.DummyEntity3;
import org.biokoframework.systema.entity.dummy3.DummyEntity3Builder;
import org.biokoframework.systema.entity.dummy4.DummyEntity4;
import org.biokoframework.systema.entity.dummy5.DummyEntity5;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EntityResolverTest {

	private InMemoryRepository<DummyEntity1> fRepository1;
	private InMemoryRepository<DummyEntity2> fRepository2;
	private Repository<DummyEntity3> fRepository3;
	private Repository<DummyEntity4> fRepository4;
	private Repository<DummyEntity5> fRepository5;
	
	@Before
	public void populateRepositories() throws ValidationException, RepositoryException {
		Injector injector = Guice.createInjector(new EntityModule());
		
		fRepository1 = new InMemoryRepository<DummyEntity1>(DummyEntity1.class, injector.getInstance(IEntityBuilderService.class));
		fRepository2 = new InMemoryRepository<DummyEntity2>(DummyEntity2.class, injector.getInstance(IEntityBuilderService.class));
		fRepository3 = new InMemoryRepository<DummyEntity3>(DummyEntity3.class, injector.getInstance(IEntityBuilderService.class));
		fRepository4 = new InMemoryRepository<DummyEntity4>(DummyEntity4.class, injector.getInstance(IEntityBuilderService.class));
		fRepository5 = new InMemoryRepository<DummyEntity5>(DummyEntity5.class, injector.getInstance(IEntityBuilderService.class));
		
		
		
		EntityBuilder<DummyEntity1> builder1 = new DummyEntity1Builder().loadDefaultExample();
		fRepository1.save(builder1.build(false));
		
		EntityBuilder<DummyEntity2> builder2 = new DummyEntity2Builder().loadDefaultExample();
		fRepository2.save(builder2.build(false));
		
		EntityBuilder<DummyEntity3> builder3 = new DummyEntity3Builder().loadDefaultExample();
		fRepository3.save(builder3.build(false));
		

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
		DummyEntity3 entity3 = fRepository3.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity3 solved3 = resolver.with(fRepository1, DummyEntity1.class)
									   .with(fRepository2, DummyEntity2.class)
									   .solve(entity3, DummyEntity3.class);
		
		JSONObject expectedSolved2 = new JSONObject();
		expectedSolved2.put("id", "1");
		expectedSolved2.put("dummyEntity1Id", new DummyEntity1Builder().loadDefaultExample().build(true));
		expectedSolved2.put("value", 314);
		
		JSONObject expectedSolved3 = new JSONObject();
		expectedSolved3.put("dummyEntity2Id", expectedSolved2);
		expectedSolved3.put("id", "1");
		expectedSolved3.put("value", "tino3");
		
		assertThat(solved3.toJSONString(), equalTo(expectedSolved3.toJSONString()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void missingRepository() throws Exception {
		DummyEntity3 entity3 = fRepository3.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity3 solved3 = resolver.with(fRepository2, DummyEntity2.class)
									   .solve(entity3, DummyEntity3.class);
		
		JSONObject expectedSolved2 = new JSONObject();
		expectedSolved2.put("id", "1");
		expectedSolved2.put("dummyEntity1Id", "1");
		expectedSolved2.put("value", 314);
		
		JSONObject expectedSolved3 = new JSONObject();
		expectedSolved3.put("dummyEntity2Id", expectedSolved2);
		expectedSolved3.put("id", "1");
		expectedSolved3.put("value", "tino3");
		
		assertThat(solved3.toJSONString(), equalTo(expectedSolved3.toJSONString()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void depthLimitedResolution() throws Exception {		
		DummyEntity3 entity3 = fRepository3.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity3 solved3 = resolver.with(fRepository2, DummyEntity2.class)
									   .with(fRepository1, DummyEntity1.class)
									   .maxDepth(1)
									   .solve(entity3, DummyEntity3.class);
		
		JSONObject expectedSolved2 = new JSONObject();
		expectedSolved2.put("id", "1");
		expectedSolved2.put("dummyEntity1Id", "1");
		expectedSolved2.put("value", 314);
		
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
		DummyEntity4 entity4 = fRepository4.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity4 solved4 = resolver.with(fRepository1, DummyEntity1.class)
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
		DummyEntity5 entity5 = fRepository5.retrieve("1");
		
		EntityResolver resolver = new AnnotatedEntityResolver();
		DummyEntity5 solved5 = resolver.with(fRepository1, DummyEntity1.class)
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
