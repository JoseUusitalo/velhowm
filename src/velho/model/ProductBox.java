package velho.model;

import java.util.Date;

/**
 * The ProductBox class.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductBox extends ProductContainer
{
	/**
	 * @param databaseID
	 * @param expirationDate
	 * @param maxSize
	 * @param product
	 * @param productCount
	 */
	public ProductBox(final int databaseID, final Date expirationDate, final int maxSize, final Product product, final int productCount)
	{
		super(databaseID, expirationDate, maxSize, product, productCount);

		if (product.getCategory().getType().getName() == "Cold")
		{
			throw new IllegalArgumentException();
		}
	}

	@Override public String toString()
	{
		return "[" + this.databaseID + "] Box: " + this.product.getName() + " (" + this.productCount + ")";
	}
}
