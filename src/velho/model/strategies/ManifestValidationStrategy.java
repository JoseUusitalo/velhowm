package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.Manifest;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link Manifest} objects.
 *
 * @author Jose Uusitalo
 */
public class ManifestValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<DatabaseObject> getInvalidObjects(final Set<DatabaseObject> validDataSet)
	{
		final Set<DatabaseObject> invalids = new HashSet<DatabaseObject>();
		Manifest manifest = null;

		for (final DatabaseObject object : validDataSet)
		{
			if (!(object instanceof Manifest))
				invalids.add(object);

			manifest = (Manifest) object;

			// @formatter:off
			if (manifest.getState() == null ||
				manifest.getOrderedDate() == null ||
				manifest.getReceivedDate() == null ||
				manifest.getOrderedDate().after(manifest.getReceivedDate()))
			// @formatter:on
				invalids.add(object);
		}

		return invalids;
	}
}
