package velho.controller;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

public class Main
{

	public static void main(final String[] args) throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		DatabaseController.unlink();

		System.out.println("---------------------------");

		ProductType type = new ProductType("typetester");
		ProductCategory category = new ProductCategory("Hibcaht", type);

		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		Session session = sessionFactory.openSession();

		session.beginTransaction();
		session.save(category);
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<ProductCategory> result = session.createQuery("from ProductCategory").list();
		System.out.println("---------------------------------------------------------------------------");
		for (ProductCategory event : result)
		{
			System.out.println(event.getName() + " " + event.getDatabaseID() + " " + event.getType());
		}
		session.getTransaction().commit();
		session.close();
		sessionFactory.close();
	}

}
