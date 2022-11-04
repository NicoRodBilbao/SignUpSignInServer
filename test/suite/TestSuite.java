package suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import factoryTest.UserFactoryTest;

/**
 *
 * @author Nicolás Rodríguez
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    UserFactoryTest.class})
public class TestSuite {
    
}
