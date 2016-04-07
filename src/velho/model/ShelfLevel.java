package velho.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A horizontal level in a {@link Shelf} that contains one or more {@link ShelfSlot}s.
 *
 * @author Jose Uusitalo
 */
public class ShelfLevel implements Comparable<ShelfLevel>
{
	private int databaseID;

	private int shelfPosition;

	private int maxShelfSlots;

	private Set<ShelfSlot> shelfSlots;

	private Shelf parentShelf;

	/**
	 * @param databaseID
	 * @param shelfPosition
	 * @param maxShelfSlots
	 * @param shelfSlots
	 */
	public ShelfLevel(final int databaseID, final int shelfPosition, final int maxShelfSlots)
	{
		this.databaseID = databaseID;
		this.shelfPosition = shelfPosition;
		this.maxShelfSlots = maxShelfSlots;
		this.shelfSlots = new TreeSet<ShelfSlot>();
	}

	/**
	 * @param shelfPosition
	 * @param maxShelfSlots
	 * @param shelfSlots
	 */
	public ShelfLevel(final int shelfPosition, final int maxShelfSlots)
	{
		this(0, shelfPosition, maxShelfSlots);
	}

	public ShelfLevel()
	{
		// For Hibernate.
	}

	@Override
	public String toString()
	{
		return "ShelfLevel [" + databaseID + "]";
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ShelfLevel))
			return false;

		final ShelfLevel sl = (ShelfLevel) o;

		return this.getDatabaseID() == sl.getDatabaseID();
	}

	@Override
	public int compareTo(final ShelfLevel level)
	{
		return getDatabaseID() - level.getDatabaseID();
	}

	/**
	 * @return the databaseID
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * @param databaseID the databaseID to set
	 */
	public void setDatabaseID(final int databaseID)
	{
		this.databaseID = databaseID;
	}

	/**
	 * @return the shelfPosition
	 */
	public int getShelfPosition()
	{
		return shelfPosition;
	}

	/**
	 * @param shelfPosition the shelfPosition to set
	 */
	public void setShelfPosition(final int shelfPosition)
	{
		this.shelfPosition = shelfPosition;
	}

	/**
	 * @return the maxShelfSlots
	 */
	public int getMaxShelfSlots()
	{
		return maxShelfSlots;
	}

	/**
	 * @param maxShelfSlots the maxShelfSlots to set
	 */
	public void setMaxShelfSlots(final int maxShelfSlots)
	{
		this.maxShelfSlots = maxShelfSlots;
	}

	/**
	 * @return the slots
	 */
	public Set<ShelfSlot> getShelfSlots()
	{
		return shelfSlots;
	}

	/**
	 * @param slots the slots to set
	 */
	public void setShelfSlots(final Set<ShelfSlot> shelfSlots)
	{
		this.shelfSlots = shelfSlots;
	}

	public boolean addToSlot(final int slotPosition, final ProductBox productBox)
	{
		for (final ShelfSlot slot : shelfSlots)
		{
			if (slot.getLevelPosition() == slotPosition)
				return slot.addBox(productBox);
		}

		return false;
	}

	public ShelfSlot getShelfSlot(final int slotPosition)
	{
		for (final ShelfSlot slot : shelfSlots)
		{
			if (slot.getLevelPosition() == slotPosition)
				return slot;
		}

		return null;
	}

	public List<ShelfSlot> getFreeShelfSlots()
	{
		final List<ShelfSlot> freeSlots = new ArrayList<ShelfSlot>();

		for (final ShelfSlot slot : shelfSlots)
		{
			if (slot.hasFreeSpace())
				freeSlots.add(slot);
		}

		return freeSlots;
	}

	public int getProductCountInBoxes()
	{
		int sum = 0;

		for (final ShelfSlot slot : shelfSlots)
			sum += slot.getProductCountInBoxes();

		return sum;
	}

	public List<ProductBox> getProductBoxes()
	{
		final List<ProductBox> boxes = new ArrayList<ProductBox>();

		for (final ShelfSlot slot : shelfSlots)
			boxes.addAll(slot.getProductBoxes());

		return boxes;
	}

	public Shelf getParentShelf()
	{
		return parentShelf;
	}

	public void setParentShelf(final Shelf parentShelf)
	{
		this.parentShelf = parentShelf;
	}
}
