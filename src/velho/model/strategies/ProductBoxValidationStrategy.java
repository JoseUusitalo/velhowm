package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.ProductBox;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductBox} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductBoxValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		ProductBox box = null;

		for (final DatabaseObject object : validDataSet)
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
