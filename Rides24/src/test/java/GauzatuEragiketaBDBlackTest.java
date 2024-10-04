import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import testOperations.TestDataAccess;

public class GauzatuEragiketaBDBlackTest {

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
    // sut.gauzatuEragiketa: The username is null. The test must return false.
    public void test1() {
        String username = null;
        double amount = 50.0;
        boolean deposit = true;

        try {
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertFalse(result);
        } catch (Exception e) {
            fail("Exception should not be thrown.");
        }
    }

    @Test
    // sut.gauzatuEragiketa: The user does not exist in the DB. The test must return false.
    public void test2() {
        String username = "nonexistentUser";
        double amount = 50.0;
        boolean deposit = true;

        try {
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertFalse(result);
        } catch (Exception e) {
            fail("Exception should not be thrown.");
        }
    }

    @Test
    //El amount es null
    public void test3() {
        String username = "Urtzi";
        Double amount = null;
        boolean deposit = false;

        try {
            testDA.open();
            testDA.addUserWithMoney(username, "123", 100.0); // User with 100 units of money
            testDA.close();

            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            fail("No deberia de haber llegado aqui");
        } catch (Exception e) {
            
            assertFalse(false);
        } finally {
            testDA.open();
            testDA.removeUserWithMoney(username);
            testDA.close();
        }
    }

    @Test
    //El deposito es null
    public void test4() {
        String username = "Urtzi";
        double amount = 200.0;
        Boolean deposit = null;

        try {
            testDA.open();
            testDA.addUserWithMoney(username, "123", 100.0); // User with 100 units of money
            testDA.close();

            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            fail("No deberia de haber llegado aqui");
        } catch (Exception e) {
            
            assertFalse(false);
        } finally {
            testDA.open();
            testDA.removeUserWithMoney(username);
            testDA.close();
        }
    }
    
    @Test
    
    public void test5() {
        String username = "Urtzi";
        double amount = 50.0;
        boolean deposit = true;

        try {
            testDA.open();
            testDA.addUserWithMoney(username, "123", 100.0);
            testDA.close();

            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertTrue(result); 
        } catch (Exception e) {
            fail("Exception should not be thrown.");
        } finally {
            testDA.open();
            testDA.removeUserWithMoney(username);
            testDA.close();
        }
    }

}
