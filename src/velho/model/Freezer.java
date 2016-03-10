package velho.model;

import java.util.Date;

/**
 * The freezer class.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class Freezer extends ProductContainer
{
	/**
	 * @param freezerID
	 * @param expirationDate
	 * @param maxSize
	 * @param product
	 * @param productCount
	 * @param category
	 */
	public Freezer(final int freezerID, final Date expirationDate, final int maxSize, final Product product, final int productCount)
	{
		super(freezerID, expirationDate, maxSize, product, productCount);

		if (this.getBoxType().getName() == "Regular")
		{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString()
	{
		return "[" + this.databaseID + "] Freezer: " + this.product.getName() + " (" + this.productCount + ")";
	}
}