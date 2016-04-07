package velho;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import velho.model.ProductBox;
import velho.model.Shelf;

/**
 * A Shelf Slot represents an indexed area with {@link ProductBox} objects on a {@link Shelf}.
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
	 * The maximum number of product boxes this slot can contain.
	 */
	private int maxBoxCount;

	/**
	 * The contents of this shelf slot.
	 */
	private Set<ProductBox> boxes;

	/**
	 * The ID string of this shelf slot.
	 */
	private String databaseID;

	/**
	 * @param shelfLevelNumber must be greater than 0
	 * @param slotIndexInLevel
	 * @param maxBoxesInSlot must be greater than 0
	 */
	public ShelfSlot(final int shelfDatabaseID, final int shelfLevelNumber, final int slotIndexInLevel, final int maxBoxesInSlot)
	{
		if (maxBoxesInSlot < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Maxmimum ProductBox count must be greater than 0, was " + maxBoxesInSlot + ".");
		if (shelfLevelNumber < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Level number must be greater than 0, was " + shelfLevelNumber + ".");

		// @formatter:off
		this.databaseID = Shelf.SHELF_IDENTIFIER + shelfDatabaseID
						   + ID_SEPARATOR
						   + shelfLevelNumber
						   + ID_SEPARATOR
						   + slotIndexInLevel;
		// @formatter:on

		this.maxBoxCount = maxBoxesInSlot;

		boxes = new HashSet<ProductBox>();
	}

	@Override
	public String toString()
	{
		return databaseID;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ShelfSlot))
			return false;

		final ShelfSlot ss = (ShelfSlot) o;

		return this.getDatabaseID().equals(ss.getDatabaseID());
	}

	@Override
	public int compareTo(final ShelfSlot slot)
	{
		return getDatabaseID().compareToIgnoreCase(slot.getDatabaseID());
	}

	/**
	 * The ID of this shelf slot in the following format:
	 * <code>&lt;shelf id&gt;-&lt;slot level in shelf&gt;-&lt;slot index in shelf&gt;</code>
	 *
	 * @return the ID of this shelf slot
	 */
	public String getDatabaseID()
	{
		return databaseID;
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
			box.setShelfSlot(databaseID);
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
}
