package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.RemovalList;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link RemovalList} objects.
 *
 * @author Jose Uusitalo
 */
public class RemovalListValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		RemovalList list = null;

		for (final AbstractDatabaseObject object : validDataSet)
		{
			if (!(object instanceof RemovalList))
				invalids.add(object);

			list = (RemovalList) object;

			if (list.getState() == null)
				invalids.add(list);
		}

		return invalids;
	}
}
