package velho.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import velho.model.User;

public class CSVController
{
	@SuppressWarnings("resource")
	public static boolean readIntoDatabase(final String filePath) throws FileNotFoundException
	{
		ColumnPositionMappingStrategy<User> strat = new ColumnPositionMappingStrategy<User>();
		strat.setType(User.class);

		String[] columns = new String[] { "badgeID", "pin", "firstName", "lastName", "roleName" };
		strat.setColumnMapping(columns);

		CSVReader reader = new CSVReader(new FileReader(filePath));
		CsvToBean<User> csv = new CsvToBean<User>();
		List<User> list = csv.parse(strat, reader);
		System.out.println(list);

		return true;
	}
}
