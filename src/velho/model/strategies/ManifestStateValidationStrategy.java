package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.ManifestState;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link ManifestState} objects.
 *
 * @author Jose Uusitalo
 */
public class ManifestStateValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		ManifestState state = null;

		for (final AbstractDatabaseObject object : validDataSet)
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
