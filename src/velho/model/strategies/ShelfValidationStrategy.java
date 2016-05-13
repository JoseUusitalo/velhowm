package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.Shelf;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link Shelf} objects.
 *
 * @author Jose Uusitalo
 */
public class ShelfValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		Shelf shelf = null;

		for (final DatabaseObject object : validDataSet)
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
