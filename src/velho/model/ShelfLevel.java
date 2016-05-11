package velho.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.naming.directory.InvalidAttributesException;

import org.apache.log4j.Logger;

import velho.controller.database.DatabaseController;

/**
 * A horizontal level in a {@link Shelf} that contains one or more {@link ShelfSlot}s.
 *
 * @author Jose Uusitalo
 */
public class ShelfLevel extends AbstractDatabaseObject
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ShelfLevel.class.getName());

	/**
	 * The position of this shelf level in its parent shelf from bottom. (Minimum value is 1.)
	 */
	private int shelfPosition;

	/**
	 * The maximum number of {@link ShelfSlot} objects this shelf level can have.
	 */
	private int maxShelfSlots;

	/**
	 * The set of {@link ShelfSlot} objects in this shelf level.
	 */
	private Set<ShelfSlot> shelfSlots;

	/**
	 * The parent {@link Shelf} objects that this shelf level is a part of.
	 */
	private Shelf parentShelf;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param parentShelf
	 * @param shelfPosition
	 * @param maxShelfSlots
	 */
	@SuppressWarnings("unused")
	public ShelfLevel(final int databaseID, final UUID uuid, final Shelf parentShelf, final int shelfPosition, final int maxShelfSlots)
	{
		// Database ID left unused on purpose.
		setUuid(uuid);
		this.parentShelf = parentShelf;
		this.shelfPosition = shelfPosition;
		this.maxShelfSlots = maxShelfSlots;
		this.shelfSlots = new TreeSet<ShelfSlot>();
	}

	/**
	 * @param databaseID
	 * @param parentShelf
	 * @param shelfPosition
	 * @param maxShelfSlots
	 */
	public ShelfLevel(final int databaseID, final Shelf parentShelf, final int shelfPosition, final int maxShelfSlots)
	{
		this(databaseID, UUID.randomUUID(), parentShelf, shelfPosition, maxShelfSlots);
	}

	/**
	 */
	public ShelfLevel()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
		this.shelfSlots = new TreeSet<ShelfSlot>();
	}

	@Override
	public String toString()
	{
		return parentShelf.getShelfID() + ShelfSlot.ID_SEPARATOR + shelfPosition;
	}

	/**
	 * Gets the position of this shelf level in the parent shelf starting from the bottom.
	 * The minimum value is 1.
	 *
	 * @return the position of this shelf level in the parent shelf
	 */
	public int getShelfPosition()
	{
		return shelfPosition;
	}

	/**
	 * Changes the position of this shelf level in its parent {@link Shelf}.
	 *
	 * @param shelfPosition the new shelf position in range [1, {@link Integer#MAX_VALUE}]
	 */
	public void setShelfPosition(final int shelfPosition)
	{
		if (shelfPosition < 1)
			throw new IllegalArgumentException("Shelf position was less than 1.");

		this.shelfPosition = shelfPosition;
	}

	/**
	 * Gets the maximum number of {@link ShelfSlot} objects this shelf level can hold.
	 *
	 * @return the maximum number of shelf slots
	 */
	public int getMaxShelfSlots()
	{
		return maxShelfSlots;
	}

	/**
	 * Sets the maximum number of {@link ShelfSlot} objects this shelf level can hold.
	 *
	 * @param maxShelfSlots the new maximum number of shelf slots in the range [1, {@link Integer#MAX_VALUE}]
	 */
	public void setMaxShelfSlots(final int maxShelfSlots)
	{
		if (maxShelfSlots < 1)
			throw new IllegalArgumentException("Maximum shelf slots was less than 1.");

		this.maxShelfSlots = maxShelfSlots;
	}

	/**
	 * Gets the set of {@link ShelfSlot} objects in this shelf level.
	 *
	 * @return the shelf slots in this shelf level
	 */
	public Set<ShelfSlot> getShelfSlots()
	{
		return shelfSlots;
	}

	/**
	 * Sets the set of {@link ShelfSlot} objects in this shelf level.
	 *
	 * @param slots the new shelf slots, <code>null</code> to clear
	 */
	public void setShelfSlots(final Set<ShelfSlot> shelfSlots)
	{
		if (shelfSlots == null)
		{
			for (final ShelfSlot s : this.shelfSlots)
				s.setParentShelfLevel(null);

			this.shelfSlots.clear();
		}
		else
			this.shelfSlots = shelfSlots;
	}

	/**
	 * Adds the given {@link ProductBox} to the given {@link ShelfSlot#getLevelPosition()} in this level.
	 *
	 * @param slotPosition the position of the slot in this shelf level
	 * @param productBox the product box to add
	 * @return <code>true</code> if box was added successfully
	 * @throws InvalidAttributesException if this shelf level has no shelf slots
	 */
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

	/**
	 * Gets the {@link ShelfSlot} objects in the given position of this shelf level.
	 *
	 * @param slotPosition the position of the wanted shelf slot in this shelf level
	 * @return the wanted shelf slot or <code>null</code> if the slot was not found
	 */
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

	/**
	 * Gets the list of {@link ShelfSlot} objects that have free space in them in this shelf level.
	 *
	 * @return the list of shelf slots with free space
	 */
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

	/**
	 * Gets the number of products inside product boxes in this shelf level.
	 *
	 * @return the number of products in this shelf level
	 */
	public int getProductCountInBoxes()
	{
		int sum = 0;

		for (final ShelfSlot slot : shelfSlots)
			sum += slot.getProductCountInBoxes();

		return sum;
	}

	/**
	 * Gets the list of all {@link ProductBox} objects in this shelf level.
	 *
	 * @return the list of product boxes in this shelf level
	 */
	public List<ProductBox> getProductBoxes()
	{
		final List<ProductBox> boxes = new ArrayList<ProductBox>();

		for (final ShelfSlot slot : shelfSlots)
			boxes.addAll(slot.getProductBoxes());

		return boxes;
	}

	/**
	 * Gets the parent {@link Shelf} of this shelf level.
	 *
	 * @return the parent shelf
	 */
	public Shelf getParentShelf()
	{
		return parentShelf;
	}

	/**
	 * Sets the parent {@link Shelf} for this shelf level.
	 *
	 * @param parentShelf the parent shelf object
	 */
	public void setParentShelf(final Shelf parentShelf)
	{
		if (parentShelf == null)
			this.parentShelf.removeLevel(this);

		this.parentShelf = parentShelf;
	}

	/**
	 * Removes the given {@link ShelfSlot} from this shelf level.
	 *
	 * @param shelfSlot the shelf slot to remove
	 */
	public void removeSlot(final ShelfSlot shelfSlot)
	{
		shelfSlots.remove(shelfSlot);
	}

	/**
	 * Sets the new parent shelf for this shelf level by the database ID of the shelf.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param parentShelfID the database ID of the new parent shelf of this shelf level
	 * @see DatabaseController#getShelfByID(int)
	 */
	public void setParentShelfID(final int parentShelfID)
	{
		if (parentShelfID < 1)
			throw new IllegalArgumentException("Parent shelf ID must be greater than 0, was '" + parentShelfID + "'.");

		this.parentShelf = DatabaseController.getShelfByID(parentShelfID);
	}
}
