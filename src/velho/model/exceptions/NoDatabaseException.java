package velho.model.exceptions;

/**
 * Thrown when attempting to manipulate the database when no database file exists.
 *
 * @author Jose Uusitalo
 */
public class NoDatabaseException extends Exception
{
	private static final long serialVersionUID = -1173887403038343638L;

	public NoDatabaseException()
	{
		// Silencing PMD.
	}

	public NoDatabaseException(final String message)
	{
		super(message);
	}

	public NoDatabaseException(final Throwable cause)
	{
		super(cause);
	}

	public NoDatabaseException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
