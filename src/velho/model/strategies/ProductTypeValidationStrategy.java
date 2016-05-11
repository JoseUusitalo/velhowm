package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.ProductType;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductType} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductTypeValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		ProductType type = null;

		for (final AbstractDatabaseObject object : validDataSet)
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
