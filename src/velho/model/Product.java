package velho.model;

import java.util.Date;

/**
 * The Product class.
 * 
 * @author Joona &amp; Jose Uusitalo
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
	 * The category of the product.
	 */
	private ProductCategory category;
	/**
	 * Popularity of the product.
	 */
	private int popularity;

	/**
	 * @param name
	 * @param expirationDate
	 * @param productID
	 * @param brand
	 * @param category
	 * 
	 */
	public Product(int databaseID, String name, Date expirationDate, ProductBrand brand, ProductCategory category, int popularity)
	{
		this.productID = databaseID;
		this.name = name;
		this.expirationDate = expirationDate;
		this.brand = brand;
		this.category = category;
		this.popularity = popularity;
	}

	@Override
	public String toString()
	{
		return "[" + productID + "] " + name + " (" + brand + " / " + category + "), Expires: " + expirationDate + ", Popularity: " + popularity;
	}

	/**
	 * Gets the popularity of the product.
	 * 
	 * @return the popularity of the product.
	 * 
	 */
	public int getPopularity()
	{
		return popularity;
	}

	/**
	 * Sets the popularity value.
	 * 
	 * @param popularity
	 * 
	 */
	public void setPopularity(int popularity)
	{
		this.popularity = popularity;
	}

	/**
	 * Gets the name of the product.
	 * 
	 * @return the name of the product.
	 * 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the expiration date of the product.
	 * 
	 * @return the expiration date of the product.
	 * 
	 */
	public Date getExpirationDate()
	{
		return expirationDate;
	}

	/**
	 * Gets the ID of the product.
	 * 
	 * @return the ID of the product.
	 * 
	 */
	public int getProductID()
	{
		return productID;
	}

	/**
	 * Gets the brand of the product.
	 * 
	 * @return the brand of the product.
	 * 
	 */
	public ProductBrand getBrand()
	{
		return brand;
	}

	/**
	 * Gets the category of the product.
	 * 
	 * @return the category of the product.
	 * 
	 */
	public ProductCategory getCategory()
	{
		return category;
	}

}
