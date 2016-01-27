package velho.model.exceptions;

/**
 * Thrown when attempting to manipulate the database when no database connection exists.
 *
 * @author Jose Uusitalo
 */
public class NoDatabaseConnectionException extends Exception
{
	private static final long serialVersionUID = 6359886222994365231L;

	public NoDatabaseConnectionException()
	{
	}

	public NoDatabaseConnectionException(final String message)
	{
		super(message);
	}

	public NoDatabaseConnectionException(final Throwable cause)
	{
		super(cause);
	}

	public NoDatabaseConnectionException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}