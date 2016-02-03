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
	public final void testCompareTo()
	{
		assertEquals(1, administrator.compareTo(manager));
		assertEquals(1, administrator.compareTo(logistician));
		assertEquals(1, manager.compareTo(logistician));

		assertEquals(0, administrator.compareTo(administrator));
		assertEquals(0, manager.compareTo(manager));
		assertEquals(0, logistician.compareTo(logistician));

		assertEquals(-1, manager.compareTo(administrator));
		assertEquals(-1, logistician.compareTo(administrator));
		assertEquals(-1, logistician.compareTo(manager));
	}

}
