package velho.model;

/**
 * @author Joona &amp; Jose Uusitalo
 */
public class ProductBrand
{
	private String name;
	private int databaseID;

	
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
	
	public String getName()
	{
		return name;
	}

	public int getDatabaseID()
	{
		return databaseID;
	}
}