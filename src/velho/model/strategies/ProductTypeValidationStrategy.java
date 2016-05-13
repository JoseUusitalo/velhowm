package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.ProductType;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductType} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductTypeValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		ProductType type = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof ProductType))
				invalids.add(object);

			type = (ProductType) object;

			if (type.getName() == null || type.getName().isEmpty())
				invalids.add(type);
		}

		return invalids;
	}
}
