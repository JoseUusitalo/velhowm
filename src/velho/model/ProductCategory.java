package velho.model;

import java.util.UUID;

/**
 * A class describing the category of a {@link Product}.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductCategory extends AbstractDatabaseObject implements Comparable<ProductCategory>
{
	/**
	 * The product category name.
	 */
	private String name;

	private UUID uuid;

	/**
	 * The product category type.
	 */
	private ProductType type;

	/**
	 * @param databaseID
	 * @param name
	 * @param type
	 */
	public ProductCategory(final int databaseID, final UUID uuid, final String name, final ProductType type)
	{
		setDatabaseID(databaseID);
		this.setUuid(uuid);
		this.type = type;
		this.name = name;
	}

	/**
	 * @param name
	 * @param type
	 */
	public ProductCategory(final String name, final ProductType type)
	{
		this(0, UUID.randomUUID(), name, type);
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
		this(null);
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
	public boolean equals(final Object o)
	{
		if (this == o)
			return true;

		if (o == null || !(o instanceof ProductCategory))
			return false;

		return this.getUuid().equals(((ProductCategory) o).getUuid());
	}

	@Override
	public int compareTo(final ProductCategory category)
	{
		return this.getName().compareTo(category.getName());
	}

	@Override
	public int hashCode()
	{
		return 3 * getUuid().hashCode();
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid()
	{
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(final UUID uuid)
	{
		this.uuid = uuid;
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
