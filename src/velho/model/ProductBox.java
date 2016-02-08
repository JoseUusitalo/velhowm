package velho.model;

/**
 * The ProductBox class.
 * 
 * @author Joona &amp; Jose Uusitalo
 */
public class ProductBox extends ProductContainer
{
	/**
	 * @param databaseID
	 * @param maxSize
	 * @param product
	 * @param productCount
	 */
	public ProductBox(int databaseID, int maxSize, Product product, int productCount)
	{
		super(databaseID, maxSize, product, productCount);

		if (product.getCategory().getType().getName() == "Cold")
		{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString()
	{
		return "[" + this.databaseID + "] Box: " + this.product.getName() + " (" + this.productCount + ")";
	}
}
