package velho.model;

import java.util.Date;
import java.util.UUID;

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
	 */
	public Freezer(final int freezerID, final UUID uuid, final Date expirationDate, final int maxSize, final Product product, final int productCount)
	{
		super(freezerID, uuid, null, null, null, product, maxSize, productCount, expirationDate);

		if (this.getBoxType().getName() == "Regular")
		{
			throw new IllegalArgumentException();
		}
	}

	public Freezer(final int databaseID, final Date expirationDate, final int maxSize, final Product product, final int productCount)
	{
		this(databaseID, UUID.randomUUID(), expirationDate, maxSize, product, productCount);
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] Freezer: " + this.product.getName() + " (" + this.productCount + ")";
	}
}
