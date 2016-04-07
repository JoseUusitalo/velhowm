package velho.model;

import java.util.List;

public class ShelfLevel implements Comparable<ShelfLevel>
{
	private int databaseID;

	private int levelPosition;

	private int maxProductBoxes;

	private List<ShelfSlot> slots;

	/**
	 * @param databaseID
	 * @param levelPosition
	 * @param maxProductBoxes
	 * @param slots
	 */
	public ShelfLevel(final int databaseID, final int levelPosition, final int maxProductBoxes, final List<ShelfSlot> slots)
	{
		this.databaseID = databaseID;
		this.levelPosition = levelPosition;
		this.maxProductBoxes = maxProductBoxes;
		this.slots = slots;
	}

	public ShelfLevel(final int levelPosition, final int maxProductBoxes, final List<ShelfSlot> slots)
	{
		this(0, levelPosition, maxProductBoxes, slots);
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
	 * @return the levelPosition
	 */
	public int getLevelPosition()
	{
		return levelPosition;
	}

	/**
	 * @return the maxProductBoxes
	 */
	public int getMaxProductBoxes()
	{
		return maxProductBoxes;
	}

	/**
	 * @return the slots
	 */
	public List<ShelfSlot> getSlots()
	{
		return slots;
	}

	/**
	 * @param databaseID the databaseID to set
	 */
	public void setDatabaseID(final int databaseID)
	{
		this.databaseID = databaseID;
	}

	/**
	 * @param levelPosition the levelPosition to set
	 */
	public void setLevelPosition(final int levelPosition)
	{
		this.levelPosition = levelPosition;
	}

	/**
	 * @param maxProductBoxes the maxProductBoxes to set
	 */
	public void setMaxProductBoxes(final int maxProductBoxes)
	{
		this.maxProductBoxes = maxProductBoxes;
	}

	/**
	 * @param slots the slots to set
	 */
	public void setSlots(final List<ShelfSlot> slots)
	{
		this.slots = slots;
	}

	public boolean addToSlot(final int slotPosition, final ProductBox productBox)
	{
		return slots.get(slotPosition).addBox(productBox);
	}

	public ShelfSlot getShelfSlot(final int slotPosition)
	{
		return slots.get(slotPosition);
	}
}
