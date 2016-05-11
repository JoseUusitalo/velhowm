package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.ProductBrand;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ProductBrand} objects.
 *
 * @author Jose Uusitalo
 */
public class ProductBrandValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		ProductBrand brand = null;

		for (final AbstractDatabaseObject object : validDataSet)
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
