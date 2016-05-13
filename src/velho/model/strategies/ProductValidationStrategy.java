package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.Product;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link Product} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		Product product = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof Product))
				invalids.add(object);

			product = (Product) object;

			if (product.getName() == null || product.getName().isEmpty() || product.getBrand() == null || product.getCategory() == null)
				invalids.add(product);
		}

		return invalids;
	}
}
