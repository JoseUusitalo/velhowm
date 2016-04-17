package velho.model;

import java.util.UUID;

/**
 * A class representing the type of the product.
 *
 * @author Jose Uusitalo
 */
public class ProductType extends AbstractDatabaseObject
{
	/**
	 * The product category name.
	 */
	private String name;

	/**
	 * @param databaseID
	 * @param name
	 */
	public ProductType(final int databaseID, final UUID uuid, final String name)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.name = name;
	}

	/**
	 * @param databaseID
	 * @param name
	 */
	public ProductType(final int databaseID, final String name)
	{
		this(databaseID, UUID.randomUUID(), name);
	}

	/**
	 * @param name
	 */
	public ProductType(final String name)
	{
		this(0, name);
	}

	/**
	 */
	public ProductType()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	@Override
	public int compareTo(final AbstractDatabaseObject type)
	{
		if (type instanceof ProductType)
			return this.getName().compareTo(((ProductType) type).getName());

		return super.compareTo(type);
	}

	/**
	 * @return the name of this product type
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Gets the name of this product type.
	 *
	 * @return the name of this product type
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets a new name for this product type.
	 *
	 * @param name the new name
	 */
	public void setName(final String name)
	{
		this.name = name;
	}
}
