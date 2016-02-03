package velho.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * A class representing a physical shelf structure in a warehouse.
 *
 * @author Jose Uusitalo
 */
public class Shelf
{
	/**
	 * The ID of thise shelf.
	 */
	private int shelfID;

	/**
	 * The levels of this shelf containing shelf slots.
	 */
	private List<Set<ShelfSlot>> levels;

	/**
	 * Automatically creates the shelf slots for this shelf as well.
	 *
	 * @param shelfID
	 * @param levels must be greater than 0
	 * @param slotsPerLevel must be greater than 0
	 * @param maxBoxesPerSlot must be greater than 0
	 */
	public Shelf(final int shelfID, final int levels, final int slotsPerLevel, final int maxBoxesPerSlot)
	{
		this.shelfID = shelfID;

		if (levels < 1)
			throw new IllegalArgumentException("Number of levels on a shelf must be greater than 0.");
		if (slotsPerLevel < 1)
			throw new IllegalArgumentException("Number of shelf slots on a shelf level must be greater than 0.");

		this.levels = new ArrayList<Set<ShelfSlot>>();

		// Create levels.
		for (int level = 0; level < levels; level++)
		{
			// Each level is a TreeSet.
			Set<ShelfSlot> currentLevel = new TreeSet<ShelfSlot>();

			// On every level create new slots.
			for (int index = 0; index < slotsPerLevel; index++)
				currentLevel.add(new ShelfSlot(this, level + 1, index, maxBoxesPerSlot));

			this.levels.add(currentLevel);
		}
	}

	/**
	 * Gets the shelf ID.
	 *
	 * @return the shelf ID
	 */
	public int getShelfID()
	{
		return shelfID;
	}

	/**
	 * Gets all the levels of shelf slots on this shelf.
	 *
	 * @return the levels of this shelf
	 */
	public List<Set<ShelfSlot>> getLevels()
	{
		return levels;
	}

	/**
	 * Gets the number of levels on this shelf.
	 *
	 * @return the number of levels of this shelf
	 */
	public int getLevelCount()
	{
		return levels.size();
	}

	/**
	 * Counts the number of shelf slots on this shelf.
	 *
	 * @return the number of shelf slots of this shelf
	 */
	public int getShelfSlotCount()
	{
		int sum = 0;
		for (final Set<ShelfSlot> slots : levels)
			sum += slots.size();
		return sum;
	}

	/**
	 * Gets all shelf slots on this shelf that have some free space.
	 *
	 * @return shelf slots with free space of this shelf
	 */
	public List<ShelfSlot> getFreeShelfSlots()
	{
		List<ShelfSlot> freeSlots = new ArrayList<ShelfSlot>();

		for (final Set<ShelfSlot> slots : levels)
			for (final ShelfSlot slot : slots)
				if (slot.hasFreeSpace())
					freeSlots.add(slot);
		return freeSlots;
	}

	/**
	 * Counts the number of products on this shelf.
	 *
	 * @return the number of products of this shelf
	 */
	public int getProductCount()
	{
		int sum = 0;
		for (final Set<ShelfSlot> slots : levels)
			for (final ShelfSlot slot : slots)
				sum += slot.getProductCount();
		return sum;
	}

	/**
	 * Gets the number of product boxes on this shelf.
	 *
	 * @return the number of product boxes of this shelf
	 */
	public int getProductBoxCount()
	{
		int sum = 0;

		for (final Set<ShelfSlot> slots : levels)
			for (final ShelfSlot slot : slots)
				sum += slot.getProductBoxCount();
		return sum;
	}

	/**
	 * Checks if this shelf is empty.
	 *
	 * @return <code>true</code> if this shelf is empty
	 */
	public boolean isEmpty()
	{
		return getFreeShelfSlots().size() == getShelfSlotCount();
	}

	/**
	 * Checks if this shelf has free space on it.
	 *
	 * @return <code>true</code> if this shelf has empty shelf slots
	 */
	public boolean hasFreeSpace()
	{
		return getFreeShelfSlots().size() != 0;
	}

	/*
	 * ---------------- SHELF SLOT ----------------
	 */

	/**
	 * An object representing an area with {@link ProductBox} objects on a {@link Shelf}.
	 *
	 * @author Jose Uusitalo
	 */
	class ShelfSlot implements Comparable<ShelfSlot>
	{
		/**
		 * The index of this shelf slot in a shelf level.
		 */
		private int shelfLevelIndex;

		/**
		 * The shelf level this shelf slot is on.
		 */
		private int shelfLevel;

		/**
		 * The maximum number of product boxes this slot can contain.
		 */
		private int maxBoxCount;

		/**
		 * The parent shelf this slot belongs to.
		 */
		private Shelf parent;

		/**
		 * The contents of this shelf slot.
		 */
		private Stack<ProductBox> stack;

		/**
		 * @param parent
		 * @param level
		 * @param indexInLevel
		 * @param maxBoxCount must be greater than 0
		 */
		private ShelfSlot(final Shelf parent, final int shelfLevel, final int indexInLevel, final int maxBoxCount)
		{
			this.parent = parent;
			this.shelfLevel = shelfLevel;
			this.shelfLevelIndex = indexInLevel;

			if (maxBoxCount < 1)
				throw new IllegalArgumentException("Maxmimum ProductBox count must be greater than 0.");

			this.maxBoxCount = maxBoxCount;

			// The stack goes from "front to back" because you can't take out boxes that are behind other boxes.
			stack = new Stack<ProductBox>();
		}

		/**
		 * Compares the ID of this shelf slot to the given one.
		 */
		@Override
		public int compareTo(final ShelfSlot s)
		{
			return getSlotID().compareToIgnoreCase(s.getSlotID());
		}

		/**
		 * The ID of this shelf slot in the following format:
		 * <code>&lt;shelf id&gt;-&lt;slot level in shelf&gt;-&lt;slot index in shelf&gt;</code>
		 *
		 * @return the ID of this shelf slot
		 */
		public String getSlotID()
		{
			return parent.shelfID + "-" + shelfLevel + "-" + shelfLevelIndex;
		}

		/**
		 * Gets the maximum number of boxes this shelf slot can contain.
		 *
		 * @return the maximum number of boxes this shelf slot can contain
		 */
		public int getMaxBoxCount()
		{
			return maxBoxCount;
		}

		/**
		 * Iterates through the stack and counts the number of products in the {@link ProductBox}es.
		 *
		 * @return the number of products in this shelf slot
		 */
		public int getProductCount()
		{
			Iterator<ProductBox> it = stack.iterator();

			int sum = 0;

			while (it.hasNext())
				sum += it.next().getProductCount();

			return sum;
		}

		/**
		 * Gets the stack of {@link ProductBox}es in this shelf slot.
		 *
		 * @return the stack of boxes
		 */
		public Stack<ProductBox> getStack()
		{
			return stack;
		}

		/**
		 * Gets the number of {@link ProductBox}es in this shelf slot.
		 *
		 * @return the number of product boxes in this shelf slot
		 */
		public int getProductBoxCount()
		{
			return stack.size();
		}

		/**
		 * Gets the parent {@link Shelf} of this shelf slot.
		 *
		 * @return parent shelf
		 */
		public Shelf getParentShelf()
		{
			return parent;
		}

		/**
		 * Checks if this shelf slot has free space.
		 *
		 * @return <code>true</code> if this shelf slot has free space
		 */
		public boolean hasFreeSpace()
		{
			return (stack.size() != maxBoxCount);
		}
	}
}
