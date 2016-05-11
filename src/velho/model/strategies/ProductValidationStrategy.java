package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.Product;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link Product} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		Product product = null;

		for (final AbstractDatabaseObject object : validDataSet)
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
