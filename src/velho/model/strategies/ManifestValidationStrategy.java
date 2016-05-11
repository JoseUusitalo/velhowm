package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.model.AbstractDatabaseObject;
import velho.model.Manifest;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link Manifest} objects.
 *
 * @author Jose Uusitalo
 */
public class ManifestValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		Manifest manifest = null;

		for (final AbstractDatabaseObject object : validDataSet)
		{
			if (!(object instanceof Manifest))
				invalids.add(object);

			manifest = (Manifest) object;

			if (manifest.getState() == null || manifest.getOrderedDate() == null || manifest.getReceivedDate() == null
					|| manifest.getOrderedDate().after(manifest.getReceivedDate()))
				invalids.add(object);
		}

		return invalids;
	}

}
