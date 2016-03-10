package velho.model;

import java.util.Date;

/**
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public abstract class ProductContainer
{
	/**
	 * The expiration date of the product.
	 */
	private Date expirationDate;

	/**
	 * The maximum size of the product box.
	 */
	protected int maxSize;
	/**
	 * The ID of the product box.
	 */
	protected int databaseID;
	/**
	 * The product.
	 */
	protected Product product;
	/**
	 * The number of products in the product box.
	 */
	protected int productCount;

	/**
	 * For {@link ProductBox}: the shelf slot id.
	 */
	protected String shelfSlot;

	/**
	 * @param boxID
	 * @param expirationDate
	 * @param maxSize
	 * @param product
	 * @param productCount
	 */
	public ProductContainer(final int boxID, final Date expirationDate, final int maxSize, final Product product, final int productCount)
	{
		if (maxSize < 1 || maxSize < productCount)
		{
			throw new IllegalArgumentException();
		}
		this.expirationDate = expirationDate;
		this.maxSize = maxSize;
		this.databaseID = boxID;
		this.product = product;
		this.productCount = productCount;
	}

	@Override
	public abstract String toString();

	/**
	 * Gets the maximum size of the product box.
	 *
	 * @return the maximum size of the product box.
	 */
	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * Gets the ID of the product box.
	 *
	 * @return the ID of the product box.
	 */
	public int getBoxID()
	{
		return databaseID;
	}

	/**
	 * Gets the product of the product box.
	 *
	 * @return the product of the product box.
	 */
	public Product getProduct()
	{
		return product;
	}

	/**
	 * Gets the number of products in the product box.
	 *
	 * @return the number of products in the products box.
	 */
	public int getProductCount()
	{
		return productCount;
	}

	/**
	 * Removes products from a product box.
	 *
	 * @param productCount
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
	 * Adds product to the product box.
	 *
	 * @param productCount
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
	 * Gets the shelf slot ID of this product container
	 *
	 * @return the shelf slot of this product container
	 */
	public String getShelfSlot()
	{
		return shelfSlot;
	}

	/**
	 * Sets the shelf slot ID of this product container
	 */
	public void setShelfSlot(final String shelfSlot)
	{
		this.shelfSlot = shelfSlot;
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
}
