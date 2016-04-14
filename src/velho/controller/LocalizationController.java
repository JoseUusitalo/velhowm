package velho.controller;

import java.text.MessageFormat;
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

		msgBundle = ResourceBundle.getBundle("res.en-US", locale);

	}

	public static void setLocale(final Locale locale)
	{
		LocalizationController.locale = locale;
	}

	public static String getCompoundString(final String key, final Object[] messageArguments)
	{
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);
		formatter.applyPattern(msgBundle.getString(key));
		return formatter.format(messageArguments);
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
