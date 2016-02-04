package velho.model;

import velho.model.enums.ProductType;

/**
 * The ProductCategory class.
 * 
 * @author Joona
 */
public class ProductCategory
{
	/**
	 * The product category name.
	 * 
	 */
	private String name;
	/**
	 * The product category type.
	 * 
	 */
	private ProductType type;

	/**
	 * @param name
	 * @param type
	 * 
	 */
	public ProductCategory(String name, ProductType type)
	{
		
		this.type = type;
		this.name = name;
		
		if (this.type == null)
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Gets the product category name.
	 * 
	 * @return the product category name.
	 * 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the product category type.
	 * 
	 * @return the product category type.
	 * 
	 */
	public ProductType getType()
	{
		return type;
	}
}
