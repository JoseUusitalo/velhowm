package velho.model;

import velho.model.enums.ProductType;

public abstract class ProductContainer
{
	/**
	 * The maximum size of the product box.
	 * 
	 */
	private int maxSize;
	/**
	 * The ID of the product box.
	 * 
	 */
	private int boxID;
	/**
	 * The product.
	 * 
	 */
	private Product product;
	/**
	 * The number of products in the product box.
	 * 
	 */
	private int productCount;

	public ProductContainer(int boxID, int maxSize, Product product, int productCount)
	{
		if (maxSize < 1 || maxSize < productCount)
		{
			throw new IllegalArgumentException();
		}
		this.maxSize = maxSize;
		this.boxID = boxID;
		this.product = product;
	}

	/**
	 * Gets the maximum size of the product box.
	 * 
	 * @return the maximum size of the product box.
	 * 
	 */
	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * Gets the ID of the product box.
	 * 
	 * @return the ID of the product box.
	 * 
	 */
	public int getBoxID()
	{
		return boxID;
	}

	/**
	 * Gets the product of the product box.
	 * 
	 * @return the product of the product box.
	 * 
	 */
	public Product getProduct()
	{
		return product;
	}

	/**
	 * Gets the number of products in the product box.
	 * 
	 * @return the number of products in the products box.
	 * 
	 */
	public int getProductCount()
	{
		return productCount;
	}

	/**
	 * Removes products from a product box.
	 * 
	 * @param productCount
	 * 
	 * @return true or false.
	 * 
	 */
	public boolean removeProduct(int count)
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
	 * 
	 * @return true or false.
	 * 
	 */
	public boolean addProduct(int count)
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
	 * 
	 */
	public ProductType getBoxType()
	{
		return product.getCategory().getType();
	}
}
