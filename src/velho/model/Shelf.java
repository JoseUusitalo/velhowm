package velho.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class representing a physical shelf structure in the warehouse.
 *
 * @author Jose Uusitalo
 */
public class Shelf
{
	/**
	 * The next free shelf ID in the warehouse.
	 */
	private static int nextFreeShelfID = 0;

	/**
	 * The identifier of a shelf in IDs.
	 */
	private static final String SHELF_IDENTIFIER = "S";

	/**
	 * The ID of this shelf.
	 */
	private int shelfID;

	/**
	 * The array of shelf slots for each level of the shelf.
	 */
	private ShelfSlot[][] slots;

	/**
	 * Converts the given slot ID into an array of integers where:
	 * <ul>
	 * <li>Index 0: is the shelf ID</li>
	 * <li>Index 1: is the level in the shelf</li>
	 * <li>Index 2: is the slot ID in the shelf</li>
	 * </ul>
	 * @param slotID
	 * @return an array of integers or an empty array if the given shelf slot ID is invalid
	 */
	public static int[] shelfSlotIDTokenizer(final String shelfSlotID)
	{
		if (shelfSlotID.charAt(0) != 'S')
			return new int[0];

		// Remove the S from front.
		String[] stringTokens = shelfSlotID.substring(1).split(ShelfSlot.ID_SEPARATOR);

		int[] tokens = new int[3];

		for (int i = 0; i < 3; i++)
		{
			try
			{
				tokens[i] = Integer.parseInt(stringTokens[i]);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException("Invalid slot ID '" + shelfSlotID + "'.");
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

		this.slots = new ShelfSlot[levels][slotsPerLevel];

		// For every level.
		for (int level = 0; level < levels; level++)
		{
			// Create new slots.
			for (int index = 0; index < slotsPerLevel; index++)
			{
				ShelfSlot slot = new ShelfSlot(level, index, maxBoxesPerSlot);
				slots[level][index] = slot;
			}
		}
	}

	/**
	 * Gets the shelf ID.
	 *
	 * @return the shelf ID
	 */
	public String getShelfID()
	{
		return SHELF_IDENTIFIER + shelfID;
	}

	/**
	 * Gets the number of shelf slots on this shelf.
	 *
	 * @return the number of slots on this shelf
	 */
	public int getShelfSlotCount()
	{
		return slots.length * slots[0].length;
	}

	/**
	 * Gets the number of levels on this shelf.
	 *
	 * @return the number of levels of this shelf
	 */
	public int getLevels()
	{
		return slots.length;
	}

	/**
	 * Gets all shelf slots on this shelf that have some free space.
	 *
	 * @return shelf slots with free space of this shelf
	 */
	public TreeSet<String> getFreeShelfSlots()
	{
		TreeSet<String> freeSlots = new TreeSet<String>();
		final int levels = slots.length;
		final int slotCount = slots[0].length;

		for (int level = 0; level < levels; level++)
		{
			for (int index = 0; index < slotCount; index++)
			{
				if (slots[level][index].hasFreeSpace())
					freeSlots.add(slots[level][index].getSlotID());
			}
		}

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
		final int levels = slots.length;
		final int slotCount = slots[0].length;

		for (int level = 0; level < levels; level++)
		{
			for (int index = 0; index < slotCount; index++)
			{
				sum += slots[level][index].getProductCount();
			}
		}

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
		final int levels = slots.length;
		final int slotCount = slots[0].length;

		for (int level = 0; level < levels; level++)
		{
			for (int index = 0; index < slotCount; index++)
			{
				sum += slots[level][index].getProductBoxCount();
			}
		}

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
		int[] tokens = Shelf.shelfSlotIDTokenizer(shelfSlotID);
		return slots[tokens[1]][tokens[2]].boxes;
	}

	/**
	 * Attempts to add the given {@link ProductBox} into the {@link ShelfSlot} specified by the slot ID.
	 *
	 * @param shelfSlotID ID of the shelf slot
	 * @param box box to add
	 * @return <code>true</code> if box was added to the slot, <code>false</code> if the slot ID is not in this shelf,
	 * or the slot did not have enough free space
	 */
	public boolean addToSlot(final String shelfSlotID, final ProductBox productBox)
	{
		int[] tokens = Shelf.shelfSlotIDTokenizer(shelfSlotID);

		// Was a shelf slot ID given?
		if (tokens.length == 0)
			return false;

		// Correct shelf?
		if (shelfID != tokens[0])
			return false;

		// Enough levels?
		if (slots.length < tokens[1] - 1)
			return false;

		// Enough slots?
		if (slots[0].length < tokens[2])
			return false;

		return slots[tokens[1]][tokens[2]].addBox(productBox);
	}

	/**
	 * Attempts to remove the given {@link ProductBox} from the {@link ShelfSlot} specified by the slot ID.
	 *
	 * @param shelfSlotID ID of the shelf slot
	 * @param box box to remove
	 * @return <code>true</code> if box was added to the slot, <code>false</code> if the slot ID is not in this shelf,
	 * or the slot did not have the specified box
	 */
	public boolean removeFromSlot(final String shelfSlotID, final ProductBox productBox)
	{
		int[] tokens = Shelf.shelfSlotIDTokenizer(shelfSlotID);

		// Correct shelf?
		if (shelfID != tokens[0])
			return false;

		// Enough levels?
		if (slots.length < tokens[1] - 1)
			return false;

		// Enough slots?
		if (slots[0].length < tokens[2])
			return false;

		return slots[tokens[1]][tokens[2]].removeBox(productBox);
	}

	/*
	 * ---------------- SHELF SLOT ----------------
	 */

	/**
	 * A Shelf Slot represents an indexed area with {@link ProductBox} objects on a {@link Shelf}.
	 *
	 * @author Jose Uusitalo
	 */
	class ShelfSlot implements Comparable<ShelfSlot>
	{
		public static final String ID_SEPARATOR = "-";

		/**
		 * The maximum number of product boxes this slot can contain.
		 */
		private int maxBoxCount;

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
			// @formatter:off
			// Zero pad shelf level and index.
			this.shelfSlotID = Shelf.SHELF_IDENTIFIER + shelfID + ID_SEPARATOR
					+ String.format("%0" + String.valueOf(slots.length).length() + "d", shelfLevel) + ID_SEPARATOR
					+ String.format("%0" + String.valueOf(slots[0].length).length() + "d", indexInLevel);
			// @formatter:on
			if (maxBoxCount < 1)
				throw new IllegalArgumentException("Maxmimum ProductBox count must be greater than 0.");

			this.maxBoxCount = maxBoxCount;

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
