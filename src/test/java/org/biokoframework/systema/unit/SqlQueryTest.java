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

package org.biokoframework.systema.unit;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy1.DummyEntity1Builder;
import org.biokoframework.systema.entity.dummy2.DummyEntity2;
import org.biokoframework.systema.entity.dummy2.DummyEntity2Builder;
import org.biokoframework.systema.entity.dummyWithDate.DummyEntityWithDate;
import org.biokoframework.systema.entity.dummyWithDate.DummyEntityWithDateBuilder;
import org.biokoframework.systema.entity.dummyWithInteger.DummyEntityWithInteger;
import org.biokoframework.systema.entity.dummyWithInteger.DummyEntityWithIntegerBuilder;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.repository.query.Query;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SqlQueryTest {

	private InMemoryRepository<DummyEntity1> fDummy1Repo;
	private InMemoryRepository<DummyEntity2> fDummy2Repo;
	private InMemoryRepository<DummyEntityWithInteger> fDummyWithIntegerRepo;
	private InMemoryRepository<DummyEntityWithDate> fDummyWithDateRepo;

	private Injector fInjector;
	private DummyEntity1Builder fDummy1Builder;
	private DummyEntity2Builder fDummy2Builder;
	private DummyEntityWithDateBuilder fDummyWithDateBuilder;
	private DummyEntityWithIntegerBuilder fDummyWithIntegerBuilder;

	@Before
	public void createRepository() throws Exception {
		fInjector = Guice.createInjector(new EntityModule(), new ValidationModule());
		
		fDummy1Builder = fInjector.getInstance(DummyEntity1Builder.class);
		fDummy2Builder = fInjector.getInstance(DummyEntity2Builder.class);
		fDummyWithDateBuilder = fInjector.getInstance(DummyEntityWithDateBuilder.class);
		fDummyWithIntegerBuilder = fInjector.getInstance(DummyEntityWithIntegerBuilder.class);
		
		fDummy1Repo = new InMemoryRepository<DummyEntity1>(DummyEntity1.class, fInjector.getInstance(IEntityBuilderService.class));
		fDummy2Repo = new InMemoryRepository<DummyEntity2>(DummyEntity2.class, fInjector.getInstance(IEntityBuilderService.class));
		fDummyWithIntegerRepo = new InMemoryRepository<DummyEntityWithInteger>(DummyEntityWithInteger.class, fInjector.getInstance(IEntityBuilderService.class));
		fDummyWithDateRepo = new InMemoryRepository<DummyEntityWithDate>(DummyEntityWithDate.class, fInjector.getInstance(IEntityBuilderService.class));
	}

	@Test
	public void simpleQueryTest() throws Exception {
		String placeholderName = "label";

		EntityBuilder<DummyEntity1> dummyEntity1Builder = fDummy1Builder.loadDefaultExample();
		fDummy1Repo.save(dummyEntity1Builder.build(false));

		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		fDummy1Repo.save(dummyEntity1Builder.build(false));


		Query<DummyEntity1> query = fDummy1Repo.createQuery();
		query.select().
		from(fDummy1Repo, DummyEntity1.class).
		where(DummyEntity1.VALUE).like().placeholder(placeholderName);

		assertThat(query, hasToString("select * from DummyEntity1 where (value like <label>) ;"));

		query.setValue(placeholderName, "Pino");

		assertThat(query.getAll(), contains(dummyEntity1Builder.build("2")));

	}

	@Test
	public void twoConstraintsQueryTest() throws Exception {

		EntityBuilder<DummyEntity1> dummyEntity1Builder = fDummy1Builder.loadDefaultExample();
		fDummy1Repo.save(dummyEntity1Builder.build(false));
		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		fDummy1Repo.save(dummyEntity1Builder.build(false));

		EntityBuilder<DummyEntity2> dummyEntity2Builder = fDummy2Builder.loadDefaultExample();
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.VALUE, 1234L);
		DummyEntity2 dummyEntity2 = dummyEntity2Builder.build("2");
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.ENTITY1_ID, "2");
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		Query<DummyEntity2> query = fDummy2Repo.createQuery();
		query.select().
		from(fDummy2Repo, DummyEntity2.class).
		where(DummyEntity2.VALUE).isEqual().placeholder("value").
		and(DummyEntity2.ENTITY1_ID).isEqual().placeholder("id");

		assertThat(query, hasToString("select * from DummyEntity2 where (value = <value>) and (dummyEntity1Id = <id>) ;"));

		query.setValue("id", "1");
		query.setValue("value", 1234L);

		assertThat(query.getAll(), contains(dummyEntity2));

	}

	@Test
	public void twoConstraintsWithNotQueryTest() throws Exception {
		EntityBuilder<DummyEntity1> dummyEntity1Builder = fDummy1Builder.loadDefaultExample();
		fDummy1Repo.save(dummyEntity1Builder.build(false));
		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		fDummy1Repo.save(dummyEntity1Builder.build(false));

		EntityBuilder<DummyEntity2> dummyEntity2Builder = fDummy2Builder.loadDefaultExample();
		DummyEntity2 firstExpected = dummyEntity2Builder.build(true);
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.VALUE, 123456L);
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.ENTITY1_ID, "2");
		DummyEntity2 secondExpected = dummyEntity2Builder.build("3");
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		Query<DummyEntity2> query = fDummy2Repo.createQuery();
		query.select().
		from(fDummy2Repo, DummyEntity2.class).
		where(DummyEntity2.VALUE).isNotEqual().placeholder("value").
		or(DummyEntity2.ENTITY1_ID).isNotEqual().placeholder("id");

		assertThat(query, hasToString("select * from DummyEntity2 where (value != <value>) or (dummyEntity1Id != <id>) ;"));

		query.setValue("id", "1");
		query.setValue("value", 123456L);

		assertThat(query.getAll(), contains(firstExpected, secondExpected));

	}

	@Test
	public void twoConstraintWithValueQueryTest() throws Exception {

		EntityBuilder<DummyEntity1> dummyEntity1Builder = fDummy1Builder.loadDefaultExample();
		fDummy1Repo.save(dummyEntity1Builder.build(false));
		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		fDummy1Repo.save(dummyEntity1Builder.build(false));

		EntityBuilder<DummyEntity2> dummyEntity2Builder = fDummy2Builder.loadDefaultExample();
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.VALUE, 54321L);
		DummyEntity2 dummyEntity2 = dummyEntity2Builder.build("2");
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.ENTITY1_ID, "2");
		fDummy2Repo.save(dummyEntity2Builder.build(false));

		Query<DummyEntity2> query = fDummy2Repo.createQuery();
		query.toString();
		query.select().
		from(fDummy2Repo, DummyEntity2.class).
		where(DummyEntity2.VALUE).isEqual(54321L).
		and(DummyEntity2.ENTITY1_ID).isEqual("1");

		assertThat(query, hasToString("select * from DummyEntity2 where (value = 54321) and (dummyEntity1Id = 1) ;"));

		assertThat(query.getAll(), contains(dummyEntity2));

	}

	@Test
	public void ilikeTest() throws RepositoryException, ValidationException {
		EntityBuilder<DummyEntity1> builder = fDummy1Builder;

		fDummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE1).build(false));
		fDummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE2).build(false));
		fDummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE3).build(false));

		Query<DummyEntity1> q = fDummy1Repo.createQuery();

		q.select().
		from(fDummy1Repo, DummyEntity1.class).
		where(DummyEntity1.VALUE).ilike("pino1");

		List<DummyEntity1> res = q.getAll();

		assertEquals(3, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntity1Builder.EXAMPLE1).build(true),
				builder.loadExample(DummyEntity1Builder.EXAMPLE2).build(true),
				builder.loadExample(DummyEntity1Builder.EXAMPLE3).build(true)
				));

	}

	@Test
	public void slikeTest() throws RepositoryException, ValidationException {
		EntityBuilder<DummyEntity1> builder = fDummy1Builder;

		fDummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE1).build(false));
		fDummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE2).build(false));
		fDummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE3).build(false));

		Query<DummyEntity1> q = fDummy1Repo.createQuery();

		q.select().
		from(fDummy1Repo, DummyEntity1.class).
		where(DummyEntity1.VALUE).slike("pino1");

		List<DummyEntity1> res = q.getAll();

		assertEquals(1, res.size());
		assertThat(res, contains(builder.loadExample(DummyEntity1Builder.EXAMPLE1).build(true)));

	}

	@Test
	public void lesserTest() throws RepositoryException, ValidationException {
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithInteger> q = fDummyWithIntegerRepo.createQuery();
		q.select().
		from(fDummyWithIntegerRepo, DummyEntityWithInteger.class).
		where(DummyEntityWithInteger.VALUE).
		lt("5");
		

		List<DummyEntityWithInteger> res = q.getAll();		
		assertEquals(4, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true)
				));


		q = fDummyWithIntegerRepo.createQuery();
		q.select().
		from(fDummyWithIntegerRepo, DummyEntityWithInteger.class).
		where(DummyEntityWithInteger.VALUE).
		lte("5");

		res = q.getAll();		
		assertEquals(5, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(true)
				));

	}
	
	
	@Test
	public void greaterTest() throws RepositoryException, ValidationException {
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(false));
		fDummyWithIntegerRepo.save(fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithInteger> q = fDummyWithIntegerRepo.createQuery();
		q.select().
			from(fDummyWithIntegerRepo, DummyEntityWithInteger.class).
			where(DummyEntityWithInteger.VALUE).
			gt("3");
		

		List<DummyEntityWithInteger> res = q.getAll();		
		assertEquals(2, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(true)));


		q = fDummyWithIntegerRepo.createQuery();
		q.select().
			from(fDummyWithIntegerRepo, DummyEntityWithInteger.class).
			where(DummyEntityWithInteger.VALUE).
			gte("3");

		res = q.getAll();
		
		assertEquals(3, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true),
				fDummyWithIntegerBuilder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(true)));

	}
	
	
	@Test
	public void lesserWithDateTest() throws RepositoryException, ValidationException {
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithDate> q = fDummyWithDateRepo.createQuery();
		q.select().
		from(fDummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithInteger.VALUE).
		lt(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).get(DummyEntityWithDate.VALUE));
		

		List<DummyEntityWithDate> res = q.getAll();	
		
		assertEquals(4, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true)
				));

		
		
		
		q = fDummyWithDateRepo.createQuery();
		q.select().
		from(fDummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithDate.VALUE).
		lte(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).get(DummyEntityWithDate.VALUE));

		res = q.getAll();		
		assertEquals(5, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(true)));

	}
	
	
	
	@Test
	public void greaterWithDateTest() throws RepositoryException, ValidationException {
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(false));
		fDummyWithDateRepo.save(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithDate> q = fDummyWithDateRepo.createQuery();
		q.select().
		from(fDummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithInteger.VALUE).
		gt(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).get(DummyEntityWithDate.VALUE));
		

		List<DummyEntityWithDate> res = q.getAll();	
		
		
		assertEquals(2, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(true)));

		
		
		
		q = fDummyWithDateRepo.createQuery();
		q.select().
		from(fDummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithDate.VALUE).
		gte(fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).get(DummyEntityWithDate.VALUE));

		res = q.getAll();		
		assertEquals(3, res.size());
		assertThat(res, containsInAnyOrder(
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true),
				fDummyWithDateBuilder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(true)));

	}


}
