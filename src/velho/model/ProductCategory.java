package velho.model;

import java.util.UUID;

import velho.controller.database.DatabaseController;

/**
 * A class describing the category of a {@link Product}.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductCategory extends AbstractDatabaseObject
{
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
	 * @param uuid
	 * @param name
	 * @param type
	 */
	public ProductCategory(final int databaseID, final UUID uuid, final String name, final ProductType type)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.type = type;
		this.name = name;
	}

	/**
	 * @param databaseID
	 * @param name
	 * @param type
	 */
	public ProductCategory(final int databaseID, final String name, final ProductType type)
	{
		this(databaseID, UUID.randomUUID(), name, type);
	}

	/**
	 * @param name
	 * @param type
	 */
	public ProductCategory(final String name, final ProductType type)
	{
		this(0, name, type);
	}

	/**
	 * @param name
	 */
	public ProductCategory(final String name)
	{
		this(0, UUID.randomUUID(), name, null);
	}

	/**
	 */
	public ProductCategory()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	/**
	 * @return the name of this category and its type
	 */
	@Override
	public String toString()
	{
		return name + " (" + type + ")";
	}

	@Override
	public int compareTo(final AbstractDatabaseObject category)
	{
		if (category instanceof ProductCategory)
			return this.getName().compareTo(((ProductCategory) category).getName());

		return super.compareTo(category);
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
	 * Sets a new name for this product category.
	 *
	 * @param name the new name of this category
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * Sets a new type for this product category.
	 *
	 * @param type the new product type of this category
	 */
	public void setType(final ProductType type)
	{
		this.type = type;
	}

	/**
	 * Sets a new type for this product category by the database ID of the type.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param typeID the database ID of the new product type of this category
	 * @see DatabaseController#getProductTypeByID(int)
	 */
	public void setTypeID(final int typeID)
	{
		if (typeID < 1)
			throw new IllegalArgumentException("Type ID must be greater than 0, was '" + typeID + "'.");

		this.type = DatabaseController.getProductTypeByID(typeID);
	}
}
