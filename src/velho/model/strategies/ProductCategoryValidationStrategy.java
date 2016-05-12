package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.ProductCategory;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductCategory} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductCategoryValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		ProductCategory category = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof ProductCategory))
				invalids.add(object);

			category = (ProductCategory) object;

			if (category.getName() == null || category.getName().isEmpty() || category.getType() == null)
				invalids.add(category);
		}

		return invalids;
	}
}
