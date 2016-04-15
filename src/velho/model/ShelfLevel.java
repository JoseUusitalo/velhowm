package velho.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.directory.InvalidAttributesException;

import org.apache.log4j.Logger;

/**
 * A horizontal level in a {@link Shelf} that contains one or more {@link ShelfSlot}s.
 *
 * @author Jose Uusitalo
 */
public class ShelfLevel extends AbstractDatabaseObject implements Comparable<ShelfLevel>
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ShelfLevel.class.getName());

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
	public ShelfLevel(final int databaseID, final Shelf parentShelf, final int shelfPosition, final int maxShelfSlots)
	{
		setDatabaseID(databaseID);
		this.parentShelf = parentShelf;
		this.shelfPosition = shelfPosition;
		this.maxShelfSlots = maxShelfSlots;
		this.shelfSlots = new TreeSet<ShelfSlot>();
	}

	public ShelfLevel()
	{
		// For Hibernate.
		this.shelfSlots = new TreeSet<ShelfSlot>();
	}

	@Override
	public String toString()
	{
		return parentShelf.getShelfID() + ShelfSlot.ID_SEPARATOR + shelfPosition;
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

	public boolean addToSlot(final int slotPosition, final ProductBox productBox) throws InvalidAttributesException
	{
		SYSLOG.trace("Adding product box " + productBox + " to shelf level slot at position " + slotPosition + " in the shelf level: " + toString());

		if (shelfSlots.isEmpty())
			throw new InvalidAttributesException("Shelf level must contain at least one shelf slot.");

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
			{
				SYSLOG.trace("Slot: " + slot);
				return slot;
			}
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
