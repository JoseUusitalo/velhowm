package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.RemovalPlatform;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link RemovalPlatform} objects.
 *
 * @author Jose Uusitalo
 */
public class RemovalPlatformValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		RemovalPlatform platform = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof RemovalPlatform))
				invalids.add(object);

			platform = (RemovalPlatform) object;

			if (Double.compare(platform.getFreeSpacePercent(), 0.0) < 0)
				invalids.add(platform);
		}

		return invalids;
	}
}
