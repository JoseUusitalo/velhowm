package velho.controller;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import velho.model.enums.SupportedTranslation;

public class LocalizationController
{
	private static ResourceBundle msgBundle;
	private static Locale localeEnglish;
	private static Locale localeGerman;

	private final static String languageEnglish = "en";
	private final static String countryEnglish = "US";

	private final static String languageGerman = "de";
	private final static String countryGerman = "DE";
	private static UIController uiController;

	public static void initializeBundle()
	{

		localeEnglish = new Locale(languageEnglish, countryEnglish);
		localeGerman = new Locale(languageGerman, countryGerman);

		msgBundle = getResourceBundle(localeEnglish);
	}

	private static ResourceBundle getResourceBundle(final Locale locale)
	{
		if (locale.equals(localeEnglish))
		{
			return ResourceBundle.getBundle("res.en-US", locale);
		}
		return ResourceBundle.getBundle("res.de-DE", locale);
	}

	public static void setLocale(final Locale locale)
	{
		LocalizationController.localeEnglish = locale;
	}

	public static String getCompoundString(final String key, final Object[] messageArguments)
	{
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(localeEnglish);
		formatter.applyPattern(msgBundle.getString(key));
		return formatter.format(messageArguments);
	}

	public static String getString(final String key)
	{
		return msgBundle.getString(key);
	}

	public static Locale getLocale()
	{
		return localeEnglish;
	}

	public static void changeTranslation(final SupportedTranslation newTranslation)
	{
		if (newTranslation.equals(SupportedTranslation.ENGLISH))
		{
			msgBundle = getResourceBundle(localeEnglish);
			uiController.recreateAllViews();
		}
		else
		{
			msgBundle = getResourceBundle(localeGerman);
			uiController.recreateAllViews();
		}
	}

	public static void setControllers(final UIController uiController)
	{
		LocalizationController.uiController = uiController;
	}

}
