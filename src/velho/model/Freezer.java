package velho.model;

import velho.model.enums.ProductType;

/**
 * The freezer class.
 * @author Joona
 */
public class Freezer extends ProductContainer
{
	/**
	 * @param freezerID
	 * @param maxSize
	 * @param product
	 * @param productCount
	 * @param category
	 */
	public Freezer(int freezerID, int maxSize, Product product, int productCount)
	{
		super(freezerID, maxSize, product, productCount);

		if (this.getBoxType() == ProductType.REGULAR)
		{
			throw new IllegalArgumentException();
		}
	}

}