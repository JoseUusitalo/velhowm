package velho.model.enums;

import velho.controller.LocalizationController;

public enum SupportedTranslation
{
	ENGLISH, GERMAN;

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
