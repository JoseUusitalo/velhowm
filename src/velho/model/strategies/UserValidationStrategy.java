package velho.model.strategies;

import java.util.HashSet;
import java.util.Set;

import velho.controller.UserController;
import velho.model.AbstractDatabaseObject;
import velho.model.User;
import velho.model.interfaces.ObjectValidationStrategy;

/**
 * An {@link ObjectValidationStrategy} for {@link User} objects.
 *
 * @author Jose Uusitalo
 */
public class UserValidationStrategy implements ObjectValidationStrategy
{
	@Override
	public Set<AbstractDatabaseObject> getInvalidObjects(final Set<AbstractDatabaseObject> validDataSet)
	{
		final Set<AbstractDatabaseObject> invalids = new HashSet<AbstractDatabaseObject>();
		User user = null;

		for (final AbstractDatabaseObject object : validDataSet)
		{
			if (!(object instanceof User))
				invalids.add(object);

			user = (User) object;

			if (!isValidUser(user))
				invalids.add(user);
		}

		return invalids;
	}

	@SuppressWarnings("static-method")
	public boolean isValidUser(final User user)
	{
		final boolean hasBadgeID = UserController.getInstance().isValidBadgeID(user.getBadgeID());
		final boolean hasPIN = UserController.getInstance().isValidPIN(user.getPin());

		//@formatter:off
		if (hasBadgeID && hasPIN || !hasBadgeID && !hasPIN ||
			user.getFirstName() == null || user.getFirstName().trim().isEmpty() || user.getFirstName().length() > User.MAX_NAME_LENGTH ||
			user.getLastName() == null || user.getLastName().trim().isEmpty() || user.getLastName().length() > User.MAX_NAME_LENGTH ||
			user.getRole() == null)
		//@formatter:on
			return false;

		return true;
	}
}
