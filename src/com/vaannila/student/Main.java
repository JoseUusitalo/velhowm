package com.vaannila.student;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaannila.util.HibernateUtil;

import velho.controller.DatabaseController;
import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

public class Main
{

	@SuppressWarnings("unchecked")
	public static void main(final String[] args) throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseLinkException
	{
		DatabaseController.link();
		DatabaseController.initializeDatabase();
		DatabaseController.unlink();

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			ProductType type = new ProductType("hibtyp");
			// By using cascade=all option the address need not be saved
			// explicitly when the student object is persisted the address will
			// be automatically saved.
			// session.save(address);
			ProductCategory cat = new ProductCategory("hibcat", type);
			session.save(cat);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
			e.printStackTrace();
		}
		finally
		{
			session.close();
		}

		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<ProductCategory> result = session.createQuery("from ProductCategory").list();
		System.out.println("---------------------------------------------------------------------------");
		for (ProductCategory event : result)
		{
			System.out.println(event.getDatabaseID() + " " + event.getName() + " " + event.getType().getName());
		}
		session.getTransaction().commit();
		session.close();

	}

}
