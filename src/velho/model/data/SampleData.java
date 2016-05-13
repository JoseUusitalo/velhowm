package velho.model.data;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import velho.controller.CSVController;
import velho.controller.database.DatabaseController;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.RemovalPlatform;
import velho.model.Shelf;
import velho.model.ShelfLevel;
import velho.model.ShelfSlot;
import velho.model.User;

/**
 * Example data to test the software.
 *
 * @author Jose Uusitalo
 */
public abstract class SampleData
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(SampleData.class.getName());

	/**
	 * Has the data been loaded?
	 */
	private static boolean dataLoaded = false;

	/**
	 * Saves all sample data objects into the database only if the data has not yet been loaded.
	 *
	 * @return <code>true</code> if data was (hopefully...) loaded
	 * @throws HibernateException
	 * @throws ParseException
	 */
	public static boolean createAll() throws HibernateException
	{
		if (dataLoaded)
		{
			SYSLOG.info("Sample data already exists.");
		}
		else
		{
			SYSLOG.debug("Loading sample data to database...");

			/*
			 * The order of these methods calls must never be changed in order to not break the referential integrity of the database.
			 */

			createSampleUsers();

			createSampleBrands();
			createSampleProductTypes();

			createSampleProductCategories();
			createSampleProducts();

			createSampleShelves();
			createSampleShelfLevels();
			createSampleShelfSlots();

			createSampleManifestStates();
			createSampleManifests();

			createSampleRemovalListStates();
			createSampleRemovalLists();

			createSampleProductBoxes();

			createSampleRemovalPlatforms();

			dataLoaded = true;
			SYSLOG.info("Sample data loaded.");
		}

		return dataLoaded;
	}

	/**
	 * Loads the sample users from the CSV file and saves them into the database.
	 */
	private static void createSampleUsers()
	{
		if (DatabaseController.getInstance().hasUsers())
		{
			SYSLOG.trace("Database already has users.");
		}
		else
		{
			SYSLOG.debug("Loading sample users.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_users.csv", User.class, true);
		}
	}

	/**
	 * Loads the sample product brands from the CSV file and saves them into the database.
	 */
	private static void createSampleBrands()
	{
		if (DatabaseController.getInstance().hasProductBrands())
		{
			SYSLOG.trace("Database already has product brands.");
		}
		else
		{
			SYSLOG.debug("Loading sample product brands.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_product_brands.csv", ProductBrand.class, true);
		}
	}

	/**
	 * Loads the sample product types from the CSV file and saves them into the database.
	 */
	private static void createSampleProductTypes()
	{
		if (DatabaseController.getInstance().hasProductTypes())
		{
			SYSLOG.trace("Database already has product types.");
		}
		else
		{
			SYSLOG.debug("Loading sample product types.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_product_types.csv", ProductType.class, true);
		}
	}

	/**
	 * Loads the sample product categories from the CSV file and saves them into the database.
	 */
	private static void createSampleProductCategories()
	{
		if (DatabaseController.getInstance().hasProductCategories())
		{
			SYSLOG.trace("Database already has product categories.");
		}
		else
		{
			SYSLOG.debug("Loading sample product categories.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_product_categories.csv", ProductCategory.class, true);
		}
	}

	/**
	 * Loads the sample products from the CSV file and saves them into the database.
	 */
	private static void createSampleProducts()
	{
		if (DatabaseController.getInstance().hasProducts())
		{
			SYSLOG.trace("Database already has products.");
		}
		else
		{
			SYSLOG.debug("Loading sample products.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_products.csv", Product.class, true);
		}
	}

	/**
	 * Loads the sample shelves from the CSV file and saves them into the database.
	 */
	private static void createSampleShelves()
	{
		if (DatabaseController.getInstance().hasShelves())
		{
			SYSLOG.trace("Database already has shelves.");
		}
		else
		{
			/* 1 - Tiny Full Shelf: 1-4 */
			/* 2 - Slot Full Shelf: 5-9 */
			/* 3 - One Slot Level: 10-13 */
			/* 4 - Many Slot Level: 14-20 */
			/* 5 - Empty Shelf 2-slot */
			SYSLOG.debug("Loading sample shelves.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_shelves.csv", Shelf.class, true);
		}
	}

	/**
	 * Loads the sample shelf levels from the CSV file and saves them into the database.
	 */
	private static void createSampleShelfLevels()
	{
		if (DatabaseController.getInstance().hasShelfLevels())
		{
			SYSLOG.trace("Database already has shelf levels.");
		}
		else
		{
			SYSLOG.debug("Loading sample shelf levels.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_shelf_levels.csv", ShelfLevel.class, true);
		}
	}

	/**
	 * Loads the sample shelf slots from the CSV file and saves them into the database.
	 */
	private static void createSampleShelfSlots()
	{
		if (DatabaseController.getInstance().hasShelfSlots())
		{
			SYSLOG.trace("Database already has shelf slots.");
		}
		else
		{
			SYSLOG.debug("Loading sample shelf slots.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_shelf_slots.csv", ShelfSlot.class, true);
		}
	}

	/**
	 * Loads the sample manifest states from the CSV file and saves them into the database.
	 */
	private static void createSampleManifestStates()
	{
		if (DatabaseController.getInstance().hasManifestStates())
		{
			SYSLOG.trace("Database already has manifest states.");
		}
		else
		{
			SYSLOG.debug("Loading sample manifest states.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_manifest_states.csv", ManifestState.class, true);
		}
	}

	/**
	 * Loads the sample manifests from the CSV file and saves them into the database.
	 */
	private static void createSampleManifests()
	{
		if (DatabaseController.getInstance().hasManifests())
		{
			SYSLOG.trace("Database already has manifests.");
		}
		else
		{
			SYSLOG.debug("Loading sample manifests.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_manifests.csv", Manifest.class, true);
		}
	}

	/**
	 * Loads the sample removal list states from the CSV file and saves them into the database.
	 */
	private static void createSampleRemovalListStates()
	{
		if (DatabaseController.getInstance().hasRemovalListStates())
		{
			SYSLOG.trace("Database already has removal list states.");
		}
		else
		{
			SYSLOG.debug("Loading sample removal list states.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_removal_list_states.csv", RemovalListState.class, true);
		}
	}

	/**
	 * Loads the sample removal lists from the CSV file and saves them into the database.
	 */
	private static void createSampleRemovalLists()
	{
		if (DatabaseController.getInstance().hasRemovalLists())
		{
			SYSLOG.trace("Database already has removal lists.");
		}
		else
		{
			SYSLOG.debug("Loading sample removal lists.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_removal_lists.csv", RemovalList.class, true);
		}
	}

	/**
	 * Loads the sample product boxes from the CSV file and saves them into the database.
	 */
	private static void createSampleProductBoxes()
	{
		if (DatabaseController.getInstance().hasProductBoxes())
		{
			SYSLOG.trace("Database already has product boxes.");
		}
		else
		{
			SYSLOG.debug("Loading sample product boxes.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_product_boxes.csv", ProductBox.class, true);
		}
	}

	/**
	 * Loads the sample removal platforms from the CSV file and saves them into the database.
	 */
	private static void createSampleRemovalPlatforms()
	{
		if (DatabaseController.getInstance().hasRemovalPlatforms())
		{
			SYSLOG.trace("Database already has removal platforms.");
		}
		else
		{
			SYSLOG.debug("Loading sample removal platforms.");
			CSVController.getInstance().loadCSVFileToDatabase("res/sample_removal_platforms.csv", RemovalPlatform.class, true);
		}
	}
}
