package velho.model;

/**
 * A class representing the type of the product.
 *
 * @author Jose Uusitalo
 */
public class ProductType extends AbstractDatabaseObject implements Comparable<ProductType>
{
	/**
	 * The product category name.
	 */
	private String name;

	/**
	 * @param databaseID
	 * @param name
	 */
	public ProductType(final int databaseID, final String name)
	{
		setDatabaseID(databaseID);
		this.name = name;
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
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ProductType))
			return false;

		final ProductType pt = (ProductType) o;

		if (this.getDatabaseID() <= 0)
			return this == pt;

		return this.getDatabaseID() == pt.getDatabaseID();
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
