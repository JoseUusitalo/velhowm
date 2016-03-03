package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.RemovalPlatform;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link RemovalPlatform} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class RemovalPlatformTest
{
	private static RemovalPlatform existingPlatform;

	@Before
	public void initDatabase() throws Exception
	{
		try
		{
			if (!DatabaseController.isLinked())
				assertTrue(DatabaseController.link());

			assertTrue(DatabaseController.initializeDatabase());
		}
		catch (ClassNotFoundException | ExistingDatabaseLinkException e)
		{
			e.printStackTrace();
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		existingPlatform = DatabaseController.getRemovalPlatformByID(1, false);
	}

	@After
	public void unlinkDatabase() throws Exception
	{
		try
		{
			DatabaseController.unlink();
		}
		catch (NoDatabaseLinkException e)
		{
			// Ignore.
		}
	}

	@Test
	public final void testToString()
	{
		assertEquals("[1] Removal Platform: 100.0% (10.0%)", existingPlatform.toString());
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(1, existingPlatform.getDatabaseID());
	}

	@Test
	public final void testGetFreeSpacePercent()
	{
		assertTrue(Double.compare(1.0, existingPlatform.getFreeSpacePercent()) == 0);
	}

	@Test
	public final void testModifyFreeSpace()
	{
		existingPlatform.modifyFreeSpace(-0.125);
		assertTrue(Double.compare((1.0 - 0.125), existingPlatform.getFreeSpacePercent()) == 0);
	}

	@Test
	public final void testEmpty()
	{
		existingPlatform.modifyFreeSpace(-0.125);
		assertTrue(Double.compare((1.0 - 0.125), existingPlatform.getFreeSpacePercent()) == 0);

		existingPlatform.empty();

		assertTrue(Double.compare((1.0), existingPlatform.getFreeSpacePercent()) == 0);
	}

	@Test
	public final void testGetFreeSpaceLeftWarningPercent()
	{
		assertEquals(0, Double.compare(0.1, existingPlatform.getFreeSpaceLeftWarningPercent()));
	}

	@Test
	public final void testsetFreeSpaceLeftWarningPercent()
	{
		existingPlatform.setFreeSpaceWarningPercent(0.5);

		assertEquals(0, Double.compare(0.5, existingPlatform.getFreeSpaceLeftWarningPercent()));
	}

	@Test
	public final void testSetState_SaveToDatabase() throws NoDatabaseLinkException
	{
		existingPlatform = DatabaseController.getRemovalPlatformByID(1, false);

		final double oldPercent = existingPlatform.getFreeSpacePercent();
		final double modPercent = -0.125;
		final double newPercent = oldPercent + modPercent;

		assertTrue(Double.compare(1.0, oldPercent) == 0);

		existingPlatform.modifyFreeSpace(modPercent);

		// Check that the method worked.
		assertEquals(0, Double.compare(newPercent, existingPlatform.getFreeSpacePercent()));
		assertTrue(DatabaseController.save(existingPlatform));

		// Cache was updated
		assertTrue(Double.compare(newPercent, DatabaseController.getRemovalPlatformByID(existingPlatform.getDatabaseID(), true).getFreeSpacePercent()) == 0);

		// Database was updated.
		assertTrue(Double.compare(newPercent, DatabaseController.getRemovalPlatformByID(existingPlatform.getDatabaseID(), false).getFreeSpacePercent()) == 0);
	}
}
