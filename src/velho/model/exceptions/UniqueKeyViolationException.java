package velho.model.exceptions;

/**
 * Thrown when attempting to manipulate the database when no database link exists.
 *
 * @author Jose Uusitalo
 */
public class UniqueKeyViolationException extends Exception
{
	private static final long serialVersionUID = 1721352489000119409L;

	public UniqueKeyViolationException()
	{
		// Silencing PMD.
	}

	public UniqueKeyViolationException(final String message)
	{
		super(message);
	}

	public UniqueKeyViolationException(final Throwable cause)
	{
		super(cause);
	}

	public UniqueKeyViolationException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}