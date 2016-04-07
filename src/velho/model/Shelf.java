package velho.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A class representing a physical shelf structure in the warehouse.
 *
 * @author Jose Uusitalo
 */
public class Shelf implements Comparable<Shelf>
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
	 * The ID of this shelf.
	 */
	private int databaseID;

	/**
	 * Number of levels on this shelf.
	 */
	private int levelCount;

	/**
	 * The {@link ShelfLevel}s of this shelf.
	 */
	private List<ShelfLevel> levels;

	/**
	 * @param databaseID
	 * @param levelCount
	 */
	public Shelf(final int databaseID, final int levelCount)
	{
		if (levelCount < 1)
			throw new IllegalArgumentException("Number of levels on a shelf must be greater than 0.");

		this.databaseID = databaseID;
		this.levelCount = levelCount;
	}

	/**
	 * @param levelCount
	 */
	public Shelf(final int levelCount)
	{
		this(0, levelCount);
	}

	/**
	 */
	public Shelf()
	{
		// For Hibernate.
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
		final int[] tokens = shelfSlotIDTokenizer(shelfSlotID);

		// Correct shelf?
		if (databaseID != tokens[0])
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "': invalid shelf");

		// Enough levels? tokens[1] is an index.
		if (tokens[1] >= levelCount || tokens[1] < 0)
			throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "': invalid level");

		// TODO: Remove or fix this?
		// Enough slots? tokens[2] is an index.
		// if (tokens[2] >= maxSlotsPerLevel || tokens[2] < 0)
		// throw new IllegalArgumentException("Invalid shelf slot ID '" + shelfSlotID + "': invalid slot");

		return tokens;
	}

	@Override
	public String toString()
	{
		return "[" + databaseID + "] Lvls: " + levelCount + ", Boxs: " + getProductBoxCount() + ", Slts: " + getShelfSlotCount() + ", Free: "
				+ getFreeShelfSlots().size();
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof Shelf))
			return false;

		final Shelf s = (Shelf) o;

		if (this.getDatabaseID() <= 0)
			return this == s;

		return this.getDatabaseID() == s.getDatabaseID();
	}

	@Override
	public int compareTo(final Shelf shelf)
	{
		return this.getDatabaseID() - shelf.getDatabaseID();
	}

	/**
	 * Gets the shelf ID.
	 *
	 * @return the shelf ID
	 */
	public String getShelfID()
	{
		return SHELF_IDENTIFIER + databaseID;
	}

	/**
	 * Gets the database ID of this shelf.
	 *
	 * @return the database ID
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * Gets the number of shelf slots on this shelf.
	 *
	 * @return the number of slots on this shelf
	 */
	public int getShelfSlotCount()
	{
		int sum = 0;

		for (final ShelfLevel level : levels)
			sum += level.getSlots().size();

		return sum;
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
	 * Gets all shelf slots on this shelf that have some free space.
	 *
	 * @return shelf slots with free space of this shelf
	 */
	public List<ShelfSlot> getFreeShelfSlots()
	{
		final List<ShelfSlot> freeSlots = new ArrayList<ShelfSlot>();

		for (final ShelfLevel level : levels)
		{
			for (final ShelfSlot slot : level.getSlots())
			{
				if (slot.hasFreeSpace())
					freeSlots.add(slot);
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

		for (final ShelfLevel level : levels)
		{
			for (final ShelfSlot slot : level.getSlots())
				sum += slot.getProductCount();
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

		for (final ShelfLevel level : levels)
		{
			for (final ShelfSlot slot : level.getSlots())
				sum += slot.getProductBoxCount();
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
		if (productBox == null)
			throw new IllegalArgumentException("Null product box.");

		final int[] tokens = tokenizeAndValidateShelfSlotID(shelfSlotID);

		return levels.get(tokens[1]).addToSlot(tokens[2], productBox);
	}

	public ShelfSlot getShelfSlot(final String shelfSlotID)
	{
		final int[] tokens = tokenizeAndValidateShelfSlotID(shelfSlotID);

		return levels.get(tokens[1]).getShelfSlot(tokens[2]);

	}
}
