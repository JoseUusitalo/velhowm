package velho.model;

import java.util.Date;

/**
 * A table row object containing the necessary data for display in search lists.
 *
 * @author Jose Uusitalo
 */
public class ProductBoxSearchResultRow
{
	/**
	 * The product.
	 */
	private ProductBox box;

	/**
	 * @param box
	 */
	public ProductBoxSearchResultRow(final ProductBox box)
	{
		this.box = box;
	}

	/**
	 * Gets the {@link ProductBox} object stored in this search result row.
	 *
	 * @return the product box in this row
	 */
	public ProductBox getBox()
	{
		return box;
	}

	/**
	 * Gets the number of products in the product box.
	 *
	 * @return the number of products in the products box.
	 */
	public int getBoxProductCount()
	{
		return box.getProductCount();
	}

	/**
	 * Gets the shelf slot this product box is in.
	 *
	 * @return the shelf slot of this product box
	 */
	public String getBoxShelfSlot()
	{
		return box.getShelfSlot();
	}

	/**
	 * Gets the name of the product.
	 *
	 * @return the name of the product.
	 *
	 */
	public String getProductName()
	{
		return box.getProduct().getName();
	}

	/**
	 * Gets the ID of the product.
	 *
	 * @return the ID of the product.
	 */
	public int getProductID()
	{
		return box.getProduct().getDatabaseID();
	}

	/**
	 * Gets the brand of the product.
	 *
	 * @return the brand of the product.
	 */
	public ProductBrand getProductBrand()
	{
		return box.getProduct().getBrand();
	}

	/**
	 * Gets the category of the product.
	 *
	 * @return the category of the product.
	 */
	public ProductCategory getProductCategory()
	{
		return box.getProduct().getCategory();
	}

	/**
	 * Gets the database ID of the product box.
	 *
	 * @return ID of the box
	 */
	public int getBoxID()
	{
		return box.getDatabaseID();
	}

	/**
	 * Gets the expiration date of the products in the product box.
	 *
	 * @return the expiration date of the product box
	 */
	public Date getExpirationDate()
	{
		return box.getExpirationDate();
	}
}
