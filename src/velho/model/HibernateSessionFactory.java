package velho.model;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory
{
	// TODO: Use singleton pattern.

	private static final SessionFactory sessionFactory;

	static
	{
		try
		{
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex)
		{
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}
}