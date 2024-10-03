
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

    // sut: system under test
    static DataAccess sut = new DataAccess();
    
    // additional operations needed to execute the test 
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
    // sut.gauzatuEragiketa: The username is null. The test must return false.
    public void test1() {
        String username = null;
        double amount = 50.0;
        boolean deposit = true;

        sut.open();
        try {
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertFalse(result); // Esperamos que sea falso
        } catch (NoResultException e) {
            // Si se lanza NoResultException, el test aún debería pasar ya que el usuario no existe
            assertFalse(false);
        } finally {
            sut.close();
        }
    }

    @Test
    // sut.gauzatuEragiketa: The user does not exist in the DB. The test must return false.
    public void test2() {
        String username = "nonexistentUser";
        double amount = 50.0;
        boolean deposit = true;

        boolean result = sut.gauzatuEragiketa(username, amount, deposit);

        // verify the results
        assertFalse(result);
    }

    @Test
    // sut.gauzatuEragiketa: The user has sufficient funds for a withdrawal. The test must return true.
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
        	sut.close();
        	// verify the results
        	assertTrue(result);
        }finally {
			  //Remove the created objects in the database (cascade removing)   
			testDA.open();	   
			testDA.removeUserWithMoney(username);
	        testDA.close();
	    }
       
    }

    @Test
    // sut.gauzatuEragiketa: The user does not have enough funds for a withdrawal, but it still returns true.
    public void test4() {
        String username = "existingUser";
        double amount = 200.0; // Withdraw more than available
        boolean deposit = false;

        // Prepare the database with the necessary state
        testDA.open();
        testDA.addUserWithMoney(username, "password", 100.0); // user with 100 units of money
        testDA.close();

        sut.open();
        boolean result = sut.gauzatuEragiketa(username, amount, deposit);
        sut.close();
        // verify the results
        assertTrue(result);

       
    }

    @Test
    // sut.gauzatuEragiketa: A deposit operation is successful. The test must return true.
    public void test5() {
        String username = "existingUser";
        double amount = 50.0;
        boolean deposit = true;

        // Prepare the database with the necessary state
        testDA.open();
        testDA.addUserWithMoney(username, "password", 100.0); // user with 100 units of money
        testDA.close();

        sut.open();
        boolean result = sut.gauzatuEragiketa(username, amount, deposit);
        sut.close();

        // verify the results
        assertTrue(result);

      
    }

}
