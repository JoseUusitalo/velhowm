package velho.model;

/**
 * The freezer class.
 * @author Joona &amp; Jose Uusitalo
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

		if (this.getBoxType().getName() == "Regular")
		{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString()
	{
		return "[" + this.databaseID + "] Freezer: " + this.product.getName() + " (" + this.productCount + ")";
	}
}