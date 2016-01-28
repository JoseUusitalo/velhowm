package velho.model.exceptions;

/**
 * Thrown when attempting to manipulate the database when no database link exists.
 *
 * @author Jose Uusitalo
 */
public class NoDatabaseLinkException extends Exception
{
	private static final long serialVersionUID = 6359886222994365231L;

	public NoDatabaseLinkException()
	{
	}

	public NoDatabaseLinkException(final String message)
	{
		super(message);
	}

	public NoDatabaseLinkException(final Throwable cause)
	{
		super(cause);
	}

	public NoDatabaseLinkException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}