package velho.model;

import java.util.UUID;

/**
 * A class representing a generic product of any type, shape, or size.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class Product extends AbstractDatabaseObject
{
	/**
	 * The name of this product.
	 */
	private String name;

	/**
	 * The brand of this product.
	 */
	private ProductBrand brand;

	/**
	 * The category of this product.
	 */
	private ProductCategory category;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param name
	 * @param brand
	 * @param category
	 */
	public Product(final int databaseID, final UUID uuid, final String name, final ProductBrand brand, final ProductCategory category)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.name = name;
		this.brand = brand;
		this.category = category;
	}

	/**
	 * @param databaseID
	 * @param name
	 * @param brand
	 * @param category
	 */
	public Product(final int databaseID, final String name, final ProductBrand brand, final ProductCategory category)
	{
		this(databaseID, UUID.randomUUID(), name, brand, category);
	}

	/**
	 * @param name
	 * @param brand
	 * @param category
	 */
	public Product(final String name, final ProductBrand brand, final ProductCategory category)
	{
		this(0, UUID.randomUUID(), name, brand, category);
	}

	/**
	 */
	public Product()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] " + name + " (" + brand + " / " + category + ")";
	}

	@Override
	public int compareTo(final AbstractDatabaseObject product)
	{
		if (product instanceof Product)
			return this.getName().compareTo(((Product) product).getName());

		return super.compareTo(product);
	}

	/**
	 * Gets the name of this product.
	 *
	 * @return the name of this product
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this product.
	 *
	 * @param name the new name of this product
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * Gets the brand of this product.
	 *
	 * @return the brand of this product
	 */
	public ProductBrand getBrand()
	{
		return brand;
	}

	/**
	 * Sets the brand of this product.
	 *
	 * @param brand the new brand of this product
	 */
	public void setBrand(final ProductBrand brand)
	{
		this.brand = brand;
	}

	/**
	 * Gets the category of this product.
	 *
	 * @return the category of this product
	 */
	public ProductCategory getCategory()
	{
		return category;
	}

	/**
	 * Sets the category of this product.
	 *
	 * @param category the new category of this product
	 */
	public void setCategory(final ProductCategory category)
	{
		this.category = category;
	}
}
