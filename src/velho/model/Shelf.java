package velho.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.naming.directory.InvalidAttributesException;

import org.apache.log4j.Logger;

/**
 * A class representing a physical shelf structure in the warehouse.
 *
 * @author Jose Uusitalo
 */
public class Shelf extends AbstractDatabaseObject
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(Shelf.class.getName());

	/**
	 * The identifier of a shelf in IDs.
	 */
	public static final String SHELF_IDENTIFIER = "S";

	/**
	 * Number of levels on this shelf.
	 */
	private int levelCount;

	/**
	 * The {@link ShelfLevel}s in this shelf.
	 */
	private Set<ShelfLevel> shelfLevels;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param levelCount
	 */
	public Shelf(final int databaseID, final UUID uuid, final int levelCount)
	{
		if (levelCount < 1)
			throw new IllegalArgumentException("Number of levels on a shelf must be greater than 0.");

		setDatabaseID(databaseID);
		setUuid(uuid);
		this.levelCount = levelCount;
		this.shelfLevels = new TreeSet<ShelfLevel>();
	}

	/**
	 * @param databaseID
	 * @param levelCount
	 */
	public Shelf(final int databaseID, final int levelCount)
	{
		this(databaseID, UUID.randomUUID(), levelCount);
	}

	/**
	 */
	public Shelf()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	/**
	 * Converts the given coordinate number on a shelf into a shelf slot ID and validates it.
	 *
	 * @param shelfID the ID of the shelf
	 * @param shelfLevelNumber the level of the shelf (1 or greater)
	 * @param shelfSlotIndex the slot on a level (0 or greater)
	 * @param validateValues validate the given values?
	 * @return a shelf slot ID
	 * @throws IllegalArgumentException if values were validated and some value was invalid
	 */
	public static String coordinatesToShelfSlotID(final int shelfID, final int shelfLevelNumber, final int shelfSlotIndex, final boolean validateValues)
			throws IllegalArgumentException
	{
		final String shelfSlotID = SHELF_IDENTIFIER + shelfID + ShelfSlot.ID_SEPARATOR + shelfLevelNumber + ShelfSlot.ID_SEPARATOR + shelfSlotIndex;

		if (validateValues)
			tokenizeShelfSlotID(shelfSlotID);

		return shelfSlotID;

	}

	/**
	 * Converts the given slot ID into an Object array where:
	 * <ul>
	 * <li>Index 0: is the shelf ID String</li>
	 * <li>Index 1: is the Integer level in the shelf</li>
	 * <li>Index 2: is the slot ID Integer in the shelf</li>
	 * </ul>
	 *
	 * @param shelfSlotID the shelf slot ID string to tokenize
	 * @return an array of integers
	 * @throws IllegalArgumentException if the given shelf slot ID was invalid
	 */
	public static Object[] tokenizeShelfSlotID(final String shelfSlotID) throws IllegalArgumentException
	{
		// If the shelf slot ID does not begin with S it is not a shelf slot ID.
		if (shelfSlotID == null || shelfSlotID.isEmpty() || shelfSlotID.charAt(0) != 'S')
			throw new IllegalArgumentException("Invalid slot ID '" + shelfSlotID + "'.");

		// Remove the S from front so we can parse the values as integers.
		final String[] stringTokens = shelfSlotID.substring(1).split(ShelfSlot.ID_SEPARATOR);

		final Object[] tokens = new Object[3];

		try
		{
			tokens[0] = Shelf.SHELF_IDENTIFIER + Integer.parseInt(stringTokens[0]);
		}
		catch (final NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "'.");
		}

		try
		{
			tokens[1] = Integer.parseInt(stringTokens[1]);
		}
		catch (final NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "'.");
		}

		try
		{
			tokens[2] = Integer.parseInt(stringTokens[2]);
		}
		catch (final NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "'.");
		}

		return tokens;
	}

	/**
	 * Converts the given slot ID into an array of integers where:
	 * <ul>
	 * <li>Index 0: is the shelf database ID</li>
	 * <li>Index 1: is the level position in the shelf</li>
	 * <li>Index 2: is the slot position on the level</li>
	 * </ul>
	 * <p>
	 * For internal use only. Automatically removes the S from shelf ID.
	 * </p>
	 *
	 * @param slotID
	 * @return an array of integers
	 * @throws IllegalArgumentException if the given shelf slot ID was invalid
	 */
	private static int[] shelfSlotIDTokenizer(final String shelfSlotID) throws IllegalArgumentException
	{
		if (shelfSlotID == null)
			throw new IllegalArgumentException("Null shelf slot ID '" + shelfSlotID + "'.");

		// If the shelf slot ID does not begin with S it is not a shelf slot ID.
		if (shelfSlotID.charAt(0) != 'S')
			throw new IllegalArgumentException("Malformed shelf slot ID '" + shelfSlotID + "'.");

		// Remove the S from front so we can parse the values as integers.
		final String[] stringTokens = shelfSlotID.substring(1).split(ShelfSlot.ID_SEPARATOR);

		final int[] tokens = new int[3];

		try
		{
			tokens[0] = Integer.parseInt(stringTokens[0]);
		}
		catch (final NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "'.");
		}

		try
		{
			tokens[1] = Integer.parseInt(stringTokens[1]);
		}
		catch (final NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "'.");
		}

		try
		{
			tokens[2] = Integer.parseInt(stringTokens[2]);
		}
		catch (final NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "'.");
		}

		return tokens;
	}

	/**
	 * Tokenizes the given shelf slot ID with {@link #shelfSlotIDTokenizer(String)} and validates the result to make
	 * sure the given ID represent a slot in this shelf.
	 *
	 * @param shelfSlotID shelf slot ID string to tokenize and validate
	 * @return an array of integers where the values are: the database ID of this shelf, the index of the level, and the
	 * index of the slot on the level
	 */
	private int[] tokenizeAndValidateShelfSlotID(final String shelfSlotID)
	{
		/*
		 * TODO: It would be nice if I could get rid of this and simply pass around the objects and only use the ID
		 * string for display purposes.
		 */

		final int[] tokens = shelfSlotIDTokenizer(shelfSlotID);

		// Correct shelf?
		if (getDatabaseID() != tokens[0])
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "': invalid shelf " + tokens[0]);

		// Enough levels?
		if (tokens[1] > levelCount || tokens[1] < 0)
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "': invalid level " + tokens[1]);

		// Enough slots?
		if (tokens[2] > getShelfLevel(tokens[1]).getMaxShelfSlots() || tokens[2] < 0)
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "': invalid slot");

		return tokens;
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] Lvls: " + levelCount + ", Boxs: " + getProductBoxes().size() + ", Slts: " + getShelfSlotCount() + ", Free: "
				+ getFreeShelfSlots().size();
	}

	/**
	 * Gets the shelf ID string which is the database ID prefix with {@link Shelf#SHELF_IDENTIFIER}.
	 *
	 * @return the shelf ID string
	 */
	public String getShelfID()
	{
		return SHELF_IDENTIFIER + getDatabaseID();
	}

	/**
	 * Gets the number of levels on this shelf.
	 *
	 * @return the number of levels of this shelf
	 */
	public int getLevelCount()
	{
		return levelCount;
	}

	/**
	 * Do not use, does not change anything yet.
	 * Only for Hibernate.
	 *
	 * @param levels the new number of levels of this shelf
	 */
	public void setLevelCount(final int levels)
	{
		this.levelCount = levels;
	}

	/**
	 * Gets all {@link ShelfLevel} objects in this shelf.
	 *
	 * @return a set of all shelf levels
	 */
	public Set<ShelfLevel> getShelfLevels()
	{
		return shelfLevels;
	}

	/**
	 * Assign a new set of {@link ShelfLevel} objects to this shelf.
	 *
	 * @param shelfLevels the set of shelf levels
	 */
	public void setShelfLevels(final Set<ShelfLevel> shelfLevels)
	{
		this.shelfLevels = shelfLevels;
	}

	/**
	 * Gets the number of shelf slots on this shelf.
	 *
	 * @return the number of slots on this shelf
	 */
	public int getShelfSlotCount()
	{
		int sum = 0;

		for (final ShelfLevel level : shelfLevels)
			sum += level.getShelfSlots().size();

		return sum;
	}

	/**
	 * Counts the number of all shelf slots on this shelf that have some free space.
	 *
	 * @return shelf slots with free space on this shelf
	 */
	public List<ShelfSlot> getFreeShelfSlots()
	{
		final List<ShelfSlot> freeSlots = new ArrayList<ShelfSlot>();

		for (final ShelfLevel level : shelfLevels)
			freeSlots.addAll(level.getFreeShelfSlots());

		return freeSlots;
	}

	/**
	 * Counts the number of products inside boxes on this shelf.
	 *
	 * @return the number of products inside boxes on this shelf
	 */
	public int getProductCountInBoxes()
	{
		int sum = 0;

		for (final ShelfLevel level : shelfLevels)
			sum += level.getProductCountInBoxes();

		return sum;
	}

	/**
	 * Gets all {@link ProductBox} objects on this shelf.
	 *
	 * @return the product boxes on this shelf
	 */
	public List<ProductBox> getProductBoxes()
	{
		final List<ProductBox> boxes = new ArrayList<ProductBox>();

		for (final ShelfLevel level : shelfLevels)
			boxes.addAll(level.getProductBoxes());

		return boxes;
	}

	/**
	 * Checks if this shelf is empty.
	 *
	 * @return <code>true</code> if this shelf is empty
	 */
	public boolean isEmpty()
	{
		return getProductBoxes().size() == 0;
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
	 * Attempts to add the given {@link ProductBox} into the {@link ShelfSlot} specified by the slot ID.
	 *
	 * @param shelfSlotID ID of the shelf slot
	 * @param productBox box to add
	 * @return <code>true</code> if box was added to the slot, <code>false</code> the slot did not have enough free
	 * space
	 * @throws IllegalArgumentException if the slot ID is not in this shelf or the given box was <code>null</code>
	 */
	public boolean addToSlot(final String shelfSlotID, final ProductBox productBox) throws IllegalArgumentException
	{
		SYSLOG.trace("Adding product box " + productBox + " to shelf slot " + shelfSlotID + " in the shelf: " + toString());

		if (productBox == null)
			throw new IllegalArgumentException("Null product box.");

		final int[] tokens = tokenizeAndValidateShelfSlotID(shelfSlotID);

		final ShelfLevel level = getShelfLevel(tokens[1]);

		if (level != null)
		{
			try
			{
				return level.addToSlot(tokens[2], productBox);
			}
			catch (InvalidAttributesException e)
			{
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Gets the {@link ShelfLevel} object according to its position in this shelf from the bottom starting at 1.
	 *
	 * @param shelfPosition the shelf position integer
	 * @return the corresponding shelf level object
	 */
	public ShelfLevel getShelfLevel(final int shelfPosition)
	{
		for (final ShelfLevel level : shelfLevels)
		{
			if (level.getShelfPosition() == shelfPosition)
				return level;
		}

		return null;
	}

	/**
	 * Gets the {@link ShelfSlot} object according to its {@link ShelfSlot#getSlotID()}.
	 *
	 * @param shelfSlotID the shelf slot ID string
	 * @return the corresponding shelf slot object
	 */
	public ShelfSlot getShelfSlot(final String shelfSlotID)
	{
		final int[] tokens = tokenizeAndValidateShelfSlotID(shelfSlotID);

		SYSLOG.trace("Looking for shelf slot " + shelfSlotID + ".");

		final ShelfLevel level = getShelfLevel(tokens[1]);

		if (level != null)
		{
			SYSLOG.trace("Level: " + level);
			return level.getShelfSlot(tokens[2]);
		}

		return null;
	}

	/**
	 * Removes the given {@link ShelfLevel} from this shelf.
	 *
	 * @param shelfLevel the shelf level to remove
	 */
	public void removeLevel(final ShelfLevel shelfLevel)
	{
		shelfLevels.remove(shelfLevel);
	}

	/**
	 * Gets the database ID of the shelf from the given {@link ShelfSlot#getSlotID()}.
	 *
	 * @param shelfSlotID shelf slot ID string
	 * @return the database ID of the shelf it shelf slot is in
	 */
	public static int shelfSlotIDToShelfDatabaseID(final String shelfSlotID)
	{
		return Integer.valueOf(String.valueOf(shelfSlotID.subSequence(1, shelfSlotID.indexOf(ShelfSlot.ID_SEPARATOR))));
	}
}
