package velho.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;

import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.model.User;

public class CSVController
{
	@SuppressWarnings("resource")
	public static List<String[]> readCSVFile(final String filePath) throws FileNotFoundException
	{
		CSVReader reader = new CSVReader(new FileReader(filePath));

		List<String[]> myEntreis = null;

		try
		{
			myEntreis = reader.readAll();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return myEntreis;

	}

	private static boolean parseUserDataSet(final List<String[]> myEntreis)
	{
		// "Badge ID", "PIN", "First Name", "Family Name", "Role Name");
		if (myEntreis != null)
		{
			for (String[] line : myEntreis) // Badge ID, Pin, First name, Last name, Role name
			{
				DatabaseController.saveOrUpdate(new User(line[2], line[3], line[1], line[0], DatabaseController.getRoleByName(line[4])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleUsersCSV() throws FileNotFoundException
	{

		return parseUserDataSet(readCSVFile("data/users.csv"));

	}

	private static boolean parseBrandsDataSet(final List<String[]> myEntreis)
	{
		if (myEntreis != null)
		{
			for (String[] line : myEntreis)
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

	private static boolean parseTypesDataSet(final List<String[]> myEntreis)
	{
		if (myEntreis != null)
		{
			for (String[] line : myEntreis)
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

	private static boolean parseCategoriesDataSet(final List<String[]> myEntreis)
	{
		if (myEntreis != null)
		{
			for (String[] line : myEntreis)
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

	private static boolean parseProductsDataSet(final List<String[]> myEntreis)
	{
		if (myEntreis != null)
		{
			for (String[] line : myEntreis)
			{
				DatabaseController.saveOrUpdate(new Product(Integer.valueOf(line[0]), (line[1]), DatabaseController.getProductBrandByName(line[2]), DatabaseController.getProductCategoryByName(line[3])));
			}
			return true;
		}

		return false;
	}

	public boolean readSampleProductsCSV() throws FileNotFoundException
	{

		return parseBrandsDataSet(readCSVFile("data/Products.csv"));

	}
}
