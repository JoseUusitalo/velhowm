package velho.model;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * The Hibernate {@link SessionFactory} singleton.
 *
 * @author Jose Uusitalo
 */
public abstract class HibernateSessionFactory
{
	/**
	 * A private inner class holding the session factory instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link SessionFactory}.
		 */
		private static final SessionFactory INSTANCE = new Configuration().configure().buildSessionFactory();
	}

	/**
	 */
	private HibernateSessionFactory()
	{
		// No need to instantiate this class.
	}

	/**
	 * Gets the instance of the {@link SessionFactory}.
	 *
	 * @return the session factory
	 */
	public static synchronized SessionFactory getInstance()
	{
		return Holder.INSTANCE;
	}
}