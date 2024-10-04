
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.NoResultException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.User;
import testOperations.TestDataAccess;

public class GauzatuEragiketaBDWhiteTest {

	static DataAccess sut = new DataAccess();

	static TestDataAccess testDA = new TestDataAccess();

	@Before
	public void setUp() {
		sut.open();
	}

	@After
	public void tearDown() {
		sut.close();
	}

	@Test
	// sut.gauzatuEragiketa: The username is null. The test must return false.If an
	// Exception is returned the gauzatuAriketa method is not well implemented.
	public void test1() {
		String username = null;
		double amount = 50.0;
		boolean deposit = true;

		sut.open();
		try {
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);
			assertFalse(result);
		} catch (Exception e) {
			assertFalse(false);
		} finally {
			sut.close();
		}
	}

	@Test
	// sut.gauzatuEragiketa: The user does not exist in the DB. The test must return false.If an
	// Exception is returned the gauzatuAriketa method is not well implemented.
	public void test2() {
		String username = "nonexistentUser";
		double amount = 50.0;
		boolean deposit = true;

		try {
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);

			assertFalse(result);
		} catch (Exception e) {
			assertFalse(false);
		} finally {
			sut.close();
		}
	}

	@Test
	// sut.gauzatuEragiketa: The user has sufficient funds for a withdrawal. The
	// test must return true.
	public void test3() {
		String username = "Urtzi";
		double amount = 50.0;
		boolean deposit = false;

		try {
			testDA.open();
			testDA.addUserWithMoney(username, "123", 100.0); // user with 100 units of money
			testDA.close();

			sut.open();
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);

			assertTrue(true);
		} finally {
			testDA.open();
			testDA.removeUserWithMoney(username);
			testDA.close();
		}
	}

	@Test
	// sut.gauzatuEragiketa: The user does not have enough funds for a withdrawal,
	// but it still returns true.
	public void test4() {
		String username = "Urtzi";
		double amount = 200.0;
		boolean deposit = false;

		try {
			testDA.open();
			testDA.addUserWithMoney(username, "password", 100.0); // user with 100 units of money
			testDA.close();

			sut.open();
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);

			assertTrue(result);
		} finally {
			testDA.open();
			testDA.removeUserWithMoney(username);
			testDA.close();
		}
	}

	@Test
	// sut.gauzatuEragiketa: A deposit operation is successful. The test must return
	// true.
	public void test5() {
		String username = "Urtzi";
		double amount = 50.0;
		boolean deposit = true;

		try {
			testDA.open();
			testDA.addUserWithMoney(username, "password", 100.0);
			testDA.close();

			sut.open();
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);

			assertTrue(result);
		} finally {
			testDA.open();
			testDA.removeUserWithMoney(username);
			testDA.close();
		}
	}

}
