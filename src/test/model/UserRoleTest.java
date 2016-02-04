package test.model;

import static org.junit.Assert.*;

import org.junit.Test;

import velho.model.Administrator;
import velho.model.Logistician;
import velho.model.Manager;
import velho.model.interfaces.UserRole;

public class UserRoleTest
{
	private UserRole administrator = new Administrator();
	private UserRole manager = new Manager();
	private UserRole logistician = new Logistician();

	@Test
	public final void testGetName()
	{
		assertEquals("Manager", manager.getName());
	}

	@Test
	public final void testCompareTo_Administrator()
	{
		assertEquals(1, administrator.compareTo(manager));
		assertEquals(1, administrator.compareTo(logistician));
		assertEquals(0, administrator.compareTo(administrator));
	}
	
	@Test
	public final void testCompareTo_Manager()
	{
		assertEquals(1, manager.compareTo(logistician));
		assertEquals(0, manager.compareTo(manager));
		assertEquals(-1, manager.compareTo(administrator));
	}

	@Test
	public final void testCompareTo_Logistician()
	{
		assertEquals(0, logistician.compareTo(logistician));
		assertEquals(-1, logistician.compareTo(administrator));
		assertEquals(-1, logistician.compareTo(manager));
	}

	@Test (expected=IllegalArgumentException.class)
	public final void testCompareTo_Administrator_Null()
	{
		administrator.compareTo(null);
	}

	@Test (expected=IllegalArgumentException.class)
	public final void testCompareTo_Manager_Null()
	{
		manager.compareTo(null);
	}

	@Test (expected=IllegalArgumentException.class)
	public final void testCompareTo_Logistician_Null()
	{
		logistician.compareTo(null);
	}
	@Test
	public final void testToString()
	{
		assertEquals(administrator.getName(), administrator.toString());
		assertEquals(manager.getName(), manager.toString());
		assertEquals(logistician.getName(), logistician.toString());
	}

}
