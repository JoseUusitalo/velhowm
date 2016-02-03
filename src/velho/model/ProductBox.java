package velho.model;

/**
 * The ProductBox class.
 * @author Joona
 *
 */
public class ProductBox
{
	/**
	 * The maximum size of the product box.
	 */
	private int maxSize;
	/**
	 * The ID of the product box.
	 */
	private int boxID;
	/**
	 * The product.
	 */
	private Product product;
	/**
	 * The number of products in the product box.
	 */
	private int productCount;

	/**
	 * @param boxID
	 * @param maxSize
	 * @param product
	 * @param productCount
	 */
	public ProductBox(int boxID, int maxSize, Product product, int productCount)
	{
		if (maxSize < 1 || maxSize < productCount)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			this.maxSize = maxSize;
		}
		this.boxID = boxID;
		this.product = product;
	}

	/**
	 * Gets the maximum size of the product box.
	 * @return the maximum size of the product box.
	 */
	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * Gets the ID of the product box.
	 * @return the ID of the product box.
	 */
	public int getBoxID()
	{
		return boxID;
	}

	/**
	 * Gets the product of the product box.
	 * @return the product of the product box.
	 */
	public Product getProduct()
	{
		return product;
	}

	/**
	 * Gets the number of products in the product box.
	 * @return the number of products in the products box.
	 */
	public int getProductCount()
	{
		return productCount;
	}

	/**
	 * Removes products from a product box.
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
	 * Adds product to the product box.
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
