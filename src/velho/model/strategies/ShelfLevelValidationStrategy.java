package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.ShelfLevel;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ShelfLevel} objects.
 *
 * @author Jose Uusitalo
 */
public class ShelfLevelValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		ShelfLevel level = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof ShelfLevel))
				invalids.add(object);

			level = (ShelfLevel) object;

			if (level.getMaxShelfSlots() < 1 || level.getParentShelf() == null || level.getShelfPosition() < 1)
				invalids.add(level);
		}

		return invalids;
	}
}
