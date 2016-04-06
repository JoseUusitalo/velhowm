package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.RemovalPlatform;

/**
 * Tests for the {@link RemovalPlatform} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class RemovalPlatformTest
{
	private static RemovalPlatform existingPlatform = DatabaseController.getRemovalPlatformByID(1);

	@BeforeClass
	public static void init()
	{
		// Testing in production environment is fun.
		existingPlatform.setFreeSpacePercent(1.0);
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

		// Rollback.
		existingPlatform.modifyFreeSpace(0.125);
	}

	@Test
	public final void testEmpty()
	{
		final double old = existingPlatform.getFreeSpacePercent();

		existingPlatform.modifyFreeSpace(-0.125);
		assertTrue(Double.compare((1.0 - 0.125), existingPlatform.getFreeSpacePercent()) == 0);

		existingPlatform.empty();

		assertTrue(Double.compare((1.0), existingPlatform.getFreeSpacePercent()) == 0);

		// Rollback.
		existingPlatform.setFreeSpacePercent(old);
	}

	@Test
	public final void testGetFreeSpaceLeftWarningPercent()
	{
		assertEquals(0, Double.compare(0.1, existingPlatform.getFreeSpaceLeftWarningPercent()));
	}

	@Test
	public final void testsetFreeSpaceLeftWarningPercent()
	{
		final double old = existingPlatform.getFreeSpaceLeftWarningPercent();

		existingPlatform.setFreeSpaceLeftWarningPercent(0.5);

		assertEquals(0, Double.compare(0.5, existingPlatform.getFreeSpaceLeftWarningPercent()));

		// Rollback.
		existingPlatform.setFreeSpaceLeftWarningPercent(old);
	}

	@Test
	public final void testSetState_SaveToDatabase()
	{
		final double oldPercent = existingPlatform.getFreeSpacePercent();
		final double modPercent = -0.125;
		final double newPercent = oldPercent + modPercent;

		assertTrue(Double.compare(1.0, oldPercent) == 0);

		existingPlatform.modifyFreeSpace(modPercent);

		// Check that the method worked.
		assertEquals(0, Double.compare(newPercent, existingPlatform.getFreeSpacePercent()));
		DatabaseController.save(existingPlatform);

		// Database was updated.
		assertTrue(Double.compare(newPercent, DatabaseController.getRemovalPlatformByID(existingPlatform.getDatabaseID()).getFreeSpacePercent()) == 0);

		// Rollback.
		existingPlatform.setFreeSpacePercent(oldPercent);
		DatabaseController.save(existingPlatform);
	}
}
