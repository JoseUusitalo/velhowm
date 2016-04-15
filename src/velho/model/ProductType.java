package velho.model;

import java.util.UUID;

/**
 * A class representing the type of the product.
 *
 * @author Jose Uusitalo
 */
public class ProductType extends AbstractDatabaseObject implements Comparable<ProductType>
{

	private UUID uuid;

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
		this.uuid = uuid;
		this.name = name;
	}

	/**
	 * @param name
	 */
	public ProductType(final String name)
	{
		this(0, UUID.randomUUID(), name);
	}

	/**
	 */
	public ProductType()
	{
		// For Hibernate.
		this(null);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
			return true;

		if (o == null || !(o instanceof ProductType))
			return false;

		return this.getUuid().equals(((ProductType) o).getUuid());
	}

	@Override
	public int hashCode()
	{
		return 3 * getUuid().hashCode();
	}

	@Override
	public int compareTo(final ProductType type)
	{
		return this.getName().compareTo(type.getName());
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
