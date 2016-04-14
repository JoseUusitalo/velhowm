package velho.model;

/**
 * A brand associated with a {@link Product}.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductBrand implements Comparable<ProductBrand>
{
	/**
	 * The database ID of this product brand.
	 */
	private int databaseID;

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
		this.databaseID = databaseID;
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
	 * Product Brand for Hibernate
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

	/**
	 * Gets Product Brand and returns DatabaseID
	 */
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

	/**
	 * Compares brand to another brand
	 */
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
	 * Gets the database ID of this product brand.
	 *
	 * @return the database ID of this product brand
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * Sets a new database ID for this product brand.
	 */
	public void setDatabaseID(final int id)
	{
		databaseID = id;
	}

	/**
	 * Sets a new name for this product brand.
	 */
	public void setName(final String name)
	{
		this.name = name;
	}
}
