package velho.model.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import velho.controller.DatabaseController;
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
import velho.model.enums.UserRole;

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

	private static void createSampleUsers()
	{
		if (!DatabaseController.hasUsers())
		{
			DatabaseController.save(new User(1, "Admin", "Test", "111111", null, UserRole.ADMINISTRATOR));
			DatabaseController.save(new User(2, "Boss", "Test", "222222", null, UserRole.MANAGER));
			DatabaseController.save(new User(3, "Worker", "Test", "333333", null, UserRole.LOGISTICIAN));
			DatabaseController.save(new User(4, "Badger", "Testaccount", null, "12345678", UserRole.LOGISTICIAN));
		}
		else
			SYSLOG.trace("Database already has users.");
	}

	private static void createSampleBrands()
	{
		if (!DatabaseController.hasProductBrands())
		{
			DatabaseController.save(new ProductBrand(1, "Test Brand #1"));
			DatabaseController.save(new ProductBrand(2, "Test Brand #2"));
			DatabaseController.save(new ProductBrand(3, "Test Brand #3"));
			DatabaseController.save(new ProductBrand(4, "Empty Brand"));
		}
		else
			SYSLOG.trace("Database already has brands.");
	}

	private static void createSampleProductTypes()
	{
		if (!DatabaseController.hasProductTypes())
		{
			DatabaseController.save(new ProductType(1, "Regular"));
			DatabaseController.save(new ProductType(2, "Raw"));
			DatabaseController.save(new ProductType(3, "Frozen"));
		}
		else
			SYSLOG.trace("Database already has types.");
	}

	private static void createSampleProductCategories()
	{
		if (!DatabaseController.hasProductCategories())
		{
			DatabaseController.save(new ProductCategory(1, "Regular Stuff", DatabaseController.getProductTypeByID(1)));
			DatabaseController.save(new ProductCategory(2, "Raw Food", DatabaseController.getProductTypeByID(2)));
			DatabaseController.save(new ProductCategory(3, "Frozen Things", DatabaseController.getProductTypeByID(3)));
			DatabaseController.save(new ProductCategory(4, "Weird Stuff", DatabaseController.getProductTypeByID(1)));
		}
		else
			SYSLOG.trace("Database already has categories.");
	}

	private static void createSampleProducts()
	{
		if (!DatabaseController.hasProducts())
		{
			DatabaseController.save(new Product(1, "Test Product #1", DatabaseController.getProductBrandByID(1), DatabaseController.getProductCategoryByID(1)));
			DatabaseController
					.save(new Product(2, "Lonely Product #2", DatabaseController.getProductBrandByID(1), DatabaseController.getProductCategoryByID(1)));
			DatabaseController.save(new Product(3, "Test Product #3", DatabaseController.getProductBrandByID(1), DatabaseController.getProductCategoryByID(2)));
			DatabaseController.save(new Product(4, "Test Product #4", DatabaseController.getProductBrandByID(1), DatabaseController.getProductCategoryByID(2)));
			DatabaseController.save(new Product(5, "Test Product #5", DatabaseController.getProductBrandByID(1), DatabaseController.getProductCategoryByID(2)));
			DatabaseController.save(new Product(6, "Test Product #6", DatabaseController.getProductBrandByID(2), DatabaseController.getProductCategoryByID(1)));
			DatabaseController.save(new Product(7, "Test Product #7", DatabaseController.getProductBrandByID(2), DatabaseController.getProductCategoryByID(1)));
			DatabaseController.save(new Product(8, "Test Product #8", DatabaseController.getProductBrandByID(2), DatabaseController.getProductCategoryByID(1)));
			DatabaseController.save(new Product(9, "Test Product #9", DatabaseController.getProductBrandByID(3), DatabaseController.getProductCategoryByID(1)));
			DatabaseController
					.save(new Product(10, "Test Product #10", DatabaseController.getProductBrandByID(3), DatabaseController.getProductCategoryByID(1)));
			DatabaseController
					.save(new Product(11, "Lonely Product #11", DatabaseController.getProductBrandByID(2), DatabaseController.getProductCategoryByID(4)));
			DatabaseController
					.save(new Product(12, "Lonely Product #12", DatabaseController.getProductBrandByID(2), DatabaseController.getProductCategoryByID(4)));
		}
		else
			SYSLOG.trace("Database already has products.");
	}

	private static void createSampleShelves()
	{
		if (!DatabaseController.hasShelves())
		{
			/* 1 - Tiny Full Shelf: 1-4 */
			DatabaseController.save(new Shelf(1, 1));
			/* 2 - Slot Full Shelf: 5-9 */
			DatabaseController.save(new Shelf(2, 2));
			/* 3 - One Slot Level: 10-13 */
			DatabaseController.save(new Shelf(3, 3));
			/* 4 - Many Slot Level: 14-20 */
			DatabaseController.save(new Shelf(4, 2));
			/* 5 - Empty Shelf 2-slot */
			DatabaseController.save(new Shelf(5, 1));
		}
		else
			SYSLOG.trace("Database already has shelves.");
	}

	private static void createSampleShelfLevels()
	{
		if (!DatabaseController.hasShelfLevels())
		{
			DatabaseController.save(new ShelfLevel(1, DatabaseController.getShelfByID(1), 1, 4));

			DatabaseController.save(new ShelfLevel(2, DatabaseController.getShelfByID(2), 1, 20));
			DatabaseController.save(new ShelfLevel(3, DatabaseController.getShelfByID(2), 2, 20));

			DatabaseController.save(new ShelfLevel(4, DatabaseController.getShelfByID(3), 1, 1));
			DatabaseController.save(new ShelfLevel(5, DatabaseController.getShelfByID(3), 2, 1));
			DatabaseController.save(new ShelfLevel(6, DatabaseController.getShelfByID(3), 3, 1));

			DatabaseController.save(new ShelfLevel(7, DatabaseController.getShelfByID(4), 1, 50));
			DatabaseController.save(new ShelfLevel(8, DatabaseController.getShelfByID(4), 2, 50));

			DatabaseController.save(new ShelfLevel(9, DatabaseController.getShelfByID(5), 1, 2));
			DatabaseController.save(new ShelfLevel(10, DatabaseController.getShelfByID(5), 2, 2));
		}
		else
			SYSLOG.trace("Database already has shelf levels.");
	}

	private static void createSampleShelfSlots()
	{
		if (!DatabaseController.hasShelfSlots())
		{
			final List<Object> objs = DatabaseController.getAllShelfLevels();
			final List<ShelfLevel> levels = new ArrayList<ShelfLevel>();

			// Padding.
			levels.add(null);

			for (Object o : objs)
				levels.add((ShelfLevel) o);

			DatabaseController.save(new ShelfSlot(1, levels.get(1), 1, 1));
			DatabaseController.save(new ShelfSlot(2, levels.get(1), 2, 1));
			DatabaseController.save(new ShelfSlot(3, levels.get(1), 3, 1));
			DatabaseController.save(new ShelfSlot(4, levels.get(1), 4, 1));

			DatabaseController.save(new ShelfSlot(5, levels.get(2), 1, 4));
			DatabaseController.save(new ShelfSlot(6, levels.get(2), 2, 4));
			DatabaseController.save(new ShelfSlot(7, levels.get(2), 3, 4));
			DatabaseController.save(new ShelfSlot(8, levels.get(2), 4, 4));
			DatabaseController.save(new ShelfSlot(9, levels.get(2), 5, 4));
			DatabaseController.save(new ShelfSlot(10, levels.get(2), 6, 4));
			DatabaseController.save(new ShelfSlot(11, levels.get(2), 7, 4));
			DatabaseController.save(new ShelfSlot(12, levels.get(2), 8, 4));
			DatabaseController.save(new ShelfSlot(13, levels.get(2), 9, 4));
			DatabaseController.save(new ShelfSlot(14, levels.get(2), 10, 4));
			DatabaseController.save(new ShelfSlot(15, levels.get(2), 11, 4));
			DatabaseController.save(new ShelfSlot(16, levels.get(2), 12, 4));
			DatabaseController.save(new ShelfSlot(17, levels.get(2), 13, 4));
			DatabaseController.save(new ShelfSlot(18, levels.get(2), 14, 4));
			DatabaseController.save(new ShelfSlot(19, levels.get(2), 15, 4));
			DatabaseController.save(new ShelfSlot(20, levels.get(2), 16, 4));
			DatabaseController.save(new ShelfSlot(21, levels.get(2), 17, 4));
			DatabaseController.save(new ShelfSlot(22, levels.get(2), 18, 4));
			DatabaseController.save(new ShelfSlot(23, levels.get(2), 19, 4));
			DatabaseController.save(new ShelfSlot(24, levels.get(2), 20, 4));

			DatabaseController.save(new ShelfSlot(25, levels.get(3), 1, 4));
			DatabaseController.save(new ShelfSlot(26, levels.get(3), 2, 4));
			DatabaseController.save(new ShelfSlot(27, levels.get(3), 3, 4));
			DatabaseController.save(new ShelfSlot(28, levels.get(3), 4, 4));
			DatabaseController.save(new ShelfSlot(29, levels.get(3), 5, 4));
			DatabaseController.save(new ShelfSlot(30, levels.get(3), 6, 4));
			DatabaseController.save(new ShelfSlot(31, levels.get(3), 7, 4));
			DatabaseController.save(new ShelfSlot(32, levels.get(3), 8, 4));
			DatabaseController.save(new ShelfSlot(33, levels.get(3), 9, 4));
			DatabaseController.save(new ShelfSlot(34, levels.get(3), 10, 4));
			DatabaseController.save(new ShelfSlot(35, levels.get(3), 11, 4));
			DatabaseController.save(new ShelfSlot(36, levels.get(3), 12, 4));
			DatabaseController.save(new ShelfSlot(37, levels.get(3), 13, 4));
			DatabaseController.save(new ShelfSlot(38, levels.get(3), 14, 4));
			DatabaseController.save(new ShelfSlot(39, levels.get(3), 15, 4));
			DatabaseController.save(new ShelfSlot(40, levels.get(3), 16, 4));
			DatabaseController.save(new ShelfSlot(41, levels.get(3), 17, 4));
			DatabaseController.save(new ShelfSlot(42, levels.get(3), 18, 4));
			DatabaseController.save(new ShelfSlot(43, levels.get(3), 19, 4));
			DatabaseController.save(new ShelfSlot(44, levels.get(3), 20, 4));

			DatabaseController.save(new ShelfSlot(45, levels.get(4), 1, 50));
			DatabaseController.save(new ShelfSlot(46, levels.get(5), 1, 50));
			DatabaseController.save(new ShelfSlot(47, levels.get(6), 1, 50));

			DatabaseController.save(new ShelfSlot(48, levels.get(7), 1, 1));
			DatabaseController.save(new ShelfSlot(49, levels.get(7), 2, 1));
			DatabaseController.save(new ShelfSlot(50, levels.get(7), 3, 1));
			DatabaseController.save(new ShelfSlot(51, levels.get(7), 4, 1));
			DatabaseController.save(new ShelfSlot(52, levels.get(7), 5, 1));
			DatabaseController.save(new ShelfSlot(53, levels.get(7), 6, 1));
			DatabaseController.save(new ShelfSlot(54, levels.get(7), 7, 1));
			DatabaseController.save(new ShelfSlot(55, levels.get(7), 8, 1));
			DatabaseController.save(new ShelfSlot(56, levels.get(7), 9, 1));
			DatabaseController.save(new ShelfSlot(57, levels.get(7), 10, 1));
			DatabaseController.save(new ShelfSlot(58, levels.get(7), 11, 1));
			DatabaseController.save(new ShelfSlot(59, levels.get(7), 12, 1));
			DatabaseController.save(new ShelfSlot(60, levels.get(7), 13, 1));
			DatabaseController.save(new ShelfSlot(61, levels.get(7), 14, 1));

			DatabaseController.save(new ShelfSlot(62, levels.get(7), 15, 1));
			DatabaseController.save(new ShelfSlot(63, levels.get(7), 16, 1));
			DatabaseController.save(new ShelfSlot(64, levels.get(7), 17, 1));
			DatabaseController.save(new ShelfSlot(65, levels.get(7), 18, 1));
			DatabaseController.save(new ShelfSlot(66, levels.get(7), 19, 1));
			DatabaseController.save(new ShelfSlot(67, levels.get(7), 20, 1));
			DatabaseController.save(new ShelfSlot(68, levels.get(7), 21, 1));
			DatabaseController.save(new ShelfSlot(69, levels.get(7), 22, 1));
			DatabaseController.save(new ShelfSlot(70, levels.get(7), 23, 1));
			DatabaseController.save(new ShelfSlot(71, levels.get(7), 24, 1));
			DatabaseController.save(new ShelfSlot(72, levels.get(7), 25, 1));
			DatabaseController.save(new ShelfSlot(73, levels.get(7), 26, 1));
			DatabaseController.save(new ShelfSlot(74, levels.get(7), 27, 1));
			DatabaseController.save(new ShelfSlot(75, levels.get(7), 28, 1));
			DatabaseController.save(new ShelfSlot(76, levels.get(7), 29, 1));
			DatabaseController.save(new ShelfSlot(77, levels.get(7), 30, 1));
			DatabaseController.save(new ShelfSlot(78, levels.get(7), 31, 1));

			DatabaseController.save(new ShelfSlot(79, levels.get(7), 32, 1));
			DatabaseController.save(new ShelfSlot(80, levels.get(7), 33, 1));
			DatabaseController.save(new ShelfSlot(81, levels.get(7), 34, 1));
			DatabaseController.save(new ShelfSlot(82, levels.get(7), 35, 1));
			DatabaseController.save(new ShelfSlot(83, levels.get(7), 36, 1));
			DatabaseController.save(new ShelfSlot(84, levels.get(7), 37, 1));
			DatabaseController.save(new ShelfSlot(85, levels.get(7), 38, 1));
			DatabaseController.save(new ShelfSlot(86, levels.get(7), 39, 1));
			DatabaseController.save(new ShelfSlot(87, levels.get(7), 40, 1));
			DatabaseController.save(new ShelfSlot(88, levels.get(7), 41, 1));
			DatabaseController.save(new ShelfSlot(89, levels.get(7), 42, 1));
			DatabaseController.save(new ShelfSlot(90, levels.get(7), 43, 1));
			DatabaseController.save(new ShelfSlot(91, levels.get(7), 44, 1));
			DatabaseController.save(new ShelfSlot(92, levels.get(7), 45, 1));
			DatabaseController.save(new ShelfSlot(93, levels.get(7), 46, 1));
			DatabaseController.save(new ShelfSlot(94, levels.get(7), 47, 1));
			DatabaseController.save(new ShelfSlot(95, levels.get(7), 48, 1));
			DatabaseController.save(new ShelfSlot(96, levels.get(7), 49, 1));
			DatabaseController.save(new ShelfSlot(97, levels.get(7), 50, 1));

			DatabaseController.save(new ShelfSlot(98, levels.get(8), 1, 1));
			DatabaseController.save(new ShelfSlot(99, levels.get(8), 2, 1));
			DatabaseController.save(new ShelfSlot(100, levels.get(8), 3, 1));
			DatabaseController.save(new ShelfSlot(101, levels.get(8), 4, 1));
			DatabaseController.save(new ShelfSlot(102, levels.get(8), 5, 1));
			DatabaseController.save(new ShelfSlot(103, levels.get(8), 6, 1));
			DatabaseController.save(new ShelfSlot(104, levels.get(8), 7, 1));
			DatabaseController.save(new ShelfSlot(105, levels.get(8), 8, 1));
			DatabaseController.save(new ShelfSlot(106, levels.get(8), 9, 1));
			DatabaseController.save(new ShelfSlot(107, levels.get(8), 10, 1));
			DatabaseController.save(new ShelfSlot(108, levels.get(8), 11, 1));
			DatabaseController.save(new ShelfSlot(109, levels.get(8), 12, 1));

			DatabaseController.save(new ShelfSlot(110, levels.get(8), 13, 1));
			DatabaseController.save(new ShelfSlot(111, levels.get(8), 14, 1));
			DatabaseController.save(new ShelfSlot(112, levels.get(8), 15, 1));
			DatabaseController.save(new ShelfSlot(113, levels.get(8), 16, 1));
			DatabaseController.save(new ShelfSlot(114, levels.get(8), 17, 1));
			DatabaseController.save(new ShelfSlot(115, levels.get(8), 18, 1));
			DatabaseController.save(new ShelfSlot(116, levels.get(8), 19, 1));
			DatabaseController.save(new ShelfSlot(117, levels.get(8), 20, 1));
			DatabaseController.save(new ShelfSlot(118, levels.get(8), 21, 1));
			DatabaseController.save(new ShelfSlot(119, levels.get(8), 22, 1));

			DatabaseController.save(new ShelfSlot(120, levels.get(8), 23, 1));
			DatabaseController.save(new ShelfSlot(121, levels.get(8), 24, 1));
			DatabaseController.save(new ShelfSlot(122, levels.get(8), 25, 1));
			DatabaseController.save(new ShelfSlot(123, levels.get(8), 26, 1));
			DatabaseController.save(new ShelfSlot(124, levels.get(8), 27, 1));
			DatabaseController.save(new ShelfSlot(125, levels.get(8), 28, 1));
			DatabaseController.save(new ShelfSlot(126, levels.get(8), 29, 1));
			DatabaseController.save(new ShelfSlot(127, levels.get(8), 30, 1));
			DatabaseController.save(new ShelfSlot(128, levels.get(8), 31, 1));
			DatabaseController.save(new ShelfSlot(129, levels.get(8), 32, 1));
			DatabaseController.save(new ShelfSlot(130, levels.get(8), 33, 1));
			DatabaseController.save(new ShelfSlot(131, levels.get(8), 34, 1));

			DatabaseController.save(new ShelfSlot(132, levels.get(8), 35, 1));
			DatabaseController.save(new ShelfSlot(133, levels.get(8), 36, 1));
			DatabaseController.save(new ShelfSlot(134, levels.get(8), 37, 1));
			DatabaseController.save(new ShelfSlot(135, levels.get(8), 38, 1));
			DatabaseController.save(new ShelfSlot(136, levels.get(8), 39, 1));
			DatabaseController.save(new ShelfSlot(137, levels.get(8), 40, 1));
			DatabaseController.save(new ShelfSlot(138, levels.get(8), 41, 1));
			DatabaseController.save(new ShelfSlot(139, levels.get(8), 42, 1));
			DatabaseController.save(new ShelfSlot(140, levels.get(8), 43, 1));
			DatabaseController.save(new ShelfSlot(141, levels.get(8), 44, 1));
			DatabaseController.save(new ShelfSlot(142, levels.get(8), 45, 1));
			DatabaseController.save(new ShelfSlot(143, levels.get(8), 46, 1));
			DatabaseController.save(new ShelfSlot(144, levels.get(8), 47, 1));
			DatabaseController.save(new ShelfSlot(145, levels.get(8), 48, 1));
			DatabaseController.save(new ShelfSlot(146, levels.get(8), 49, 1));
			DatabaseController.save(new ShelfSlot(147, levels.get(8), 50, 1));

			DatabaseController.save(new ShelfSlot(148, levels.get(9), 1, 1));
			DatabaseController.save(new ShelfSlot(149, levels.get(9), 2, 1));
			DatabaseController.save(new ShelfSlot(150, levels.get(10), 1, 1));
			DatabaseController.save(new ShelfSlot(151, levels.get(10), 2, 1));
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
