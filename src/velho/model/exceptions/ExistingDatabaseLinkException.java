package velho.model.exceptions;

/**
 * Thrown when attempting to manipulate the database when no database link exists.
 *
 * @author Jose Uusitalo
 */
public class ExistingDatabaseLinkException extends Exception
{
	private static final long serialVersionUID = 6359886222994365231L;

	public ExistingDatabaseLinkException()
	{
	}

	public ExistingDatabaseLinkException(final String message)
	{
		super(message);
	}

	public ExistingDatabaseLinkException(final Throwable cause)
	{
		super(cause);
	}

	public ExistingDatabaseLinkException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}