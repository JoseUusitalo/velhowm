package velho.model;

/**
 * @author Joona &amp; Jose Uusitalo
 */
public class ProductBrand
{
	private String name;
	private int databaseID;

	/**
	 * @param databaseID
	 * @param name
	 */
	public ProductBrand(int databaseID, String name)
	{
		this.databaseID = databaseID;
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
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
}