package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.ShelfSlot;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ShelfSlot} objects.
 *
 * @author Jose Uusitalo
 */
public class ShelfSlotValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		ShelfSlot slot = null;

		for (final AbstractDatabaseObject object : validDataSet)
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
