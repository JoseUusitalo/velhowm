package velho.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

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

public class CSVController
{
	@SuppressWarnings("resource")
	public static List<String[]> readCSVFile(final String filePath) throws FileNotFoundException
	{
		CSVReader reader = new CSVReader(new FileReader(filePath));

		List<String[]> csvLines = null;

		try
		{
			csvLines = reader.readAll();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return csvLines;

	}

	private static Object[] parseUserDataSet(final List<String[]> csvLines)
	{
		final Set<User> dataset = new HashSet<User>();
		final List<List<String>> invalidData = new ArrayList<List<String>>();

		if (csvLines != null)
		{
			boolean isValid = false;
			int databaseID = 0;
			String fname = null, lname = null, pin = null, badge = null;
			UserRole role = null;

			for (String[] line : csvLines)
			{
				try
				{
					databaseID = Integer.valueOf(line[0]);
					fname = line[1];
					lname = line[2];
					pin = line[3];
					badge = line[4];
					role = DatabaseController.getUserRoleByName(line[5]);

					isValid = UserController.validateUserData(badge, pin, fname, lname, role);
				}
				catch (Exception e)
				{
					isValid = false;
				}

				if (isValid)
				{
					dataset.add(new User(databaseID, fname, lname, pin, badge, role));
				}
				else
				{
					invalidData.add(Arrays.asList(line));
				}
			}
		}

		return new Object[] { dataset, invalidData };
	}

	public static Object[] readSampleUsersCSV() throws FileNotFoundException
	{
		return parseUserDataSet(readCSVFile("data/users.csv"));
	}

	private static boolean parseBrandsDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new ProductBrand(Integer.valueOf(line[0]), line[1]));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleBrandsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/brands.csv"));

	}

	private static boolean parseTypesDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new ProductType(Integer.valueOf(line[0]), line[1]));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleTypesCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/types.csv"));

	}

	private static boolean parseCategoriesDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new ProductCategory(Integer.valueOf(line[0]), line[1], DatabaseController.getProductTypeByName(line[2])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleCategoriesCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Categories.csv"));

	}

	private static boolean parseProductsDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new Product(Integer.valueOf(line[0]), (line[1]), DatabaseController.getProductBrandByName(line[2]),
						DatabaseController.getProductCategoryByName(line[3])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleShelvesCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseShelvesDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new Shelf(Integer.valueOf(line[0]), Integer.valueOf(line[1])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleProductsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseShelfLevelsDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new ShelfLevel(Integer.valueOf(line[0]), Integer.valueOf(line[1]), Integer.valueOf(line[2])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleShelfLevelsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseShelfSlotsDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new ShelfSlot(Integer.valueOf(line[0]), Integer.valueOf(line[1]), Integer.valueOf(line[2])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleShelfSlotsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseManifestStatesDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new ManifestState(Integer.valueOf(line[0]), line[1]));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleManifestStatesCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseManifestsDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new Manifest(Integer.valueOf(line[0]), Integer.valueOf(line[1]), Integer.valueOf(line[2]), line[3], line[4]));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleManifestsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseRemovalListStatesDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new RemovalListState(Integer.valueOf(line[0]), line[1]));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleRemovalListStatesCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseRemovalListsDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new RemovalList(Integer.valueOf(line[0]), Integer.valueOf(line[1])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleRemovalListsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseProductBoxesDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new ProductBox(Integer.valueOf(line[0]), Integer.valueOf(line[1]), Integer.valueOf(line[2]),
						Integer.valueOf(line[3]), Integer.valueOf(line[4]), Integer.valueOf(line[5]), Integer.valueOf(line[6]), line[7]));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleProductBoxesCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	private static boolean parseRemovalPlatformsDataSet(final List<String[]> csvLines)
	{
		if (csvLines != null)
		{
			for (String[] line : csvLines)
			{
				DatabaseController.saveOrUpdate(new RemovalPlatform(Integer.valueOf(line[0]), Double.parseDouble(line[1]), Double.parseDouble(line[2])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleRemovalPlatformsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}

	public static void readBeansCSV()
	{
		CSVReader reader = null;

		try
		{
			reader = new CSVReader(new FileReader("data/users.csv"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		final List<List<String>> invalidData = new ArrayList<List<String>>();

		ColumnPositionMappingStrategy<User> strat = new ColumnPositionMappingStrategy<User>();
		strat.setType(User.class);
		String[] columns = new String[] { "databaseID", "firstName", "lastName", "pin", "badgeID", "role" };
		strat.setColumnMapping(columns);

		CsvToBean<User> csv = new CsvToBean<User>();
		List<User> list = new ArrayList<User>();

		list = csv.parse(strat, reader);

		System.out.println(list);
	}

	public static void writeCSV()
	{
		CSVWriter writer = null;
		try
		{
			writer = new CSVWriter(new FileWriter("productbrands.csv"), '@');

			// feed in your array (or convert your data to an array)
			String[] entries = new String[] { "asd,u348g" };

			writer.writeNext(entries);

			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
