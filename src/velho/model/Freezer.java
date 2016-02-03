package velho.model;

/**
 * The freezer class.
 * @author Joona
 *
 */
public class Freezer
{
	/**
	 * The maximum size of the freezer.
	 */
	private int maxSize;
	/**
	 * The ID of the freezer.
	 */
	private int freezerID;
	/**
	 * The product.
	 */
	private Product product;
	/**
	 * The number of products in the freezer.
	 */
	private int productCount;

	/**
	 * @param freezerID
	 * @param maxSize
	 * @param product
	 * @param productCount
	 */
	public Freezer(int freezerID, int maxSize, Product product, int productCount)
	{
		if (maxSize < 1 || maxSize < productCount)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			this.maxSize = maxSize;
		}
		this.freezerID = freezerID;
		this.product = product;
	}

	/**
	 * Gets the maximum size of the freezer.
	 * @return the maximum size of the freezer.
	 */
	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * Gets the ID of the freezer.
	 * @return the ID of the freezer.
	 */
	public int getFreezerID()
	{
		return freezerID;
	}

	/**
	 * Gets the product of the freezer.
	 * @return the product of the freezer.
	 */
	public Product getProduct()
	{
		return product;
	}

	/**
	 * Gets the number of products in the freezer.
	 * @return the number of products in the freezer.
	 */
	public int getProductCount()
	{
		return productCount;
	}

	/**
	 * Removes products from the freezer.
	 * @param productCount
	 * @return true or false.
	 */
	public boolean removeProduct(int productCount)
	{
		if (this.productCount > 0)
		{
			if ((this.productCount - productCount) >= 0)
			{
				this.productCount = this.productCount - productCount;
				return true;
			}

			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}

	}

	/**
	 * Adds product to the freezer.
	 * @param productCount
	 * @return true or false.
	 */
	public boolean addProduct(int productCount)
	{
		if ((this.productCount + productCount) > maxSize)
		{
			return false;
		}
		if (productCount <= maxSize)
		{
			this.productCount = this.productCount + productCount;

			return true;
		}
		else
		{
			return false;
		}
	}
}