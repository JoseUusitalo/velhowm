package velho.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;

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

}
