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
	 * The position (greater than 1) of this shelf slot in a level.
	 */
	private int levelPosition;

	/**
	 * The maximum number of product boxes this slot can contain.
	 */
	private int maxProductBoxes;

	/**
	 * The contents of this shelf slot.
	 */
	private Set<ProductBox> productBoxes;

	private ShelfLevel parentShelfLevel;

	/**
	 * @param databaseID
	 * @param parentShelfLevel
	 * @param levelPosition
	 * @param maxBoxesInSlot
	 */
	public ShelfSlot(final int databaseID, final int levelPosition, final int maxBoxesInSlot, final ShelfLevel parentShelfLevel)
	{
		this.databaseID = databaseID;
		this.parentShelfLevel = parentShelfLevel;

		if (maxBoxesInSlot < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Maxmimum ProductBox count must be greater than 0, was " + maxBoxesInSlot + ".");
		if (levelPosition < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Level position must be greater than 0, was " + levelPosition + ".");

		this.maxProductBoxes = maxBoxesInSlot;

		productBoxes = new HashSet<ProductBox>();
	}

	public ShelfSlot(final ShelfLevel parentShelfLevel, final int levelPosition, final int maxBoxesInSlot)
	{
		this(0, levelPosition, maxBoxesInSlot, parentShelfLevel);
	}

	public ShelfSlot()
	{
		// For Hibernate.
	}

	@Override
	public String toString()
	{
		return parentShelfLevel.getParentShelf().getShelfID() + ID_SEPARATOR + parentShelfLevel.getShelfPosition() + ID_SEPARATOR + levelPosition;
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

	public void setDatabaseID(final int databaseID)
	{
		this.databaseID = databaseID;
	}

	public int getLevelPosition()
	{
		return levelPosition;
	}

	public void setLevelPosition(final int levelPosition)
	{
		this.levelPosition = levelPosition;
	}

	public int getMaxProductBoxes()
	{
		return maxProductBoxes;
	}

	public void setMaxProductBoxes(final int maxProductBoxes)
	{
		this.maxProductBoxes = maxProductBoxes;
	}

	/**
	 * The ID of this shelf slot in the following format:
	 * <code>&lt;shelf id&gt;-&lt;slot level in shelf&gt;-&lt;slot index in shelf&gt;</code>
	 *
	 * @return the ID of this shelf slot
	 */
	public String getSlotID()
	{
		return parentShelfLevel.getParentShelf().getShelfID() + ID_SEPARATOR + parentShelfLevel.getShelfPosition() + ID_SEPARATOR + levelPosition;
	}

	public Set<ProductBox> getProductBoxes()
	{
		return productBoxes;
	}

	public void setProductBoxes(final Set<ProductBox> productBoxes)
	{
		this.productBoxes = productBoxes;
	}

	/**
	 * Iterates through the set and counts the number of products in the {@link ProductBox}es.
	 *
	 * @return the number of products in this shelf slot
	 */
	public int getProductCountInBoxes()
	{
		final Iterator<ProductBox> it = productBoxes.iterator();

		int sum = 0;

		while (it.hasNext())
			sum += it.next().getProductCount();

		return sum;
	}

	/**
	 * Checks if this shelf slot has free space.
	 *
	 * @return <code>true</code> if this shelf slot has free space
	 */
	public boolean hasFreeSpace()
	{
		return (productBoxes.size() < maxProductBoxes);
	}

	/**
	 * Attempts to add the given {@link ProductBox} to this shelf slot.
	 *
	 * @param box box to add
	 * @return <code>true</code> if the box was added, <code>false</code> if there wasn't enough space
	 */
	public boolean addBox(final ProductBox box)
	{
		if (productBoxes.size() + 1 > maxProductBoxes)
			return false;

		if (productBoxes.add(box))
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
		if (productBoxes.remove(box))
		{
			box.setShelfSlot(null);
			return true;
		}

		return false;
	}

	public boolean contains(final ProductBox box)
	{
		return productBoxes.contains(box);
	}

	public ShelfLevel getParentShelfLevel()
	{
		return parentShelfLevel;
	}

	public void setParentShelfLevel(final ShelfLevel parentShelfLevel)
	{
		this.parentShelfLevel = parentShelfLevel;
	}
}
