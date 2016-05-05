package velho.model;

import java.util.Date;
import java.util.UUID;

/**
 * A class representing a physical box of some sort that can contain any number of {@link Product} objects.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class ProductBox extends ProductContainer
{
	/**
	 * @param databaseID
	 * @param uuid
	 * @param manifest
	 * @param removalList
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 * @param expirationDate
	 */
	public ProductBox(final int databaseID, final UUID uuid, final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot, final Product product, final int maxSize, final int productCount, final Date expirationDate)
	{
		super(databaseID, uuid, manifest, removalList, shelfSlot, product, maxSize, productCount, expirationDate);
	}

	/**
	 * @param databaseID
	 * @param manifest
	 * @param removalList
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 * @param expirationDate
	 */
	public ProductBox(final int databaseID, final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot, final Product product, final int maxSize, final int productCount, final Date expirationDate)
	{
		super(databaseID, UUID.randomUUID(), manifest, removalList, shelfSlot, product, maxSize, productCount, expirationDate);
	}

	/**
	 * @param manifest
	 * @param removalList
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 * @param expirationDate
	 */
	public ProductBox(final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot, final Product product, final int maxSize, final int productCount, final Date expirationDate)
	{
		this(0, UUID.randomUUID(), manifest, removalList, shelfSlot, product, maxSize, productCount, expirationDate);
	}

	/**
	 * @param databaseID
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 */
	public ProductBox(final int databaseID, final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot, final Product product, final int maxSize, final int productCount)
	{
		this(databaseID, manifest, removalList, shelfSlot, product, maxSize, productCount, null);
	}

	/**
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 */
	public ProductBox(final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot, final Product product, final int maxSize, final int productCount)
	{
		this(0, manifest, removalList, shelfSlot, product, maxSize, productCount, null);
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
	 * @param product
	 * @param maxSize
	 * @param productCount
	 * @param expirationDate
	 */
	public ProductBox(final Product product, final int maxSize, final int productCount, final Date expirationDate)
	{
		this(0, null, null, null, product, maxSize, productCount, expirationDate);
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
	 * @param product
	 * @param maxSize
	 * @param productCount
	 */
	public ProductBox(final Product product, final int maxSize, final int productCount)
	{
		this(0, null, null, null, product, maxSize, productCount, null);
	}

	/**
	 */
	public ProductBox()
	{
		super();
		// For Hibernate, also generates the UUID.
	}

	public ProductBox(final Integer valueOf, final Integer valueOf2, final Integer valueOf3, final Integer valueOf4, final Integer valueOf5, final Integer valueOf6, final Integer valueOf7, final String string)
	{
		// TODO for 1 string to rule them all CSV business
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] Box: " + this.product.getName() + " (" + this.productCount + ")";
	}
}
