package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.ManifestState;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ManifestState} objects.
 *
 * @author Jose Uusitalo
 */
public class ManifestStateValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		ManifestState state = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof ManifestState))
				invalids.add(object);

			state = (ManifestState) object;

			if (state.getName() == null || state.getName().trim().isEmpty())
				invalids.add(state);
		}

		return invalids;
	}
}
