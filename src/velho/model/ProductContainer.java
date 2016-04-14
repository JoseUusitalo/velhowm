package velho.model;

import java.util.Date;

/**
 * An abstract container that can hold any number of {@link Product} objects.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public abstract class ProductContainer extends AbstractDatabaseObject implements Comparable<ProductContainer>
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
	 * For {@link ProductBox}: the shelf slot id.
	 */
	protected ShelfSlot shelfSlot;

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
		setDatabaseID(boxID);
		this.product = product;
		this.productCount = productCount;
	}

	/**
	 */
	public ProductContainer()
	{
		// For Hibernate.
	}

	@Override
	public abstract String toString();

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ProductContainer))
			return false;

		final ProductContainer c = (ProductContainer) o;

		if (this.getDatabaseID() <= 0)
			return this == c;

		return this.getDatabaseID() == c.getDatabaseID();
	}

	@Override
	public int compareTo(final ProductContainer container)
	{
		return this.getDatabaseID() - container.getDatabaseID();
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
}
