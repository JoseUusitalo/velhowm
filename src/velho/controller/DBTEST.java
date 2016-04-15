package velho.controller;

import velho.model.HibernateSessionFactory;
import velho.model.ProductBox;
import velho.model.Shelf;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseException;
import velho.model.exceptions.NoDatabaseLinkException;

public class DBTEST
{
	public static void main(final String[] args) throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseException, NoDatabaseLinkException
	{
		System.out.println("\n\nlink\n\n");
		System.out.println("STATE:              " + DatabaseController.link());

		// System.out.println("\n\nopen session\n\n");
		// DatabaseController.openSession();

		System.out.println("\n\nreset\n\n");
		System.out.println(DatabaseController.resetDatabase() + " = true");

		System.out.println("     session open:     " + HibernateSessionFactory.getInstance().getCurrentSession().isOpen());

		int boxProductCount = 2;
		int shelfID = 4;
		Shelf shelf = DatabaseController.getShelfByID(shelfID);
		ProductBox box = DatabaseController.getProductBoxByID(21);

		String slotid = shelf.getShelfID() + "-2-12";
		int oldBoxCount = shelf.getProductBoxes().size();
		int oldProductCount = shelf.getProductCountInBoxes();

		System.out.println("\n\n--START--\n\n");
		System.out.println(shelf);

		System.out.println("\nBefore " + shelf.getShelfSlot(slotid).getProductBoxes());
		System.out.println(shelf.getProductBoxes());

		System.out.println("\nAdd " + box);
		System.out.println(shelf);

		System.out.println("\nAdd to the object.");
		System.out.println(shelf.addToSlot(slotid, box) + " = true");

		System.out.println("\nAfter " + shelf.getShelfSlot(slotid).getProductBoxes());
		System.out.println(shelf.getProductBoxes());
		System.out.println(shelf);

		System.out.println("\nProduct box is in the shelf.");
		System.out.println(shelf.getProductBoxes().contains(box) + " = true");

		System.out.println("\nProduct box is in the slot.");
		System.out.println(shelf.getShelfSlot(slotid).contains(box) + " = true");

		System.out.println("\nNumber of product boxes in the shelf has increased by one.");
		System.out.println(oldBoxCount + 1 + " = " + shelf.getProductBoxes().size());

		System.out.println("\nNumber of products in the shelf has increased.");
		System.out.println(oldProductCount + boxProductCount + " = " + shelf.getProductCountInBoxes());

		System.out.println("\n-- Save to database --");
		DatabaseController.save(shelf);

		System.out.println("\nCheck that database has been updated.");
		shelf = DatabaseController.getShelfByID(shelfID);
		System.out.println("Fresh: " + DatabaseController.getShelfByID(shelfID));
		System.out.println(shelf.getProductBoxes());

		System.out.println("\nProduct box is in the shelf.");
		System.out.println(shelf.getProductBoxes().contains(box) + " = true");

		System.out.println("\nProduct box is in the slot.");
		System.out.println(shelf.getShelfSlot(slotid).getProductBoxes() + " is set\n");
		System.out.println(shelf.getShelfSlot(slotid).getProductBoxes().contains(box) + " = true");
		System.out.println(shelf.getShelfSlot(slotid).getProductBoxes().iterator().next() + " is set box");
		System.out.println(shelf.getShelfSlot(slotid).getProductBoxes().iterator().next().equals(box) + " is trueee");
		System.out.println(shelf.getShelfSlot(slotid).contains(box) + " = true");
		System.out.println(shelf.getShelfSlot(slotid).getProductBoxes().contains(box) + " in set");

		System.out.println("\nNumber of product boxes in the shelf has increased by one.");
		System.out.println(oldBoxCount + 1 + " = " + shelf.getProductBoxes().size());

		System.out.println("\nNumber of products in the shelf has increased.");
		System.out.println(oldProductCount + boxProductCount + " = " + shelf.getProductCountInBoxes());

		// System.out.println("\n\nclose session\n\n");
		// DatabaseController.closeSession();

		System.out.println("\n\nclose factory\n\n");
		DatabaseController.closeSessionFactory();

		System.out.println("\n\nunlink\n\n");
		DatabaseController.unlink();

		System.out.println("\n--DONE--");
	}
}
