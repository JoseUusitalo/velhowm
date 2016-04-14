package velho.model;

import java.util.Date;

/**
 * A class representing a physical box of some sort that can contain any number of {@link Product} objects.
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

		// Boxes cannot store cold products. Those may only be stored in freezers.
		if (product.getCategory().getType().getName() == "Cold")
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 */
	public ProductBox()
	{
		// For Hibernate.
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] Box: " + this.product.getName() + " (" + this.productCount + ")";
	}
}
