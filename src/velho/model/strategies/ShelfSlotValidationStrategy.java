package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.ShelfSlot;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ShelfSlot} objects.
 *
 * @author Jose Uusitalo
 */
public class ShelfSlotValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		ShelfSlot slot = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof ShelfSlot))
				invalids.add(object);

			slot = (ShelfSlot) object;

			if (slot.getLevelPosition() < 1 || slot.getMaxProductBoxes() < 1)
				invalids.add(slot);
		}

		return invalids;
	}
}
