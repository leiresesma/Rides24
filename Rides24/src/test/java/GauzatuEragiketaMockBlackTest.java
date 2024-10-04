import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.User;

public class GauzatuEragiketaMockBlackTest {

	static DataAccess sut;

	protected MockedStatic<Persistence> persistenceMock;
	@Mock
	protected EntityManager db;
	@Mock
	protected EntityTransaction et;

	@Mock
	protected EntityManagerFactory entityManagerFactory;

	@Mock
	TypedQuery<User> typedQuery;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
		persistenceMock = Mockito.mockStatic(Persistence.class);
		persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
				.thenReturn(entityManagerFactory);

		Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
		Mockito.doReturn(et).when(db).getTransaction();
		sut = new DataAccess(db);
	}

	@After
	public void tearDown() {
		persistenceMock.close();
	}

	// Existe el usuario y la transacción se completa correctamente
	@Test
	public void test1() {
		String username = "Urtzi";
		double amount = 50.0;
		boolean deposit = true;

		// Mock a user with sufficient balance
		User mockUser = new User(username, "123", "regular");
		try {
			Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(typedQuery);
			Mockito.when(typedQuery.getSingleResult()).thenReturn(mockUser);

			sut.open();
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);
			sut.close();
			assertTrue(result);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BK-ak: 7
	// username == null / Tiene que saltar una excepcion de query is null
	public void test2() {
		String username = null;
		double amount = 3.0;
		boolean deposit = true;

		try {
			sut.open();
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);
			sut.close();
			
			assertFalse(result);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// BK-ak: 8
	// amount == null (aunque en Java `double` no puede ser `null`, simulamos un
	// caso no válido)
	public void test3() {
		String username = "Urtzi";
		Double amount = null; // Usamos `Double` para permitir `null`
		boolean deposit = true;

		try {
			// Invocar el método
			sut.open();
			sut.gauzatuEragiketa(username, amount, deposit);
			sut.close();
			fail(); // Si llega aquí, algo salió mal
		} catch (NullPointerException e) {
			// Se espera una excepción
			assertTrue(true);
		}
	}

	@Test
	// BK-ak: 9
	// deposit == null (aunque en Java `boolean` no puede ser `null`, simulamos un
	// caso no válido)
	public void test4() {
		String username = "Urtzi";
		double amount = 3.0;
		Boolean deposit = null; // Usamos `Boolean` para permitir `null`

		try {
			// Invocar el método
			sut.open();
			sut.gauzatuEragiketa(username, amount, deposit);
			sut.close();
			fail(); // Si llega aquí, algo salió mal
		} catch (NullPointerException e) {
			// Se espera una excepción
			assertTrue(true);
		}
	}

	@Test
	// BK-ak: 10
	// user ⊄ DB
	public void test5() {
		String username = "Julen";
		double amount = 3.0;
		boolean deposit = true;

		// No existe el usuario en la BD
		Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(typedQuery);
		Mockito.when(typedQuery.getSingleResult()).thenReturn(null);

		try {
			sut.open();
			boolean result = sut.gauzatuEragiketa(username, amount, deposit);
			sut.close();
			assertFalse(result);
		} catch (Exception e) {
			fail();
		}
	}

}
