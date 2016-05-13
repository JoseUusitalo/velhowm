package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.RemovalListState;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link RemovalListState} objects.
 *
 * @author Jose Uusitalo
 */
public class RemovalListStateValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		RemovalListState state = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof RemovalListState))
				invalids.add(object);

			state = (RemovalListState) object;

			if (state.getName() == null || state.getName().trim().isEmpty())
				invalids.add(state);
		}

		return invalids;
	}
}
