package velho.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import velho.controller.database.DatabaseController;

/**
 * A Shelf Slot represents an indexed area with {@link ProductBox} objects on a {@link ShelfLevel}.
 *
 * @author Jose Uusitalo
 */
public class ShelfSlot extends AbstractDatabaseObject
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ShelfSlot.class.getName());

	/**
	 * A separator string between values in shelf slot IDs.
	 */
	public static final String ID_SEPARATOR = "-";

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

	/**
	 * The {@link ShelfLevel} that this shelf slot is on.
	 */
	private ShelfLevel parentShelfLevel;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param parentShelfLevel
	 * @param levelPosition
	 * @param maxBoxesInSlot
	 */
	public ShelfSlot(final int databaseID, final UUID uuid, final ShelfLevel parentShelfLevel, final int levelPosition, final int maxBoxesInSlot)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.parentShelfLevel = parentShelfLevel;

		if (maxBoxesInSlot < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Maxmimum ProductBox count must be greater than 0, was " + maxBoxesInSlot + ".");

		this.maxProductBoxes = maxBoxesInSlot;

		if (levelPosition < 1)
			throw new IllegalArgumentException("[" + databaseID + "] Level position must be greater than 0, was " + levelPosition + ".");

		this.levelPosition = levelPosition;

		productBoxes = new HashSet<ProductBox>();
	}

	/**
	 * @param databaseID
	 * @param parentShelfLevel
	 * @param levelPosition
	 * @param maxBoxesInSlot
	 */
	public ShelfSlot(final int databaseID, final ShelfLevel parentShelfLevel, final int levelPosition, final int maxBoxesInSlot)
	{
		this(databaseID, UUID.randomUUID(), parentShelfLevel, levelPosition, maxBoxesInSlot);
	}

	/**
	 */
	public ShelfSlot()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
		productBoxes = new HashSet<ProductBox>();
	}

	@Override
	public String toString()
	{
		return parentShelfLevel.getParentShelf().getShelfID() + ID_SEPARATOR + parentShelfLevel.getShelfPosition() + ID_SEPARATOR + levelPosition;
	}

	@Override
	public int compareTo(final AbstractDatabaseObject slot)
	{
		if (slot instanceof ShelfSlot)
			return getSlotID().compareToIgnoreCase(((ShelfSlot) slot).getSlotID());

		return super.compareTo(slot);
	}

	/**
	 * Gets the position (greater than 1) of this shelf slot in the parent {@link ShelfLevel}.
	 *
	 * @return the position of this shelf slot in the parent shelf level
	 */
	public int getLevelPosition()
	{
		return levelPosition;
	}

	/**
	 * Sets the position of this shelf slot in the parent {@link ShelfLevel}.
	 *
	 * @param levelPosition the new position in the parent level in the range [1, {@link Integer#MAX_VALUE}]
	 */
	public void setLevelPosition(final int levelPosition)
	{
		if (levelPosition < 1)
			throw new IllegalArgumentException("Shelf slot position was less than 1.");

		this.levelPosition = levelPosition;
	}

	/**
	 * Gets the maximum number of product boxes in this shelf slot.
	 *
	 * @return the maximum product boxes this slot can hold
	 */
	public int getMaxProductBoxes()
	{
		return maxProductBoxes;
	}

	/**
	 * Sets the maximum number of {@link ProductBox} objects this shelf slot can hold.
	 *
	 * @param maxProductBoxes the new maximum number of product boxes (greater than or equal to 0)
	 */
	public void setMaxProductBoxes(final int maxProductBoxes)
	{
		if (maxProductBoxes < 0)
			throw new IllegalArgumentException("Maximum product boxes was less than 0.");

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

	/**
	 * Gets the set of {@link ProductBox} objects on this shelf slot.
	 *
	 * @return the set of product boxes in this slot
	 */
	public Set<ProductBox> getProductBoxes()
	{
		return productBoxes;
	}

	/**
	 * Sets the set of {@link ProductBox} objects in this shelf slot.
	 *
	 * @param productBoxes the new set of product boxes, <code>null</code> to clear
	 */
	public void setProductBoxes(final Set<ProductBox> productBoxes)
	{
		if (productBoxes == null)
		{
			for (final ProductBox b : this.productBoxes)
				b.setShelfSlot(null);

			this.productBoxes.clear();
		}
		else
			this.productBoxes = productBoxes;
	}

	/**
	 * Iterates through the set and counts the number of products in the {@link ProductBox}es.
	 *
	 * @return the number of products in this shelf slot
	 */
	public int getProductCountInBoxes()
	{
		final Iterator<ProductBox> iter = productBoxes.iterator();

		int sum = 0;

		while (iter.hasNext())
			sum += iter.next().getProductCount();

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
		SYSLOG.trace("Adding product box " + box + " to shelf slot: " + this);

		if (productBoxes.size() + 1 > maxProductBoxes)
		{
			SYSLOG.trace("Failed to add product box + " + box + ": shelf slot was full.");
			return false;
		}

		if (productBoxes.add(box))
		{
			box.setShelfSlot(this);
			SYSLOG.trace("Successfully added product box + " + box + ".");

			return true;
		}

		SYSLOG.trace("Failed to add product box + " + box + ": box set already contains the box.");
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
		SYSLOG.trace("Removing product box " + box + " from shelf slot: " + this);

		if (productBoxes.remove(box))
		{
			box.setShelfSlot(null);

			SYSLOG.trace("Successfully removed product box + " + box + ".");
			return true;
		}

		SYSLOG.trace("Failed to remove product box + " + box + ": box set did not contain box.");

		return false;
	}

	/**
	 * Checks if this shelf slot contains the given {@link ProductBox} object.
	 *
	 * @param box product box to check
	 * @return <code>true</code> if the product box is in this shelf slot
	 */
	public boolean contains(final ProductBox box)
	{
		return productBoxes.contains(box);
	}

	/**
	 * Gets the parent {@link ShelfLevel} object this shelf slot is in.
	 *
	 * @return the parent shelf level
	 */
	public ShelfLevel getParentShelfLevel()
	{
		return parentShelfLevel;
	}

	/**
	 * Sets the parent {@link ShelfLevel} object for this shelf slot.
	 *
	 * @param parentShelfLevel the new parent shelf level
	 */
	public void setParentShelfLevel(final ShelfLevel parentShelfLevel)
	{
		if (parentShelfLevel == null)
			this.parentShelfLevel.removeSlot(this);

		this.parentShelfLevel = parentShelfLevel;
	}

	/**
	 * Sets the new parent shelf level for this shelf slot by the database ID of the shelf level.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param parentShelfLevelID the database ID of the new parent shelf level of this shelf slot
	 * @see DatabaseController#getShelfLevelByID(int)
	 */
	public void setParentShelfLevelID(final int parentShelfLevelID)
	{
		if (parentShelfLevelID < 1)
			throw new IllegalArgumentException("Parent shelf level ID must be greater than 0, was '" + parentShelfLevelID + "'.");

		this.parentShelfLevel = DatabaseController.getShelfLevelByID(parentShelfLevelID);
	}
}
