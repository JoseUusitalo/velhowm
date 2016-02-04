package velho.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class representing a physical shelf structure in a warehouse.
 *
 * @author Jose Uusitalo
 */
public class Shelf
{
	private static int nextFreeShelfID = 0;

	/**
	 * The ID of thise shelf.
	 */
	private int shelfID;

	/**
	 * The levels of this shelf containing shelf slots.
	 */
	private Map<String, ShelfSlot> slots;

	/**
	 * The number of levels in this shelf.
	 */
	private int levels;

	/**
	 * Converts the given slot ID into an array of integers where:
	 * <ul>
	 * <li>Index 0: is the shelf ID</li>
	 * <li>Index 1: is the level in the shelf</li>
	 * <li>Index 2: is the slot ID in the shelf</li>
	 * </ul>
	 * @param slotID
	 * @return
	 */
	public static int[] slotIDTokenizer(final String slotID)
	{
		String[] stringTokens = slotID.split(ShelfSlot.ID_SEPARATOR);
		int[] tokens = new int[3];

		for (int i = 0; i < 3; i++)
		{
			try
			{
				tokens[i] = Integer.parseInt(stringTokens[i]);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException("Invalid slot ID '" + slotID + "'.");
			}
		}

		return tokens;
	}

	/**
	 * Automatically creates the shelf slots for this shelf as well.
	 *
	 * @param shelfID
	 * @param levels must be greater than 0
	 * @param slotsPerLevel must be greater than 0
	 * @param maxBoxesPerSlot must be greater than 0
	 */
	public Shelf(final int levels, final int slotsPerLevel, final int maxBoxesPerSlot)
	{
		this.shelfID = nextFreeShelfID++;

		if (levels < 1)
			throw new IllegalArgumentException("Number of levels on a shelf must be greater than 0.");
		if (slotsPerLevel < 1)
			throw new IllegalArgumentException("Number of shelf slots on a shelf level must be greater than 0.");

		this.levels = levels;
		this.slots = new TreeMap<String, ShelfSlot>();

		// For every level.
		for (int level = 0; level < levels; level++)
		{
			// Create new slots.
			for (int index = 0; index < slotsPerLevel; index++)
			{
				ShelfSlot slot = new ShelfSlot(level, index, maxBoxesPerSlot);
				slots.put(slot.getSlotID(), slot);
			}
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
	 * Gets all the shelf slots on this shelf.
	 *
	 * @return the slots of this shelf
	 */
	public Map<String, ShelfSlot> getShelfSlots()
	{
		return slots;
	}

	/**
	 * Gets the number of levels on this shelf.
	 *
	 * @return the number of levels of this shelf
	 */
	public int getLevels()
	{
		return levels;
	}

	/**
	 * Gets all shelf slots on this shelf that have some free space.
	 *
	 * @return shelf slots with free space of this shelf
	 */
	public TreeSet<String> getFreeShelfSlots()
	{
		TreeSet<String> freeSlots = new TreeSet<String>();

		for (final ShelfSlot slot : slots.values())
			if (slot.hasFreeSpace())
				freeSlots.add(slot.getSlotID());
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
		for (final ShelfSlot slot : slots.values())
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

		for (final ShelfSlot slot : slots.values())
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
		return getProductBoxCount() == 0;
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

	/**
	 * Gets the specified {@link ShelfSlot}.
	 *
	 * @param shelfSlotID the ID of the shelf slot to get
	 * @return the wanted shelf slot or <code>null</code> if shelf slow is not in this shelf
	 */
	public Set<ProductBox> getShelfSlotBoxes(final String shelfSlotID)
	{
		return slots.get(shelfSlotID).boxes;
	}

	/**
	 * Attempts to add the given {@link ProductBox} into the {@link ShelfSlot} specified by the slot ID.
	 *
	 * @param slotID shelf slot id
	 * @param box box to add
	 * @return <code>true</code> if box was added to the slot, <code>false</code> if the slot ID is not in this shelf,
	 * or the slot did not have enough free space
	 */
	public boolean addToSlot(final String slotID, final ProductBox box)
	{
		if (!slots.containsKey(slotID))
			return false;

		return slots.get(slotID).addBox(box);
	}

	/**
	 * Attempts to remove the given {@link ProductBox} from the {@link ShelfSlot} specified by the slot ID.
	 *
	 * @param slotID shelf slot ID
	 * @param box box to remove
	 * @return <code>true</code> if box was added to the slot, <code>false</code> if the slot ID is not in this shelf,
	 * or the slot did not have the specified box
	 */
	public boolean removeFromSlot(final String slotID, final ProductBox box)
	{
		if (!slots.containsKey(slotID))
			return false;

		return slots.get(slotID).removeBox(box);
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
		public static final String ID_SEPARATOR = "-";

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
		 * The ID of parent shelf this slot belongs to.
		 */
		private int parentShelfID;

		/**
		 * The contents of this shelf slot.
		 */
		private Set<ProductBox> boxes;

		/**
		 * The ID of this shelf slot.
		 */
		private String shelfSlotID;

		/**
		 * @param parent
		 * @param level
		 * @param indexInLevel
		 * @param maxBoxCount must be greater than 0
		 */
		private ShelfSlot(final int shelfLevel, final int indexInLevel, final int maxBoxCount)
		{
			this.shelfSlotID = shelfID + ID_SEPARATOR + shelfLevel + ID_SEPARATOR + indexInLevel;

			if (maxBoxCount < 1)
				throw new IllegalArgumentException("Maxmimum ProductBox count must be greater than 0.");

			this.maxBoxCount = maxBoxCount;

			/*
			 * TODO: Find a way to implements stacks.
			 * The stack should go from "front to back" because you can't take out boxes that are behind other boxes.
			 */
			boxes = new HashSet<ProductBox>();
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
			return shelfSlotID;
		}

		/**
		 * The ID of the shelf this shelf slot is in.
		 *
		 * @return the ID of the parent shelf
		 */
		public int getShelfID()
		{
			return shelfID;
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
			Iterator<ProductBox> it = boxes.iterator();

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
		public Set<ProductBox> getBoxes()
		{
			return boxes;
		}

		/**
		 * Gets the number of {@link ProductBox}es in this shelf slot.
		 *
		 * @return the number of product boxes in this shelf slot
		 */
		public int getProductBoxCount()
		{
			return boxes.size();
		}

		/**
		 * Checks if this shelf slot has free space.
		 *
		 * @return <code>true</code> if this shelf slot has free space
		 */
		public boolean hasFreeSpace()
		{
			return (boxes.size() != maxBoxCount);
		}

		/**
		 * Attempts to add the given {@link ProductBox} to this shelf slot.
		 *
		 * @param box box to add
		 * @return <code>true</code> if the box was added, <code>false</code> if there wasn't enough space
		 */
		public boolean addBox(final ProductBox box)
		{
			if (boxes.size() + 1 > maxBoxCount)
				return false;

			return boxes.add(box);
		}

		/**
		 * Attempts to remove the given {@link ProductBox} to this shelf slot.
		 *
		 * @param box box to remove
		 * @return <code>true</code> if the box was removed, <code>false</code> if the box was not present
		 */
		public boolean removeBox(final ProductBox box)
		{
			if (!boxes.contains(box))
				return false;

			return boxes.remove(box);
		}
	}
}
