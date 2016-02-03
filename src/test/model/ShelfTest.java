package test.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import velho.model.Shelf;

/**
 * Tests for the {@link Shelf} classhelf.
 *
 * @author Jose Uusitalo
 */
public class ShelfTest
{
	private int ID = 0;
	private int LEVELS = 3;
	private int SLOTS = 16;
	private int BOXES_PER_SLOT = 32;

	private Shelf emptyShelf;
	private Shelf shelf;

	@Before
	public final void createShelf()
	{
		// Create a new shelf before each test.
		emptyShelf = new Shelf(ID, LEVELS, SLOTS, BOXES_PER_SLOT);
	}

	@Test
	public final void testGetShelfID_Empty()
	{
		assertEquals(ID, emptyShelf.getShelfID());
	}

	@Test
	public final void testGetLevelCount_Empty()
	{
		assertEquals(LEVELS, emptyShelf.getLevelCount());
	}

	@Test
	public final void testGetShelfSlotCount_Empty()
	{
		assertEquals(LEVELS * SLOTS, emptyShelf.getShelfSlotCount());
	}

	@Test
	public final void testGetFreeShelfSlots_Empty()
	{
		assertEquals(emptyShelf.getShelfSlotCount(), emptyShelf.getFreeShelfSlots().size());
	}

	@Test
	public final void testGetLevels_Empty()
	{
		assertEquals(LEVELS, emptyShelf.getLevels().size());
	}

	@Test
	public final void testGetProductBoxCount_Empty()
	{
		assertEquals(0, emptyShelf.getProductBoxCount());
	}

	@Test
	public final void testGetProductCount_Empty()
	{
		assertEquals(0, emptyShelf.getProductCount());
	}

	@Test
	public final void testIsEmpty_Empty()
	{
		assertTrue(emptyShelf.isEmpty());
	}

	@Test
	public final void testHasFreeSpace_Empty()
	{
		assertTrue(emptyShelf.hasFreeSpace());
	}
}
