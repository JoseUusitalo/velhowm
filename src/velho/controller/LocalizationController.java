package velho.controller;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationController
{
	private static ResourceBundle msgBundle;
	private static Locale locale;

	public static void initializeBundle()
	{
		String language = "en";
		String country = "US";

		locale = new Locale(language, country);

		System.out.println(new File("res/en-US").getAbsolutePath());
		msgBundle = ResourceBundle.getBundle("res.en-US", locale);
	}

	public static void setLocale(final Locale locale)
	{
		LocalizationController.locale = locale;
	}

	public static String getString(final String key)
	{
		return msgBundle.getString(key);
	}

	public static Locale getLocale()
	{
		return locale;
	}

}
