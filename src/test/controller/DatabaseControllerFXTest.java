package test.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.JavaFXThreadingRule;
import velho.model.enums.DatabaseFileState;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Additional tests for the {@link DatabaseController} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class DatabaseControllerFXTest
{
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	@Before
	public final void connectAndInitializeDatabase() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertTrue(DatabaseController.link() != DatabaseFileState.DOES_NOT_EXIST);
		assertTrue(DatabaseController.initializeDatabase());
	}

	@After
	public final void unlink()
	{
		try
		{
			DatabaseController.unlink();
		}
		catch (NoDatabaseLinkException e)
		{
			// This is ok.
		}
	}

	@Test
	public final void testRelink() throws NoDatabaseLinkException
	{
		assertTrue(DatabaseController.isLinked());
		DatabaseController.unlink();
		assertFalse(DatabaseController.isLinked());
		DatabaseController.tryReLink();
		assertTrue(DatabaseController.isLinked());
		DatabaseController.unlink();
	}
}
