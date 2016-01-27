package test.controller;

import static org.junit.Assert.*;

import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.exceptions.NoDatabaseConnectionException;

/**
 * Tests for the {@link velho.controller.Database} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class DatabaseControllerTest
{
	@Test
	public final void testDatabaseConnect() throws ClassNotFoundException
	{
		assertEquals(true, DatabaseController.connect());
		DatabaseController.shutdown();
	}

	@Test
	public final void testDatabaseConnection() throws ClassNotFoundException
	{
		DatabaseController.connect();
		assertEquals(true, DatabaseController.isConnected());
		DatabaseController.shutdown();
	}

	@Test(expected=NoDatabaseConnectionException.class)
	public final void testFailInitialization() throws NoDatabaseConnectionException
	{
		DatabaseController.initializeDatabase();
	}
}
