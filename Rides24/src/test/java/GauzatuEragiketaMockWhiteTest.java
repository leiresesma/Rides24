import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import domain.User; 


public class GauzatuEragiketaMockWhiteTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;
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

    
    @Test
    // sut.gauzatuEragiketa: El usuario es nulo. El m�todo debe devolver false.
    public void test1() {
        String username = null;
        double amount = 50.0;
        boolean deposit = true;
        User user = new User(username, "password", "userType");

        try {
        	Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(typedQuery);		
    		Mockito.when(typedQuery.getSingleResult()).thenReturn(null);
           
    		sut.open();
            
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            sut.close();

         
            assertFalse(result);

        } catch (Exception e) {
            fail("No se esperaba ninguna excepci�n: " + e.getMessage());
        }
    }

    @Test
    // sut.gauzatuEragiketa: El usuario no existe en la base de datos. El m�todo debe devolver false.
    public void test2() {
        String username = "nonexistentUser";
        double amount = 50.0;
        User user = new User(username, "password", "userType");
        boolean deposit = true;

        try {
        	Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(typedQuery);		
    		Mockito.when(typedQuery.getSingleResult()).thenReturn(null);
           // Mockito.when(db.find(User.class, username)).thenReturn(null);

     
            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            sut.close();

           
            assertFalse(result);

        } catch (Exception e) {
            fail("No se esperaba ninguna excepci�n: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
	@Test
    // sut.gauzatuEragiketa: El usuario tiene fondos suficientes para un retiro.
    public void test3() {
        String username = "existingUser";
        double amount = 50.0;
        boolean deposit = false;
        User user = new User(username, "password", "userType");
        user.setMoney(100.0); 

        try {
        	Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(typedQuery);		
    		Mockito.when(typedQuery.getSingleResult()).thenReturn(user);
          

            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            sut.close();

            assertTrue(result);
            assertEquals(50.0, user.getMoney(), 0.01); 

        } catch (Exception e) {
            fail("No se esperaba ninguna otra excepci�n: " + e.getMessage());
        } finally {
			sut.close();
		}
    }

    @Test
    // sut.gauzatuEragiketa: El usuario no tiene suficientes fondos para un retiro, pero se completa la operacion.
    public void test4() {
        String username = "existingUser";
        double amount = 200.0;
        boolean deposit = false;
        User user = new User(username, "password", "userType");
        user.setMoney(100.0);

        try {
           	Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(typedQuery);		
    		Mockito.when(typedQuery.getSingleResult()).thenReturn(user);

            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            sut.close();

         
            assertTrue(result);
           

        } catch (Exception e) {
            fail("No se esperaba ninguna otra excepci�n: " + e.getMessage());
        }
    }

    @Test
    // sut.gauzatuEragiketa: Verificar el dep�sito exitoso.
    public void test5() {
        String username = "existingUser";
        double amount = 50.0;
        boolean deposit = true;
        User user = new User(username, "password", "userType");
        user.setMoney(100.0); 

        try {
           
           	Mockito.when(db.createQuery(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(typedQuery);		
    		Mockito.when(typedQuery.getSingleResult()).thenReturn(user);

         
            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            sut.close();

            
            assertTrue(result);
            assertEquals(150.0, user.getMoney(), 0.01);

        } catch (Exception e) {
            fail("No se esperaba ninguna excepci�n: " + e.getMessage());
        }
    }
}
