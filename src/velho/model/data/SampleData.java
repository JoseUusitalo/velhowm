package velho.model.data;

import java.text.ParseException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import velho.controller.ProductController;
import velho.controller.ShelfController;
import velho.controller.UserController;
import velho.controller.database.DatabaseController;
import velho.model.AbstractDatabaseObject;
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
	public static boolean createAll() throws HibernateException, ParseException
	{
		if (!dataLoaded)
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
		else
			SYSLOG.info("Sample data already exists.");

		return dataLoaded;
	}

	/**
	 * Loads the sample users from the CSV file and saves them into the database.
	 */
	private static void createSampleUsers()
	{
		if (!DatabaseController.hasUsers())
		{
			SYSLOG.debug("Loading sample users.");

			final CSVLoader<User> csvLoader = new CSVLoader<User>(User.class)
			{
				@Override
				protected Set<User> getInvalidDataObjects(final Set<User> validDataSet)
				{
					return UserController.getInvalidUsers(validDataSet);
				}
			};

			csvLoader.load("data/sample_users.csv");
			csvLoader.save();

			for (Object obj : DatabaseController.getAllUsers())
				System.out.println("User: " + ((AbstractDatabaseObject) obj).getDatabaseID() + " " + obj);
		}
		else
			SYSLOG.trace("Database already has users.");
	}

	private static void createSampleBrands()
	{
		if (!DatabaseController.hasProductBrands())
		{
			SYSLOG.debug("Loading sample product brands.");

			final CSVLoader<ProductBrand> csvLoader = new CSVLoader<ProductBrand>(ProductBrand.class)
			{
				@Override
				protected Set<ProductBrand> getInvalidDataObjects(final Set<ProductBrand> validDataSet)
				{
					return ProductController.getInvalidProductBrands(validDataSet);
				}
			};

			csvLoader.load("data/sample_product_brands.csv");
			csvLoader.save();

			for (Object obj : DatabaseController.getAllProductBrands())
				System.out.println("Brand: " + ((AbstractDatabaseObject) obj).getDatabaseID() + " " + obj);
		}
		else
			SYSLOG.trace("Database already has product brands.");
	}

	private static void createSampleProductTypes()
	{
		if (!DatabaseController.hasProductTypes())
		{
			SYSLOG.debug("Loading sample product types.");

			final CSVLoader<ProductType> csvLoader = new CSVLoader<ProductType>(ProductType.class)
			{
				@Override
				protected Set<ProductType> getInvalidDataObjects(final Set<ProductType> validDataSet)
				{
					return ProductController.getInvalidProductTypes(validDataSet);
				}
			};

			csvLoader.load("data/sample_product_types.csv");
			csvLoader.save();

			for (Object obj : DatabaseController.getAllProductTypes())
				System.out.println("Type: " + ((AbstractDatabaseObject) obj).getDatabaseID() + " " + obj);
		}
		else
			SYSLOG.trace("Database already has product types.");
	}

	private static void createSampleProductCategories()
	{
		if (!DatabaseController.hasProductCategories())
		{
			SYSLOG.debug("Loading sample product categories.");

			final CSVLoader<ProductCategory> csvLoader = new CSVLoader<ProductCategory>(ProductCategory.class)
			{
				@Override
				protected Set<ProductCategory> getInvalidDataObjects(final Set<ProductCategory> validDataSet)
				{
					return ProductController.getInvalidProductCategories(validDataSet);
				}
			};

			csvLoader.load("data/sample_product_categories.csv");
			csvLoader.save();

			for (Object obj : DatabaseController.getAllProductCategories())
				System.out.println("Category: " + ((AbstractDatabaseObject) obj).getDatabaseID() + " " + obj);
		}
		else
			SYSLOG.trace("Database already has product categories.");
	}

	private static void createSampleProducts()
	{
		if (!DatabaseController.hasProducts())
		{
			SYSLOG.debug("Loading sample products.");

			final CSVLoader<Product> csvLoader = new CSVLoader<Product>(Product.class)
			{
				@Override
				protected Set<Product> getInvalidDataObjects(final Set<Product> validDataSet)
				{
					return ProductController.getInvalidProducts(validDataSet);
				}
			};

			csvLoader.load("data/sample_products.csv");
			csvLoader.save();

			for (Object obj : DatabaseController.getAllProducts())
				System.out.println("Product: " + ((AbstractDatabaseObject) obj).getDatabaseID() + " " + obj);
		}
		else
			SYSLOG.trace("Database already has products.");
	}

	private static void createSampleShelves()
	{
		if (!DatabaseController.hasShelves())
		{
			/* 1 - Tiny Full Shelf: 1-4 */
			/* 2 - Slot Full Shelf: 5-9 */
			/* 3 - One Slot Level: 10-13 */
			/* 4 - Many Slot Level: 14-20 */
			/* 5 - Empty Shelf 2-slot */
			SYSLOG.debug("Loading sample shelves.");

			final CSVLoader<Shelf> csvLoader = new CSVLoader<Shelf>(Shelf.class)
			{
				@Override
				protected Set<Shelf> getInvalidDataObjects(final Set<Shelf> validDataSet)
				{
					return ShelfController.getInvalidShelves(validDataSet);
				}
			};

			csvLoader.load("data/sample_shelves.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has shelves.");
	}

	private static void createSampleShelfLevels()
	{
		if (!DatabaseController.hasShelfLevels())
		{
			SYSLOG.debug("Loading sample shelf levels.");

			final CSVLoader<ShelfLevel> csvLoader = new CSVLoader<ShelfLevel>(ShelfLevel.class)
			{
				@Override
				protected Set<ShelfLevel> getInvalidDataObjects(final Set<ShelfLevel> validDataSet)
				{
					return ShelfController.getInvalidShelfLevels(validDataSet);
				}
			};

			csvLoader.load("data/sample_shelf_levels.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has shelf levels.");
	}

	private static void createSampleShelfSlots()
	{
		if (!DatabaseController.hasShelfSlots())
		{
			SYSLOG.debug("Loading sample shelf slots.");

			final CSVLoader<ShelfSlot> csvLoader = new CSVLoader<ShelfSlot>(ShelfSlot.class)
			{
				@Override
				protected Set<ShelfSlot> getInvalidDataObjects(final Set<ShelfSlot> validDataSet)
				{
					return ShelfController.getInvalidShelfSlots(validDataSet);
				}
			};

			csvLoader.load("data/sample_shelf_slots.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has shelf slots.");
	}

	private static void createSampleManifestStates()
	{
		if (!DatabaseController.hasManifestStates())
		{
			DatabaseController.save(new ManifestState(1, "Stored"));
			DatabaseController.save(new ManifestState(2, "Accepted"));
			DatabaseController.save(new ManifestState(3, "Received"));
			DatabaseController.save(new ManifestState(4, "Rejected"));
			DatabaseController.save(new ManifestState(5, "Discharged"));
		}
		else
			SYSLOG.trace("Database already has manifest states.");
	}

	private static void createSampleManifests() throws HibernateException, ParseException
	{
		if (!DatabaseController.hasManifests())
		{
			DatabaseController.save(new Manifest(1, 1, DatabaseController.getManifestStateByID(3), DatabaseController.parseDateString("2016-02-20"),
					DatabaseController.parseDateString("2016-02-29")));
			DatabaseController.save(new Manifest(2, 2, DatabaseController.getManifestStateByID(1), DatabaseController.parseDateString("2016-01-01"),
					DatabaseController.parseDateString("2016-01-01")));
			DatabaseController.save(new Manifest(3, 3, DatabaseController.getManifestStateByID(2), DatabaseController.parseDateString("2016-02-04"),
					DatabaseController.parseDateString("2016-02-10")));
			DatabaseController.save(new Manifest(4, 4, DatabaseController.getManifestStateByID(4), DatabaseController.parseDateString("2015-04-12"),
					DatabaseController.parseDateString("2015-07-18")));
		}
		else
			SYSLOG.trace("Database already has manifests.");
	}

	private static void createSampleRemovalListStates()
	{
		if (!DatabaseController.hasRemovalListStates())
		{
			DatabaseController.save(new RemovalListState(1, "Active"));
			DatabaseController.save(new RemovalListState(2, "Canceled"));
			DatabaseController.save(new RemovalListState(3, "Finished"));
		}
		else
			SYSLOG.trace("Database already has removal list states.");
	}

	private static void createSampleRemovalLists()
	{
		if (!DatabaseController.hasRemovalLists())
		{
			DatabaseController.save(new RemovalList(1, DatabaseController.getRemovalListStateByID(1)));
			DatabaseController.save(new RemovalList(2, DatabaseController.getRemovalListStateByID(2)));
			DatabaseController.save(new RemovalList(3, DatabaseController.getRemovalListStateByID(3)));
			DatabaseController.save(new RemovalList(4, DatabaseController.getRemovalListStateByID(3)));

			// Empty.
			DatabaseController.save(new RemovalList(5, DatabaseController.getRemovalListStateByID(2)));
		}
		else
			SYSLOG.trace("Database already has removal lists.");
	}

	private static void createSampleProductBoxes() throws HibernateException, ParseException
	{
		if (!DatabaseController.hasProductBoxes())
		{
			DatabaseController.save(new ProductBox(1, null, null, DatabaseController.getShelfSlotByID(1), DatabaseController.getProductByID(9), 10, 10,
					DatabaseController.parseDateString("2016-02-18")));

			DatabaseController.save(new ProductBox(2, null, null, DatabaseController.getShelfSlotByID(2), DatabaseController.getProductByID(1), 10, 10,
					DatabaseController.parseDateString("2016-10-30")));
			DatabaseController.save(new ProductBox(3, null, null, DatabaseController.getShelfSlotByID(3), DatabaseController.getProductByID(1), 10, 10));
			DatabaseController.save(new ProductBox(4, null, null, DatabaseController.getShelfSlotByID(4), DatabaseController.getProductByID(3), 10, 10));

			DatabaseController.save(new ProductBox(5, null, null, DatabaseController.getShelfSlotByID(5), DatabaseController.getProductByID(1), 50, 0));
			DatabaseController.save(new ProductBox(6, null, null, DatabaseController.getShelfSlotByID(5), DatabaseController.getProductByID(3), 50, 5,
					DatabaseController.parseDateString("2005-12-12")));
			DatabaseController.save(new ProductBox(7, null, null, DatabaseController.getShelfSlotByID(5), DatabaseController.getProductByID(6), 50, 10,
					DatabaseController.parseDateString("2008-01-10")));
			DatabaseController.save(new ProductBox(8, null, null, DatabaseController.getShelfSlotByID(5), DatabaseController.getProductByID(6), 50, 25));
			DatabaseController.save(new ProductBox(9, null, null, DatabaseController.getShelfSlotByID(44), DatabaseController.getProductByID(1), 50, 50,
					DatabaseController.parseDateString("2020-05-26")));

			DatabaseController.save(new ProductBox(10, null, null, DatabaseController.getShelfSlotByID(46), DatabaseController.getProductByID(1), 20, 20));
			DatabaseController.save(new ProductBox(11, null, null, DatabaseController.getShelfSlotByID(46), DatabaseController.getProductByID(10), 20, 20,
					DatabaseController.parseDateString("2015-03-20")));
			DatabaseController.save(new ProductBox(12, null, null, DatabaseController.getShelfSlotByID(46), DatabaseController.getProductByID(10), 30, 20));
			DatabaseController.save(new ProductBox(13, null, null, DatabaseController.getShelfSlotByID(46), DatabaseController.getProductByID(8), 30, 20));

			DatabaseController.save(new ProductBox(14, null, null, DatabaseController.getShelfSlotByID(48), DatabaseController.getProductByID(1), 10, 5,
					DatabaseController.parseDateString("2030-01-02")));
			DatabaseController.save(new ProductBox(15, null, null, DatabaseController.getShelfSlotByID(50), DatabaseController.getProductByID(9), 10, 5));
			DatabaseController.save(new ProductBox(16, null, null, DatabaseController.getShelfSlotByID(55), DatabaseController.getProductByID(3), 10, 5,
					DatabaseController.parseDateString("2012-04-18")));
			DatabaseController.save(new ProductBox(17, null, null, DatabaseController.getShelfSlotByID(67), DatabaseController.getProductByID(4), 10, 5));
			DatabaseController.save(new ProductBox(18, null, null, DatabaseController.getShelfSlotByID(90), DatabaseController.getProductByID(5), 10, 5));
			DatabaseController.save(new ProductBox(19, null, null, DatabaseController.getShelfSlotByID(91), DatabaseController.getProductByID(6), 10, 5,
					DatabaseController.parseDateString("2016-05-16")));
			DatabaseController.save(new ProductBox(20, null, null, DatabaseController.getShelfSlotByID(86), DatabaseController.getProductByID(7), 10, 5));

			/* Lonely Containers 21-23 */
			DatabaseController.save(new ProductBox(21, DatabaseController.getProductByID(11), 2, 2));
			DatabaseController.save(new ProductBox(22, DatabaseController.getProductByID(12), 2, 2));
			DatabaseController.save(new ProductBox(23, DatabaseController.getProductByID(1), 1, 0, DatabaseController.parseDateString("2003-11-30")));

			// Removal list containers.
			DatabaseController.save(new ProductBox(24, null, DatabaseController.getRemovalListByID(1), null, DatabaseController.getProductByID(10), 15, 10,
					DatabaseController.parseDateString("2017-06-17")));
			DatabaseController.save(new ProductBox(25, null, DatabaseController.getRemovalListByID(1), null, DatabaseController.getProductByID(5), 25, 0,
					DatabaseController.parseDateString("2018-06-20")));
			DatabaseController.save(new ProductBox(26, null, DatabaseController.getRemovalListByID(1), null, DatabaseController.getProductByID(3), 200, 120));
			DatabaseController.save(new ProductBox(27, null, DatabaseController.getRemovalListByID(2), null, DatabaseController.getProductByID(12), 200, 96));
			DatabaseController.save(new ProductBox(28, null, DatabaseController.getRemovalListByID(2), null, DatabaseController.getProductByID(1), 200, 2,
					DatabaseController.parseDateString("2016-08-30")));
			DatabaseController.save(new ProductBox(29, null, DatabaseController.getRemovalListByID(3), null, DatabaseController.getProductByID(1), 200, 10,
					DatabaseController.parseDateString("2016-08-11")));
			DatabaseController.save(new ProductBox(30, null, DatabaseController.getRemovalListByID(4), null, DatabaseController.getProductByID(4), 5, 0));

			// Manifest containers.
			DatabaseController.save(new ProductBox(31, DatabaseController.getManifestByID(1), null, null, DatabaseController.getProductByID(4), 1, 1,
					DatabaseController.parseDateString("2016-06-17")));
			DatabaseController.save(new ProductBox(32, DatabaseController.getManifestByID(1), null, null, DatabaseController.getProductByID(4), 20, 20,
					DatabaseController.parseDateString("2020-06-01")));
			DatabaseController.save(new ProductBox(33, DatabaseController.getManifestByID(1), null, null, DatabaseController.getProductByID(5), 200, 200,
					DatabaseController.parseDateString("2015-12-30")));
			DatabaseController.save(new ProductBox(34, DatabaseController.getManifestByID(1), null, null, DatabaseController.getProductByID(10), 340, 340,
					DatabaseController.parseDateString("2016-03-22")));
			DatabaseController.save(new ProductBox(35, DatabaseController.getManifestByID(2), null, null, DatabaseController.getProductByID(8), 60, 60,
					DatabaseController.parseDateString("2018-10-29")));
			DatabaseController.save(new ProductBox(36, DatabaseController.getManifestByID(2), null, null, DatabaseController.getProductByID(2), 87, 87,
					DatabaseController.parseDateString("2019-03-15")));
			DatabaseController.save(new ProductBox(37, DatabaseController.getManifestByID(2), null, null, DatabaseController.getProductByID(2), 99, 99,
					DatabaseController.parseDateString("2021-01-01")));
			DatabaseController.save(new ProductBox(38, DatabaseController.getManifestByID(4), null, null, DatabaseController.getProductByID(9), 110, 110));
			DatabaseController.save(new ProductBox(39, DatabaseController.getManifestByID(4), null, null, DatabaseController.getProductByID(1), 205, 205));

			// Something.
			DatabaseController.save(new ProductBox(40, DatabaseController.getProductByID(8), 55, 55));
		}
		else
			SYSLOG.trace("Database already has product boxes.");
	}

	private static void createSampleRemovalPlatforms()
	{
		if (!DatabaseController.hasRemovalPlatforms())
		{
			DatabaseController.save(new RemovalPlatform(1, 1.0, 0.1));
		}
		else
			SYSLOG.trace("Database already has removal platforms.");
	}
}
