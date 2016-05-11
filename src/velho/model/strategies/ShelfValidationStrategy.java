package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.Shelf;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link Shelf} objects.
 *
 * @author Jose Uusitalo
 */
public class ShelfValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		Shelf shelf = null;

		for (final AbstractDatabaseObject object : validDataSet)
		{
			if (!(object instanceof Shelf))
				invalids.add(object);

			shelf = (Shelf) object;

			if (shelf.getLevelCount() < 1)
				invalids.add(shelf);
		}

		return invalids;
	}
}
