package velho.model.data;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import velho.controller.database.DatabaseController;
import velho.model.CSVLoader;
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

			final CSVLoader<User> csvLoader = new CSVLoader<User>(User.class);
			csvLoader.load("res/sample_users.csv");
			csvLoader.save();
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

			final CSVLoader<ProductBrand> csvLoader = new CSVLoader<ProductBrand>(ProductBrand.class);
			csvLoader.load("res/sample_product_brands.csv");
			csvLoader.save();
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

			final CSVLoader<ProductType> csvLoader = new CSVLoader<ProductType>(ProductType.class);
			csvLoader.load("res/sample_product_types.csv");
			csvLoader.save();
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

			final CSVLoader<ProductCategory> csvLoader = new CSVLoader<ProductCategory>(ProductCategory.class);
			csvLoader.load("res/sample_product_categories.csv");
			csvLoader.save();
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

			final CSVLoader<Product> csvLoader = new CSVLoader<Product>(Product.class);
			csvLoader.load("res/sample_products.csv");
			csvLoader.save();
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

			final CSVLoader<Shelf> csvLoader = new CSVLoader<Shelf>(Shelf.class);
			csvLoader.load("res/sample_shelves.csv");
			csvLoader.save();
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

			final CSVLoader<ShelfLevel> csvLoader = new CSVLoader<ShelfLevel>(ShelfLevel.class);
			csvLoader.load("res/sample_shelf_levels.csv");
			csvLoader.save();
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

			final CSVLoader<ShelfSlot> csvLoader = new CSVLoader<ShelfSlot>(ShelfSlot.class);
			csvLoader.load("res/sample_shelf_slots.csv");
			csvLoader.save();
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

			final CSVLoader<ManifestState> csvLoader = new CSVLoader<ManifestState>(ManifestState.class);
			csvLoader.load("res/sample_manifest_states.csv");
			csvLoader.save();
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

			final CSVLoader<Manifest> csvLoader = new CSVLoader<Manifest>(Manifest.class);
			csvLoader.load("res/sample_manifests.csv");
			csvLoader.save();
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

			final CSVLoader<RemovalListState> csvLoader = new CSVLoader<RemovalListState>(RemovalListState.class);
			csvLoader.load("res/sample_removal_list_states.csv");
			csvLoader.save();
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

			final CSVLoader<RemovalList> csvLoader = new CSVLoader<RemovalList>(RemovalList.class);
			csvLoader.load("res/sample_removal_lists.csv");
			csvLoader.save();
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

			final CSVLoader<ProductBox> csvLoader = new CSVLoader<ProductBox>(ProductBox.class);
			csvLoader.load("res/sample_product_boxes.csv");
			csvLoader.save();
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

			final CSVLoader<RemovalPlatform> csvLoader = new CSVLoader<RemovalPlatform>(RemovalPlatform.class);
			csvLoader.load("res/sample_removal_platforms.csv");
			csvLoader.save();
		}
	}
}
