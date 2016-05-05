package velho.controller.database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import velho.model.AbstractDatabaseObject;

/**
 * <p>
 * A custom {@link IdentifierGenerator} for Hibernate.
 * The assigned identifier generator allows for manually assigned IDs and IDs generated with hibernate_sequence.
 * If the database ID of the object being saved is 0 or less, a new ID is generated.
 * Otherwise the assigned ID is used.
 * </p>
 * <p>
 * Additionally restricts database saves and updates to instances of {@link AbstractDatabaseObject} only.
 * </p>
 *
 * @author Jose Uusitalo
 * @see <a href="http://myjourneyonjava.blogspot.fi/2015/01/creating-custom-generator-class-in.html">Source (modified)</a>
 */
public class AssignedIdentifierGenerator implements IdentifierGenerator
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger DBLOG = Logger.getLogger("dbLogger");

	/**
	 * The name of the Hibernate sequence identifier generator which is used if the databse ID is not assigned.
	 */
	private String DEFAULT_SEQUENCE_NAME = "hibernate_sequence";

	@Override
	public Serializable generate(final SessionImplementor sessionImpl, final Object data) throws HibernateException, IllegalArgumentException
	{
		if (data instanceof AbstractDatabaseObject)
		{
			final int assignedID = ((AbstractDatabaseObject) data).getDatabaseID();

			if (assignedID > 0)
			{
				DBLOG.trace("Accepted manually assigned ID: " + assignedID);
				return assignedID;
			}

			Serializable result = null;

			// If at first you fail, just try try try again.
			// Try-with-resources is fancy.
			try (Connection connection = DatabaseController.getConnection())
			{
				try (final ResultSet resultSet = connection.createStatement().executeQuery("call next value for " + DEFAULT_SEQUENCE_NAME))
				{
					if (resultSet.next())
						result = resultSet.getInt(1);
				}
				catch (final Exception e2)
				{
					try
					{
						// If sequence is not found then create the sequence.
						connection.createStatement().execute("CREATE SEQUENCE " + DEFAULT_SEQUENCE_NAME);

						DBLOG.error("Failed to find identifier sequence " + DEFAULT_SEQUENCE_NAME + ". It was created manually.");

						try (final ResultSet resultSet = connection.createStatement().executeQuery("call next value for " + DEFAULT_SEQUENCE_NAME))
						{
							if (resultSet.next())
								result = resultSet.getInt(1);
						}
						catch (final SQLException e4)
						{
							e4.printStackTrace();
						}
					}
					catch (final Exception e3)
					{
						DBLOG.fatal("Failed to create sequence " + DEFAULT_SEQUENCE_NAME + ".");
						e3.printStackTrace();
					}
				}

				DBLOG.trace("Generated new ID: " + result);

				return result;
			}
			catch (final SQLException e1)
			{
				e1.printStackTrace();
			}
		}

		DBLOG.error("Cannot save an object that is not an instance of AbstractDatabaseObject.");

		throw new IllegalArgumentException("Only instances of AbstractDatabaseObject may be saved to the database.");
	}
}
