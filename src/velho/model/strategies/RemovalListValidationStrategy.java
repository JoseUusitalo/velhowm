package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.RemovalList;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link RemovalList} objects.
 *
 * @author Jose Uusitalo
 */
public class RemovalListValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		RemovalList list = null;

		for (final DatabaseObject object : validDataSet)
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
