package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.ProductBox;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductBox} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductBoxValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		ProductBox box = null;

		for (final AbstractDatabaseObject object : validDataSet)
		{
			if (!(object instanceof ProductBox))
				invalids.add(object);

			box = (ProductBox) object;

			if (box.getProduct() == null || box.getMaxSize() < 1 || box.getProductCount() < 0)
				invalids.add(box);
		}

		return invalids;
	}
}
