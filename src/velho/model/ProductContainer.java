package velho.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import velho.controller.database.DatabaseController;

/**
 * An abstract container that can hold any number of {@link Product} objects.
 *
 * @author Jose Uusitalo &amp; Joona Silvennoinen
 */
public abstract class ProductContainer extends AbstractDatabaseObject
{
	/**
	 * The expiration date of the products in this container.
	 */
	private Date expirationDate;

	/**
	 * The maximum size of this product box.
	 */
	protected int maxSize;

	/**
	 * The product.
	 */
	protected Product product;

	/**
	 * The number of products in the product box.
	 */
	protected int productCount;

	/**
	 * The {@link ShelfSlot} this container is currently in.
	 */
	protected ShelfSlot shelfSlot;

	/**
	 * The {@link RemovalList} this container is currently in.
	 */
	protected RemovalList removalList;

	/**
	 * The {@link Manifest} this container is currently in.
	 */
	protected Manifest manifest;

	/**
	 * @param databaseID
	 * @param shelfSlot
	 * @param product
	 * @param maxSize
	 * @param productCount
	 * @param expirationDate
	 */
	@SuppressWarnings("unused")
	public ProductContainer(final int databaseID, final UUID uuid, final Manifest manifest, final RemovalList removalList, final ShelfSlot shelfSlot,
			final Product product, final int maxSize, final int productCount, final Date expirationDate)
	{
		if (maxSize < 1 || maxSize < productCount)
		{
			throw new IllegalArgumentException();
		}

		// Database ID left unused on purpose.
		setUuid(uuid);
		this.manifest = manifest;
		this.removalList = removalList;
		this.shelfSlot = shelfSlot;
		this.expirationDate = expirationDate;
		this.maxSize = maxSize;
		this.product = product;
		this.productCount = productCount;
	}

	/**
	 */
	public ProductContainer()
	{
		setUuid(UUID.randomUUID());
		setDatabaseID(0);
	}

	/**
	 * Gets the maximum size of the product container.
	 *
	 * @return the maximum size of the product container.
	 */
	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * Sets the maximum number of product this product container can hold
	 *
	 * @param maxSize the maximum size of this product container
	 */
	public void setMaxSize(final int maxSize)
	{
		this.maxSize = maxSize;
	}

	/**
	 * Gets the product of this product container.
	 *
	 * @return the product of this product container.
	 */
	public Product getProduct()
	{
		return product;
	}

	/**
	 * Sets the product of this product container.
	 *
	 * @param product the new product of this product container
	 */
	public void setProduct(final Product product)
	{
		this.product = product;
	}

	/**
	 * Gets the number of products in this product container.
	 *
	 * @return the number of products in the products container.
	 */
	public int getProductCount()
	{
		return productCount;
	}

	/**
	 * Sets the number of products in this product container.
	 *
	 * @param count the new product count
	 */
	public void setProductCount(final int count)
	{
		productCount = count;
	}

	/**
	 * Removes products from this product container.
	 *
	 * @param count number of products to remove
	 * @return true or false.
	 */
	public boolean removeProduct(final int count)
	{
		if (this.productCount > 0)
		{
			if ((this.productCount - count) >= 0)
			{
				this.productCount = this.productCount - count;
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Adds product to this product container.
	 *
	 * @param count
	 * @return true or false.
	 */
	public boolean addProduct(final int count)
	{
		if ((this.productCount + count) > maxSize)
		{
			return false;
		}
		this.productCount = this.productCount + count;
		return true;
	}

	/**
	 * Gets the product category type.
	 *
	 * @return the product category type.
	 */
	public ProductType getBoxType()
	{
		return product.getCategory().getType();
	}

	/**
	 * Sets the type of the products in this product container.
	 *
	 * @param type the new product type
	 */
	public void setBoxType(final ProductType type)
	{
		product.getCategory().setType(type);
	}

	/**
	 * Gets the shelf slot ID of this product container
	 *
	 * @return the shelf slot of this product container
	 */
	public ShelfSlot getShelfSlot()
	{
		return shelfSlot;
	}

	/**
	 * Sets the shelf slot of this product container.
	 *
	 * @param shelfSlot the shelf slot object
	 */
	public void setShelfSlot(final ShelfSlot shelfSlot)
	{
		this.shelfSlot = shelfSlot;
	}

	public RemovalList getRemovalList()
	{
		return removalList;
	}

	public void setRemovalList(final RemovalList removalList)
	{
		this.removalList = removalList;
	}

	public Manifest getManifest()
	{
		return manifest;
	}

	public void setManifest(final Manifest manifest)
	{
		this.manifest = manifest;
	}

	/**
	 * Gets the expiration date of the products in this product container.
	 *
	 * @return the expiration date of this container
	 */
	public Date getExpirationDate()
	{
		return expirationDate;
	}

	/**
	 * Sets the expiration date of the products in this product container.
	 *
	 * @param date the new expiration date
	 */
	public void setExpirationDate(final Date date)
	{
		expirationDate = date;
	}

	/**
	 * Sets a new removal list for this product container by the database ID of the removal list.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param removalListID the database ID of the new removal list of this product container
	 * @see DatabaseController#getRemovalListByID(int)
	 */
	public void setRemovalListID(final int removalListID)
	{
		if (removalListID > 0)
			this.removalList = DatabaseController.getInstance().getRemovalListByID(removalListID);
	}

	/**
	 * Sets a new manifest for this product container by the database ID of the manifest.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param manifestID the database ID of the new manifest of this product container
	 * @see DatabaseController#getManifestByID(int)
	 */
	public void setManifestID(final int manifestID)
	{
		if (manifestID > 0)
			this.manifest = DatabaseController.getInstance().getManifestByID(manifestID);
	}

	/**
	 * Sets a new shelf slot for this product container by the database ID of the shelf slot.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param shelfSlotID the database ID of the new shelf slot of this product container
	 * @see DatabaseController#getShelfSlotByID(int)
	 */
	public void setShelfSlotID(final int shelfSlotID)
	{
		if (shelfSlotID > 0)
			this.shelfSlot = DatabaseController.getInstance().getShelfSlotByID(shelfSlotID);
	}

	/**
	 * Sets a new product for this product container by the database ID of the product.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param productID the database ID of the new product of this product container
	 * @see DatabaseController#getProductByID(int)
	 */
	public void setProductID(final int productID)
	{
		if (productID > 0)
			this.product = DatabaseController.getInstance().getProductByID(productID);
	}

	/**
	 * Sets the expiration date of the products in this product container.
	 * <blockquote>
	 * The string must be formatted as follows: <code>yyyy-MM-dd</code>
	 * </blockquote>
	 *
	 * @param dateString the new expiration date as a string
	 */
	public void setExpirationDateString(final String dateString) throws ParseException
	{
		if (dateString != null && !dateString.trim().isEmpty())
			this.expirationDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
	}
}
