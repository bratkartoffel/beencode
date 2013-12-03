package de.wfhosting.beencode.util;

import java.io.IOException;
import java.nio.charset.Charset;

import de.wfhosting.common.R;

/**
 * Abstract helper class for various need.<br>
 * 
 * @since 0.1
 */
public final class Tools {
	public static final Charset UTF8 = Charset.forName("UTF-8");

	public static void checkLeadingZero(final boolean started, final int digit)
			throws IOException {
		if (!started && digit == '0') {
			throw new IOException(R.t(LanguageFields.ERROR_LEADING_ZERO));
		}
	}

	public static boolean isDigit(final int code) {
		return code >= '0' && code <= '9';
	}

	private Tools() {
		// hide constructor
	}
}
