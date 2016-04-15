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
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 * @param expirationDate
	 */
	public ProductBox(final int databaseID, final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot, final Product product,
			final int maxSize, final int productCount, final Date expirationDate)
	{
		super(databaseID, manifest, removalList, shelfSlot, product, maxSize, productCount, expirationDate);

		// Boxes cannot store cold products. Those may only be stored in freezers.
		if (product.getCategory().getType().getName() == "Cold")
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @param databaseID
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 */
	public ProductBox(final int databaseID, final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot, final Product product,
			final int maxSize, final int productCount)
	{
		this(databaseID, manifest, removalList, shelfSlot, product, maxSize, productCount, null);
	}

	/**
	 * @param databaseID
	 * @param product
	 * @param maxSize
	 * @param productCount
	 * @param expirationDate
	 */
	public ProductBox(final int databaseID, final Product product, final int maxSize, final int productCount, final Date expirationDate)
	{
		this(databaseID, null, null, null, product, maxSize, productCount, expirationDate);
	}

	/**
	 * @param databaseID
	 * @param product
	 * @param maxSize
	 * @param productCount
	 */
	public ProductBox(final int databaseID, final Product product, final int maxSize, final int productCount)
	{
		this(databaseID, null, null, null, product, maxSize, productCount, null);
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
