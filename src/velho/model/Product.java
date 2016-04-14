package velho.model;

/**
 * A class representing a generic product of any type, shape, or size.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class Product extends AbstractDatabaseObject implements Comparable<Product>
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
	 * Popularity of this product.
	 */
	private int popularity;

	/**
	 * @param name
	 * @param databaseID
	 * @param brand
	 * @param category
	 * @param popularity
	 */
	public Product(final int databaseID, final String name, final ProductBrand brand, final ProductCategory category, final int popularity)
	{
		setDatabaseID(databaseID);
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.popularity = popularity;
	}

	public Product(final String name, final ProductBrand brand, final ProductCategory category, final int popularity)
	{
		setDatabaseID(0);
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.popularity = popularity;
	}

	/**
	 */
	public Product()
	{
		// For Hibernate.
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] " + name + " (" + brand + " / " + category + "), Popularity: " + popularity;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof Product))
			return false;

		final Product p = (Product) o;

		if (this.getDatabaseID() <= 0)
			return this == p;

		return this.getDatabaseID() == p.getDatabaseID();
	}

	@Override
	public int compareTo(final Product product)
	{
		return this.getName().compareTo(product.getName());
	}

	/**
	 * Gets the popularity of this product.
	 *
	 * @return the popularity of this product
	 */
	public int getPopularity()
	{
		return popularity;
	}

	/**
	 * Sets the popularity value.
	 *
	 * @param popularity the new popularity value
	 */
	public void setPopularity(final int popularity)
	{
		this.popularity = popularity;
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
