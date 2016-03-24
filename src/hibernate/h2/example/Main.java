package hibernate.h2.example;

import java.io.File;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main
{

	public static void main(final String[] args)
	{
		User user = new User(1, "Levi");

		System.out.println(new File("hibernate.cfg.xml").exists());
		System.out.println(new File("hibernate.cfg.xml").getAbsolutePath());
		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		Session session = sessionFactory.openSession();

		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from User").list();
		for (User event : (List<User>) result)
		{
			System.out.println(event.getName());
		}
		session.getTransaction().commit();
		session.close();
	}

}
