package velho.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Shelf Slot represents an indexed area with {@link ProductBox} objects on a {@link ShelfLevel}.
 *
 * @author Jose Uusitalo
 */
public class ShelfSlot implements Comparable<ShelfSlot>
{
	/**
	 * A separator string between values in shelf slot IDs.
	 */
	public static final String ID_SEPARATOR = "-";

	/**
	 * The database ID of this shelf slot.
	 */
	private int databaseID;

	/**
	 * The position (greater than 1) of this shelf slot on a level.
	 */
	private int levelPosition;

	/**
	 * The maximum number of product boxes this slot can contain.
	 */
	private int maxBoxCount;

	/**
	 * The contents of this shelf slot.
	 */
	private Set<ProductBox> boxes;

	private ShelfLevel parentLevel;

	private Shelf parentShelf;

	/**
	 * @param shelfLevelNumber must be greater than 0
	 * @param slotIndexInLevel
	 * @param maxBoxesInSlot must be greater than 0
	 */
	public ShelfSlot(final Shelf parentShelf, final ShelfLevel parentLevel, final int levelPosition, final int maxBoxesInSlot)
	{
		this.parentShelf = parentShelf;
		this.parentLevel = parentLevel;

		if (maxBoxesInSlot < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Maxmimum ProductBox count must be greater than 0, was " + maxBoxesInSlot + ".");
		if (levelPosition < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Level position must be greater than 0, was " + levelPosition + ".");

		this.maxBoxCount = maxBoxesInSlot;

		boxes = new HashSet<ProductBox>();
	}

	@Override
	public String toString()
	{
		return parentShelf.getDatabaseID() + ID_SEPARATOR + parentLevel.getDatabaseID() + ID_SEPARATOR + levelPosition;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ShelfSlot))
			return false;

		final ShelfSlot ss = (ShelfSlot) o;

		return this.getSlotID().equals(ss.getSlotID());
	}

	@Override
	public int compareTo(final ShelfSlot slot)
	{
		return getSlotID().compareToIgnoreCase(slot.getSlotID());
	}

	/**
	 * Gets the database ID of this shelf slot.
	 *
	 * @return the database ID of this shelf slot
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * The ID of this shelf slot in the following format:
	 * <code>&lt;shelf id&gt;-&lt;slot level in shelf&gt;-&lt;slot index in shelf&gt;</code>
	 *
	 * @return the ID of this shelf slot
	 */
	public String getSlotID()
	{
		return parentShelf.getDatabaseID() + ID_SEPARATOR + parentLevel.getDatabaseID() + ID_SEPARATOR + levelPosition;
	}

	public Set<ProductBox> getBoxes()
	{
		return boxes;
	}

	/**
	 * Iterates through the set and counts the number of products in the {@link ProductBox}es.
	 *
	 * @return the number of products in this shelf slot
	 */
	public int getProductCount()
	{
		final Iterator<ProductBox> it = boxes.iterator();

		int sum = 0;

		while (it.hasNext())
			sum += it.next().getProductCount();

		return sum;
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

		if (boxes.add(box))
		{
			box.setShelfSlot(this);
			return true;
		}

		return false;
	}

	/**
	 * Removes the given {@link ProductBox} from this shelf slot.
	 *
	 * @param box box to remove
	 * @return <code>true</code> if the box was removed, <code>false</code> if the box was not present
	 */
	public boolean removeBox(final ProductBox box)
	{
		if (boxes.remove(box))
		{
			box.setShelfSlot(null);
			return true;
		}

		return false;
	}

	public boolean contains(final ProductBox box)
	{
		return boxes.contains(box);
	}
}
