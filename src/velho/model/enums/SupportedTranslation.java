package velho.model.enums;

import velho.controller.LocalizationController;

/**
 * @author Joona Silvennoinen
 */
public enum SupportedTranslation
{
	/**
	 * The English language.
	 */
	ENGLISH,

	/**
	 * The German language.
	 */
	GERMAN;

	@Override
	public String toString()
	{
		if (this.equals(ENGLISH))
		{
			return LocalizationController.getString("languageNameEnglish") + " (English)";
		}
		return LocalizationController.getString("languageNameGerman") + " (German)";
	}
}
