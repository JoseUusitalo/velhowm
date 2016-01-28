package test.controller;

import static org.junit.Assert.*;

import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.exceptions.NoDatabaseLinkException;

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
		assertEquals(true, DatabaseController.link());
		DatabaseController.unlink();
	}

	@Test
	public final void testDatabaseConnection() throws ClassNotFoundException
	{
		DatabaseController.link();
		assertEquals(true, DatabaseController.isLinked());
		DatabaseController.unlink();
	}

	@Test(expected=NoDatabaseLinkException.class)
	public final void testFailInitialization() throws NoDatabaseLinkException
	{
		DatabaseController.initializeDatabase();
	}
}
