package com.example.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
class DemoApplicationTests {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	void simpleSelectTest() {
		var demo_1 = new Demo(1L, "one");
		entityManager.persist(demo_1);
		var result = (Demo) entityManager.createNativeQuery("SELECT * FROM DEMO WHERE id = 1", Demo.class).getResultList().get(0);
		assertEquals("", 1L, result.getId());
	}

	@Test
	@Transactional
	void simpleSelectWithUnnestTest() {
		var resultSize = entityManager
				.createNativeQuery("SELECT * FROM UNNEST(ARRAY[1, 2, 3])")
				.getResultList()
				.size();
		assertEquals("", 3, resultSize);
	}

	@Test
	@Transactional
	// this test works fine with PostgreSQL, and throw exception with H2DB
	void complexSelectWithUnnestTest() {
		var demo_1 = new Demo(1L, "one");
		var demo_2 = new Demo(2L, "two");
		var demo_3 = new Demo(3L, "three");
		entityManager.persist(demo_1);
		entityManager.persist(demo_2);
		entityManager.persist(demo_3);
		var resultSize = entityManager
				.createNativeQuery("SELECT name, UNNEST(ARRAY[1, 2, 3]) FROM DEMO")
				.getResultList()
				.size();
		assertEquals("", 9, resultSize);
	}

}
