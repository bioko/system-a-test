package it.engaged.framework.systemA.unit;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import it.bioko.system.command.ValidationException;
import it.bioko.system.repository.core.RepositoryException;
import it.bioko.system.repository.core.query.Query;
import it.bioko.system.repository.memory.InMemoryRepository;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy1.DummyEntity1Builder;
import it.bioko.systema.entity.dummy2.DummyEntity2;
import it.bioko.systema.entity.dummy2.DummyEntity2Builder;
import it.bioko.systema.entity.dummyWithDate.DummyEntityWithDate;
import it.bioko.systema.entity.dummyWithDate.DummyEntityWithDateBuilder;
import it.bioko.systema.entity.dummyWithInteger.DummyEntityWithInteger;
import it.bioko.systema.entity.dummyWithInteger.DummyEntityWithIntegerBuilder;
import it.bioko.utils.domain.EntityBuilder;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SqlQueryTest {

	private InMemoryRepository<DummyEntity1> _dummy1Repo;
	private InMemoryRepository<DummyEntity2> _dummy2Repo;
	private InMemoryRepository<DummyEntityWithInteger> _dummyWithIntegerRepo;
	private InMemoryRepository<DummyEntityWithDate> _dummyWithDateRepo;


	@Before
	public void createRepository() throws Exception {
		_dummy1Repo = new InMemoryRepository<DummyEntity1>(DummyEntity1.class);
		_dummy2Repo = new InMemoryRepository<DummyEntity2>(DummyEntity2.class);
		_dummyWithIntegerRepo = new InMemoryRepository<DummyEntityWithInteger>(DummyEntityWithInteger.class);
		_dummyWithDateRepo = new InMemoryRepository<DummyEntityWithDate>(DummyEntityWithDate.class);
	}

	@Test
	public void simpleQueryTest() throws Exception {
		String placeholderName = "label";

		EntityBuilder<DummyEntity1> dummyEntity1Builder = new DummyEntity1Builder().loadDefaultExample();
		_dummy1Repo.save(dummyEntity1Builder.build(false));

		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		_dummy1Repo.save(dummyEntity1Builder.build(false));


		Query<DummyEntity1> query = _dummy1Repo.createQuery();
		query.select().
		from(_dummy1Repo, DummyEntity1.class).
		where(DummyEntity1.VALUE).like().placeholder(placeholderName);

		assertThat(query, hasToString("select * from DummyEntity1 where (value like <label>) ;"));

		query.setValue(placeholderName, "Pino");

		assertThat(query.getAll(), contains(dummyEntity1Builder.build("2")));

	}

	@Test
	public void twoConstraintsQueryTest() throws Exception {

		EntityBuilder<DummyEntity1> dummyEntity1Builder = new DummyEntity1Builder().loadDefaultExample();
		_dummy1Repo.save(dummyEntity1Builder.build(false));
		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		_dummy1Repo.save(dummyEntity1Builder.build(false));

		EntityBuilder<DummyEntity2> dummyEntity2Builder = new DummyEntity2Builder().loadDefaultExample();
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.VALUE, "TheExpected");
		DummyEntity2 dummyEntity2 = dummyEntity2Builder.build("2");
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.ENTITY1_ID, "2");
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		Query<DummyEntity2> query = _dummy2Repo.createQuery();
		query.select().
		from(_dummy2Repo, DummyEntity2.class).
		where(DummyEntity2.VALUE).like().placeholder("value").
		and(DummyEntity2.ENTITY1_ID).isEqual().placeholder("id");

		assertThat(query, hasToString("select * from DummyEntity2 where (value like <value>) and (dummyEntity1Id = <id>) ;"));

		query.setValue("id", "1");
		query.setValue("value", "TheExpected");

		assertThat(query.getAll(), contains(dummyEntity2));

	}

	@Test
	public void twoConstraintsWithNotQueryTest() throws Exception {

		EntityBuilder<DummyEntity1> dummyEntity1Builder = new DummyEntity1Builder().loadDefaultExample();
		_dummy1Repo.save(dummyEntity1Builder.build(false));
		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		_dummy1Repo.save(dummyEntity1Builder.build(false));

		EntityBuilder<DummyEntity2> dummyEntity2Builder = new DummyEntity2Builder().loadDefaultExample();
		DummyEntity2 firstExpected = dummyEntity2Builder.build(true);
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.VALUE, "TheExpected");
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.ENTITY1_ID, "2");
		DummyEntity2 secondExpected = dummyEntity2Builder.build("3");
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		Query<DummyEntity2> query = _dummy2Repo.createQuery();
		query.select().
		from(_dummy2Repo, DummyEntity2.class).
		where(DummyEntity2.VALUE).notLike().placeholder("value").
		or(DummyEntity2.ENTITY1_ID).isNotEqual().placeholder("id");

		assertThat(query, hasToString("select * from DummyEntity2 where (value not like <value>) or (dummyEntity1Id != <id>) ;"));

		query.setValue("id", "1");
		query.setValue("value", "TheExpected");

		assertThat(query.getAll(), contains(firstExpected, secondExpected));

	}

	@Test
	public void twoConstraintWithValueQueryTest() throws Exception {

		EntityBuilder<DummyEntity1> dummyEntity1Builder = new DummyEntity1Builder().loadDefaultExample();
		_dummy1Repo.save(dummyEntity1Builder.build(false));
		dummyEntity1Builder.set(DummyEntity1.VALUE, "Pino");
		_dummy1Repo.save(dummyEntity1Builder.build(false));

		EntityBuilder<DummyEntity2> dummyEntity2Builder = new DummyEntity2Builder().loadDefaultExample();
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.VALUE, "TheExpected");
		DummyEntity2 dummyEntity2 = dummyEntity2Builder.build("2");
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		dummyEntity2Builder.set(DummyEntity2.ENTITY1_ID, "2");
		_dummy2Repo.save(dummyEntity2Builder.build(false));

		Query<DummyEntity2> query = _dummy2Repo.createQuery();
		query.toString();
		query.select().
		from(_dummy2Repo, DummyEntity2.class).
		where(DummyEntity2.VALUE).like("TheExpected").
		and(DummyEntity2.ENTITY1_ID).isEqual("1");

		assertThat(query, hasToString("select * from DummyEntity2 where (value like TheExpected) and (dummyEntity1Id = 1) ;"));

		assertThat(query.getAll(), contains(dummyEntity2));

	}

	@Test
	public void ilikeTest() throws RepositoryException, ValidationException {
		EntityBuilder<DummyEntity1> builder = new DummyEntity1Builder();

		_dummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE1).build(false));
		_dummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE2).build(false));
		_dummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE3).build(false));

		Query<DummyEntity1> q = _dummy1Repo.createQuery();

		q.select().
		from(_dummy1Repo, DummyEntity1.class).
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
		EntityBuilder<DummyEntity1> builder = new DummyEntity1Builder();

		_dummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE1).build(false));
		_dummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE2).build(false));
		_dummy1Repo.save(builder.loadExample(DummyEntity1Builder.EXAMPLE3).build(false));

		Query<DummyEntity1> q = _dummy1Repo.createQuery();

		q.select().
		from(_dummy1Repo, DummyEntity1.class).
		where(DummyEntity1.VALUE).slike("pino1");

		List<DummyEntity1> res = q.getAll();

		assertEquals(1, res.size());
		assertThat(res, contains(builder.loadExample(DummyEntity1Builder.EXAMPLE1).build(true)));

	}

	@Test
	public void lesserTest() throws RepositoryException, ValidationException {
		DummyEntityWithIntegerBuilder builder = new DummyEntityWithIntegerBuilder();

		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithInteger> q = _dummyWithIntegerRepo.createQuery();
		q.select().
		from(_dummyWithIntegerRepo, DummyEntityWithInteger.class).
		where(DummyEntityWithInteger.VALUE).
		lt("5");
		

		List<DummyEntityWithInteger> res = q.getAll();		
		assertEquals(4, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true)
				));


		q = _dummyWithIntegerRepo.createQuery();
		q.select().
		from(_dummyWithIntegerRepo, DummyEntityWithInteger.class).
		where(DummyEntityWithInteger.VALUE).
		lte("5");

		res = q.getAll();		
		assertEquals(5, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(true)
				));

	}
	
	
	@Test
	public void greaterTest() throws RepositoryException, ValidationException {
		DummyEntityWithIntegerBuilder builder = new DummyEntityWithIntegerBuilder();

		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE1).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE2).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(false));
		_dummyWithIntegerRepo.save(builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithInteger> q = _dummyWithIntegerRepo.createQuery();
		q.select().
		from(_dummyWithIntegerRepo, DummyEntityWithInteger.class).
		where(DummyEntityWithInteger.VALUE).
		gt("3");
		

		List<DummyEntityWithInteger> res = q.getAll();		
		assertEquals(2, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(true)
				));


		q = _dummyWithIntegerRepo.createQuery();
		q.select().
		from(_dummyWithIntegerRepo, DummyEntityWithInteger.class).
		where(DummyEntityWithInteger.VALUE).
		gte("3");

		res = q.getAll();
		
		assertEquals(3, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE3).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE4).build(true),
				builder.loadExample(DummyEntityWithIntegerBuilder.EXAMPLE5).build(true)
				));

	}
	
	
	@Test
	public void lesserWithDateTest() throws RepositoryException, ValidationException {
		DummyEntityWithDateBuilder builder = new DummyEntityWithDateBuilder();

		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithDate> q = _dummyWithDateRepo.createQuery();
		q.select().
		from(_dummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithInteger.VALUE).
		lt(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).get(DummyEntityWithDate.VALUE));
		

		List<DummyEntityWithDate> res = q.getAll();	
		
		assertEquals(4, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true)
				));

		
		
		
		q = _dummyWithDateRepo.createQuery();
		q.select().
		from(_dummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithDate.VALUE).
		lte(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).get(DummyEntityWithDate.VALUE));

		res = q.getAll();		
		assertEquals(5, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(true)
				));

	}
	
	
	
	@Test
	public void greaterWithDateTest() throws RepositoryException, ValidationException {
		DummyEntityWithDateBuilder builder = new DummyEntityWithDateBuilder();

		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE1).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE2).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(false));
		_dummyWithDateRepo.save(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(false));

		Query<DummyEntityWithDate> q = _dummyWithDateRepo.createQuery();
		q.select().
		from(_dummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithInteger.VALUE).
		gt(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).get(DummyEntityWithDate.VALUE));
		

		List<DummyEntityWithDate> res = q.getAll();	
		
		
		assertEquals(2, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(true)
				));

		
		
		
		q = _dummyWithDateRepo.createQuery();
		q.select().
		from(_dummyWithDateRepo, DummyEntityWithDate.class).
		where(DummyEntityWithDate.VALUE).
		gte(builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).get(DummyEntityWithDate.VALUE));

		res = q.getAll();		
		assertEquals(3, res.size());
		assertThat(res, containsInAnyOrder(
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE3).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE4).build(true),
				builder.loadExample(DummyEntityWithDateBuilder.EXAMPLE5).build(true)
				));

	}


}
