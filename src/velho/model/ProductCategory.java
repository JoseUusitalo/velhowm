package velho.model;

/**
 * A class describing the category of a {@link Product}.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductCategory
{
	/**
	 * The database ID of this product category.
	 */
	private int databaseID;

	/**
	 * The product category name.
	 */
	private String name;

	/**
	 * The product category type.
	 */
	private ProductType type;

	/**
	 * @param databaseID
	 * @param name
	 * @param type
	 */
	public ProductCategory(final int databaseID, final String name, final ProductType type)
	{
		this.databaseID = databaseID;
		this.type = type;
		this.name = name;
	}

	/**
	 * @param name
	 * @param type
	 */
	public ProductCategory(final String name, final ProductType type)
	{
		this(-1, name, type);
	}

	/**
	 * @param name
	 */
	public ProductCategory(final String name)
	{
		this(-1, name, null);
	}

	/**
	 */
	public ProductCategory()
	{
		// For Hibernate.
	}

	/**
	 * @return the name of this category and its type
	 */
	@Override
	public String toString()
	{
		return name + " (" + type + ")";
	}

	/**
	 * Gets the database ID of this category.
	 *
	 * @return the database id
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * Gets the product category name.
	 *
	 * @return the product category name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the product category type.
	 *
	 * @return the product category type.
	 */
	public ProductType getType()
	{
		return type;
	}

	/**
	 * Sets a new datbase ID for this product category.
	 */
	public void setDatabaseID(final int id)
	{
		databaseID = id;
	}

	/**
	 * Sets a new name for this product category.
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * Sets a new type for this product category.
	 */
	public void setType(final ProductType type)
	{
		this.type = type;
	}
}
