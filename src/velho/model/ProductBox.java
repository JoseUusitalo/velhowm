package velho.model;

import velho.model.enums.ProductType;

/**
 * The ProductBox class.
 * 
 * @author Joona
 */
public class ProductBox extends ProductContainer
{
	public ProductBox(int boxID, int maxSize, Product product, int productCount)
	{
		super(boxID, maxSize, product, productCount);
		
		if (this.getBoxType() == ProductType.COLD)
		{
			throw new IllegalArgumentException();
		}
	}
}
