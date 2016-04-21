package velho.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.enums.UserRole;

/**
 * Test for the {@link UserRole} enum.
 *
 * @author Jose Uusitalo
 */
public class UserRoleTest
{
	private final UserRole administrator = UserRole.ADMINISTRATOR;
	private final UserRole manager = UserRole.MANAGER;
	private final UserRole logistician = UserRole.LOGISTICIAN;
	private final UserRole guest = UserRole.GUEST;

	/**
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws ParseException
	 */
	@BeforeClass
	public static final void loadSampleData() throws ParseException
	{
		DatabaseController.loadSampleData();
	}

	@Test
	public final void testGetName()
	{
		assertEquals("Manager", manager.getName());
	}

	@Test
	public final void testCompareTo_Administrator()
	{
		assertTrue(administrator.compareTo(guest) > 0);
		assertTrue(administrator.compareTo(logistician) > 0);
		assertTrue(administrator.compareTo(manager) > 0);
		assertEquals(0, administrator.compareTo(administrator));
	}

	@Test
	public final void testCompareTo_Manager()
	{
		assertTrue(manager.compareTo(guest) > 0);
		assertTrue(manager.compareTo(logistician) > 0);
		assertEquals(0, manager.compareTo(manager));
		assertTrue(manager.compareTo(administrator) < 0);
	}

	@Test
	public final void testCompareTo_Logistician()
	{
		assertTrue(logistician.compareTo(guest) > 0);
		assertEquals(0, logistician.compareTo(logistician));
		assertTrue(logistician.compareTo(administrator) < 0);
		assertTrue(logistician.compareTo(manager) < 0);
	}

	@Test
	public final void testToString()
	{
		assertEquals(administrator.getName(), administrator.toString());
		assertEquals(manager.getName(), manager.toString());
		assertEquals(logistician.getName(), logistician.toString());
		assertEquals(guest.getName(), guest.toString());
	}

}
