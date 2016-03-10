package velho.model;

import velho.controller.DatabaseController;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * The ProductCategory class.
 *
 * @author Joona Silvennoinen &amp; Jose Uusitalo
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
	 * The database ID of this product category.
	 */
	private int databaseID;

	/**
	 * @param name
	 * @param type
	 */
	public ProductCategory(final int databaseID, final String name, final ProductType type)
	{
		this.databaseID = databaseID;
		this.type = type;
		this.name = name;

		if (this.type == null)
		{
			throw new IllegalArgumentException();
		}
	}

	public ProductCategory(final String name) throws NoDatabaseLinkException
	{
		this.databaseID = -1;
		this.type = DatabaseController.getProductTypeByID(1); // TODO WRITE allow changeging type
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name + " (" + type + ")";
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

	/**
	 * Gets the database ID of this category.
	 *
	 * @return the database id
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}
}
