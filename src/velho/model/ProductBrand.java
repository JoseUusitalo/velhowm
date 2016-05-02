package velho.model;

import java.util.UUID;

/**
 * A brand associated with a {@link Product}.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductBrand extends AbstractDatabaseObject
{
	/**
	 * The name of this product brand.
	 */
	private String name;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param name
	 */
	public ProductBrand(final int databaseID, final UUID uuid, final String name)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.name = name;
	}

	/**
	 * @param name
	 */
	public ProductBrand(final int databaseID, final String name)
	{
		this(databaseID, UUID.randomUUID(), name);
	}

	/**
	 * @param name
	 */
	public ProductBrand(final String name)
	{
		this(0, UUID.randomUUID(), name);
	}

	/**
	 * Product Brand for Hibernate
	 */
	public ProductBrand()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	/**
	 * @return the name of this product brand
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Gets Product Brand and returns DatabaseID
	 */
	@Override
	public int compareTo(final AbstractDatabaseObject brand)
	{
		if (brand instanceof ProductBrand)
			return this.getName().compareTo(((ProductBrand) brand).getName());

		return super.compareTo(brand);
	}

	/**
	 * Gets the name of this product brand.
	 *
	 * @return the name of this product brand
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets a new name for this product brand.
	 */
	public void setName(final String name)
	{
		this.name = name;
	}
}
