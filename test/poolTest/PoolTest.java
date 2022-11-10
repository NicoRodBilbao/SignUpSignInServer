package poolTest;

import exceptions.ServerException;
import org.junit.Test;

import pool.Pool;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;

import static org.junit.Assert.*;

/**
 * This test class tests that the connection pool functions properly.
 *
 * @author Nicolas Rodriguez
 */
public class PoolTest {

	public PoolTest() {
	}

	@After
	public void tearDown() {
		try {
			Pool.getPool().killAllConnections();
		} catch (ServerException ex) {
			Logger.getLogger(PoolTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Test of getPool method, of class Pool.
	 */
	@Test
	public void testGetPool_0args() {
		Pool pool = Pool.getPool();
		assertNotNull(pool);
		assertEquals(0, pool.getFreeConnectionCount());
	}

	/**
	 * Test of getPool method, of class Pool.
	 */
	@Test
	public void testGetPool_int() throws Exception {
		Pool pool = Pool.getPool(2);
		assertNotNull(pool);
		assertEquals(2, pool.getFreeConnectionCount());
	}

	/**
	 * Test of getFreeConnectionCount method, of class Pool.
	 */
	@Test
	public void testGetFreeConnectionCount() {
		try {
			Pool pool = Pool.getPool(1);
			assertNotNull(pool);
			assertEquals(1, pool.getFreeConnectionCount());
		} catch (ServerException ex) {
			Logger.getLogger(PoolTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Test of getUsedConnectionCount method, of class Pool.
	 */
	@Test
	public void testGetUsedConnectionCount() {
		Pool pool = Pool.getPool();
		assertNotNull(pool);
		assertEquals(0, pool.getUsedConnectionCount());
	}

	/**
	 * Test of getConnection method, of class Pool.
	 */
	@Test
	public void testGetConnection() {
		try {
			Pool pool = Pool.getPool();
			assertNotNull(pool);
			assertNotNull(pool.getConnection());
			assertEquals(1, pool.getUsedConnectionCount());
		} catch (ServerException ex) {
			Logger.getLogger(PoolTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Test of returnConnection method, of class Pool.
	 */
	@Test
	public void testReturnConnection() throws Exception {
		Pool pool = Pool.getPool();
		Connection con = pool.getConnection();
		assertEquals(0, pool.getFreeConnectionCount());
		pool.returnConnection(con);
		assertEquals(1, pool.getFreeConnectionCount());
	}

	/**
	 * Test of killAllConnections method, of class Pool.
	 */
	@Test
	public void testKillAllConnections() throws Exception {
		Pool pool = Pool.getPool();
		pool.getConnection();
		pool.killAllConnections();
		assertEquals(0, pool.getFreeConnectionCount());
		assertEquals(0, pool.getUsedConnectionCount());
	}

}
