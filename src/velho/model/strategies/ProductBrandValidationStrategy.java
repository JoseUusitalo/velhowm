package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.ProductBrand;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductBrand} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductBrandValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		ProductBrand brand = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof ProductBrand))
				invalids.add(object);

			brand = (ProductBrand) object;

			if (brand.getName() == null || brand.getName().isEmpty())
				invalids.add(brand);
		}

		return invalids;
	}
}
