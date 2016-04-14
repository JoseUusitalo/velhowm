package velho.model;

/**
 * A brand associated with a {@link Product}.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductBrand extends AbstractDatabaseObject implements Comparable<ProductBrand>
{
	/**
	 * The name of this product brand.
	 */
	private String name;

	/**
	 * @param databaseID
	 * @param name
	 */
	public ProductBrand(final int databaseID, final String name)
	{
		setDatabaseID(databaseID);
		this.name = name;
	}

	/**
	 * @param name
	 */
	public ProductBrand(final String name)
	{
		this(0, name);
	}

	/**
	 */
	public ProductBrand()
	{
		// For Hibernate.
	}

	/**
	 * @return the name of this product brand
	 */
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ProductBrand))
			return false;

		final ProductBrand pb = (ProductBrand) o;

		if (this.getDatabaseID() <= 0)
			return this == pb;

		return this.getDatabaseID() == pb.getDatabaseID();
	}

	@Override
	public int compareTo(final ProductBrand brand)
	{
		return this.getName().compareTo(brand.getName());
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
