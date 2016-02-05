package velho.model;

/**
 * The ProductCategory class.
 * 
 * @author Joona &amp; Jose Uusitalo
 */
public class ProductCategory
{
	/**
	 * The product category name.
	 * 
	 */
	private String name;
	/**
	 * The product category type.
	 * 
	 */
	private ProductType type;

	/**
	 * The database ID of this product category.
	 */
	private int databaseID;

	/**
	 * @param name
	 * @param type
	 * 
	 */
	public ProductCategory(int databaseID, String name, ProductType type)
	{
		this.databaseID = databaseID;
		this.type = type;
		this.name = name;

		if (this.type == null)
		{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString()
	{
		return "[" + databaseID + "] " + name + " (" + type + ")";
	}

	/**
	 * Gets the product category name.
	 * 
	 * @return the product category name.
	 * 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the product category type.
	 * 
	 * @return the product category type.
	 * 
	 */
	public ProductType getType()
	{
		return type;
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
