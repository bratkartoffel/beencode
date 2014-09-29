package de.wfhosting.beencode.util;

import de.wfhosting.common.Config;

/* auto generated. DO NOT EDIT! */
public final class LanguageFields {
	static {
		Config.registerConfigClass(LanguageFields.class);
	}

	public static final String APPLICATION_LOCALE = "application.locale".toString();
	public static final String APPLICATION_LOCALE_DEFVAL = "de";

	public static final String ERROR_INTEGER_INVALID_DATA = "error.integer.invalid.data".toString();
	public static final String ERROR_INTEGER_INVALID_DATA_DEFVAL = "Ungültige Daten im Stream. Gelesen: '{0}', EOF: '{1}'.";

	public static final String ERROR_INVALID_PREFIX = "error.invalid.prefix".toString();
	public static final String ERROR_INVALID_PREFIX_DEFVAL = "Ungültiger Prefix für eine {0}. Ist '{1}', erwartet: '{2}'.";

	public static final String ERROR_LEADING_ZERO = "error.leading.zero".toString();
	public static final String ERROR_LEADING_ZERO_DEFVAL = "Führende Nullen sind nicht erlaubt.";

	public static final String ERROR_NEGATIVE_ZERO = "error.negative.zero".toString();
	public static final String ERROR_NEGATIVE_ZERO_DEFVAL = "Ungültige Daten in Stream: -0 ist nicht erlaubt.";

	public static final String ERROR_STRING_LENGTH = "error.string.length".toString();
	public static final String ERROR_STRING_LENGTH_DEFVAL = "Ungültige Größe für String: {0} > {1}.";

	public static final String ERROR_UNEXPECTED_END = "error.unexpected.end".toString();
	public static final String ERROR_UNEXPECTED_END_DEFVAL = "Unerwartetes Ende der Daten.";

	public static final String ERROR_UNKNOWN = "error.unknown".toString();
	public static final String ERROR_UNKNOWN_DEFVAL = "Anwendungsfehler.";


	private LanguageFields() {
		// hide constructor
	}
}
