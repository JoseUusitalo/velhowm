package velho.model;

import java.util.Date;

/**
 * The Product class.
 * @author Joona
 *
 */
public class Product
{
	/**
	 * The name of the product.
	 */
	private String name;
	/**
	 * The expiration date of the product.
	 */
	private Date expirationDate;
	/**
	 * The ID of the product.
	 */
	private int productID;
	/**
	 * The brand of the product.
	 */
	private ProductBrand brand;
	/**
	 * The type of the product.
	 */
	private ProductType type;
	/**
	 * Popularity of the product.
	 */
	private int popularity;
	
	/**
	 * @param name
	 * @param expirationDate
	 * @param productID
	 * @param brand
	 * @param type
	 */
	public Product(String name, Date expirationDate, int productID, ProductBrand brand, ProductType type)
	{
		this.name = name;
		this.expirationDate = expirationDate;
		this.productID = productID;
		this.brand = brand;
		this.type = type;
	}

	/**
	 * Gets the popularity of the product.
	 * @return the popularit of the product.
	 */
	public int getPopularity()
	{
		return popularity;
	}

	/**
	 * Sets the popularity value.
	 * @param popularity
	 */
	public void setPopularity(int popularity)
	{
		this.popularity = popularity;
	}

	/**
	 * Gets the name of the product.
	 * @return the name of the product.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the expiration date of the product.
	 * @return the expiration date of the product.
	 */
	public Date getExpirationDate()
	{
		return expirationDate;
	}

	/**
	 * Gets the ID of the product.
	 * @return the ID of the product.
	 */
	public int getProductID()
	{
		return productID;
	}

	/**
	 * Gets the brand of the product.
	 * @return the brand of the product.
	 */
	public ProductBrand getBrand()
	{
		return brand;
	}

	/**
	 * Gets the type of the product.
	 * @return the type of the product.
	 */
	public ProductType getType()
	{
		return type;
	}
	
	
}

