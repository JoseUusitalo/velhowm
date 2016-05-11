package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.ProductCategory;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductCategory} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductCategoryValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		ProductCategory category = null;

		for (final AbstractDatabaseObject object : validDataSet)
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
