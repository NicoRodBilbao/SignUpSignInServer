package suite;

import dataAccessTest.DAOServerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import factoryTest.UserFactoryTest;
import poolTest.PoolTest;

/**
 * This class executes all the methods of all the test classes in the tests folder.
 *
 * @author Nicolás Rodríguez
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    UserFactoryTest.class,
    DAOServerTest.class,
    PoolTest.class})
public class TestSuite {
    
}
