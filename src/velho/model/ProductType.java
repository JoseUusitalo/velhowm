package velho.model;

/**
 * A class representing the type of the product.
 * 
 * @author Jose Uusitalo
 */
public class ProductType
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
	 * @param name
	 * @param type
	 */
	public ProductType(final int databaseID, final String name)
	{
		this.databaseID = databaseID;
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
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
}
