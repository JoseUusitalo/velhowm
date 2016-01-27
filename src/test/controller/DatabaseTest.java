package test.controller;

import static org.junit.Assert.*;

import org.junit.Test;

import velho.controller.Database;
import velho.model.exceptions.NoDatabaseConnectionException;

/**
 * Tests for the {@link velho.controller.Database} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class DatabaseTest
{
	@Test
	public final void testDatabaseConnect() throws ClassNotFoundException
	{
		assertEquals(true, Database.connect());
		Database.shutdown();
	}

	@Test
	public final void testDatabaseConnection() throws ClassNotFoundException
	{
		Database.connect();
		assertEquals(true, Database.isConnected());
		Database.shutdown();
	}

	@Test(expected=NoDatabaseConnectionException.class)
	public final void testFailInitialization() throws ClassNotFoundException, NoDatabaseConnectionException
	{
		Database.initializeDatabase();
	}
}
