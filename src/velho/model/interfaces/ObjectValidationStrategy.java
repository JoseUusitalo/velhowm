package velho.model.interfaces;

import java.util.Set;

import velho.model.AbstractDatabaseObject;

/**
 * A validation strategy for validating an {@link AbstractDatabaseObject}.
 *
 * @author Jose Uusitalo
 */
public interface ObjectValidationStrategy
{
	/**
	 * <p>
	 * Gets the contextually invalid objects from the specified set of technically valid objects.
	 * </p>
	 * <p>
	 * For example it is possible to create a {@link User} object that has both a PIN and a badge number, but the application assumes that all users have either
	 * a PIN or a badge number but not both.
	 * </p>
	 *
	 * @param validDataSet a set of valid objects
	 * @return a set of contextually invalid objects in the valid data set
	 */
	Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet);
}
