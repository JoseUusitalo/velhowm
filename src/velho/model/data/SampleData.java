package velho.model.data;

import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import velho.controller.ManifestController;
import velho.controller.ProductController;
import velho.controller.RemovalListController;
import velho.controller.RemovalPlatformController;
import velho.controller.ShelfController;
import velho.controller.UserController;
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

			csvLoader.load("res/sample_users.csv");
			csvLoader.save();
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

			csvLoader.load("res/sample_product_brands.csv");
			csvLoader.save();
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

			csvLoader.load("res/sample_product_types.csv");
			csvLoader.save();
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

			csvLoader.load("res/sample_product_categories.csv");
			csvLoader.save();
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

			csvLoader.load("res/sample_products.csv");
			csvLoader.save();
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

			csvLoader.load("res/sample_shelves.csv");
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

			csvLoader.load("res/sample_shelf_levels.csv");
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

			csvLoader.load("res/sample_shelf_slots.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has shelf slots.");
	}

	private static void createSampleManifestStates()
	{
		if (!DatabaseController.hasManifestStates())
		{
			SYSLOG.debug("Loading sample manifest states.");

			final CSVLoader<ManifestState> csvLoader = new CSVLoader<ManifestState>(ManifestState.class)
			{
				@Override
				protected Set<ManifestState> getInvalidDataObjects(final Set<ManifestState> validDataSet)
				{
					return ManifestController.getInvalidManifestStates(validDataSet);
				}
			};

			csvLoader.load("res/sample_manifest_states.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has manifest states.");
	}

	private static void createSampleManifests()
	{
		if (!DatabaseController.hasManifests())
		{
			SYSLOG.debug("Loading sample manifests.");

			final CSVLoader<Manifest> csvLoader = new CSVLoader<Manifest>(Manifest.class)
			{
				@Override
				protected Set<Manifest> getInvalidDataObjects(final Set<Manifest> validDataSet)
				{
					return ManifestController.getInvalidManifests(validDataSet);
				}
			};

			csvLoader.load("res/sample_manifests.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has manifests.");
	}

	private static void createSampleRemovalListStates()
	{
		if (!DatabaseController.hasRemovalListStates())
		{
			SYSLOG.debug("Loading sample removal list states.");

			final CSVLoader<RemovalListState> csvLoader = new CSVLoader<RemovalListState>(RemovalListState.class)
			{
				@Override
				protected Set<RemovalListState> getInvalidDataObjects(final Set<RemovalListState> validDataSet)
				{
					return RemovalListController.getInvalidRemovalListStates(validDataSet);
				}
			};

			csvLoader.load("res/sample_removal_list_states.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has removal list states.");
	}

	private static void createSampleRemovalLists()
	{
		if (!DatabaseController.hasRemovalLists())
		{
			SYSLOG.debug("Loading sample removal lists.");

			final CSVLoader<RemovalList> csvLoader = new CSVLoader<RemovalList>(RemovalList.class)
			{
				@Override
				protected Set<RemovalList> getInvalidDataObjects(final Set<RemovalList> validDataSet)
				{
					return RemovalListController.getInvalidRemovalLists(validDataSet);
				}
			};

			csvLoader.load("res/sample_removal_lists.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has removal lists.");
	}

	private static void createSampleProductBoxes()
	{
		if (!DatabaseController.hasProductBoxes())
		{
			SYSLOG.debug("Loading sample product boxes.");

			final CSVLoader<ProductBox> csvLoader = new CSVLoader<ProductBox>(ProductBox.class)
			{
				@Override
				protected Set<ProductBox> getInvalidDataObjects(final Set<ProductBox> validDataSet)
				{
					return ProductController.getInvalidProductBoxes(validDataSet);
				}
			};

			csvLoader.load("res/sample_product_boxes.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has product boxes.");
	}

	private static void createSampleRemovalPlatforms()
	{
		if (!DatabaseController.hasRemovalPlatforms())
		{
			SYSLOG.debug("Loading sample removal platforms.");

			final CSVLoader<RemovalPlatform> csvLoader = new CSVLoader<RemovalPlatform>(RemovalPlatform.class)
			{
				@Override
				protected Set<RemovalPlatform> getInvalidDataObjects(final Set<RemovalPlatform> validDataSet)
				{
					return RemovalPlatformController.getInvalidRemovalPlatforms(validDataSet);
				}
			};

			csvLoader.load("res/sample_removal_platforms.csv");
			csvLoader.save();
		}
		else
			SYSLOG.trace("Database already has removal platforms.");
	}
}
