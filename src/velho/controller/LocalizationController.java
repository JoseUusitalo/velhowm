package velho.controller;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import velho.model.enums.SupportedTranslation;

/**
 * The singleton controller for handling localization.
 *
 * @author Joona Silvennoinen
 */
public class LocalizationController
{
	private ResourceBundle msgBundle;
	private Locale localeEnglish;
	private Locale localeGerman;

	private final static String LANGUAGE_ENGLISH = "en";
	private final static String COUNTRY_USA = "US";

	private final static String LANGUAGE_GERMAN = "de";
	private final static String COUNTRY_GERMANY = "DE";
	private SupportedTranslation currentTranslation;

	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link LocalizationController}.
		 */
		private static final LocalizationController INSTANCE = new LocalizationController();
	}

	/**
	 */
	private LocalizationController()
	{
		// No need to instantiate this class.
	}

	/**
	 * Gets the instance of the {@link LocalizationController}.
	 *
	 * @return the localization controller
	 */
	public static synchronized LocalizationController getInstance()
	{
		return Holder.INSTANCE;
	}

	public void initializeBundle()
	{
		currentTranslation = SupportedTranslation.ENGLISH;
		localeEnglish = new Locale(LANGUAGE_ENGLISH, COUNTRY_USA);
		localeGerman = new Locale(LANGUAGE_GERMAN, COUNTRY_GERMANY);

		msgBundle = getResourceBundle(localeEnglish);
	}

	private ResourceBundle getResourceBundle(final Locale locale)
	{
		if (locale.equals(localeEnglish))
		{
			return ResourceBundle.getBundle("translations.en-US", locale);
		}

		return ResourceBundle.getBundle("translations.de-DE", locale);
	}

	public void setLocale(final Locale locale)
	{
		localeEnglish = locale;
	}

	public String getCompoundString(final String key, final Object... messageArguments)
	{
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(localeEnglish);
		formatter.applyPattern(msgBundle.getString(key));
		return formatter.format(messageArguments);
	}

	public String getString(final String key)
	{
		return msgBundle.getString(key);
	}

	public Locale getLocale()
	{
		return localeEnglish;
	}

	public void changeTranslation(final SupportedTranslation newTranslation)
	{
		if (newTranslation.equals(SupportedTranslation.ENGLISH))
		{
			msgBundle = getResourceBundle(localeEnglish);
		}
		else
		{
			msgBundle = getResourceBundle(localeGerman);

		}
		currentTranslation = newTranslation;
		UIController.getInstance().recreateAllViews();
	}

	public SupportedTranslation getCurrentTranslation()
	{
		return currentTranslation;
	}
}
