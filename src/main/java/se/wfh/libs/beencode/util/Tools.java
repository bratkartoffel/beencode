package se.wfh.libs.beencode.util;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Abstract helper class for various need.<br>
 *
 * @since 0.1
 */
public final class Tools {
	public static void checkLeadingZero(final boolean started, final int digit)
			throws IOException {
		if (!started && digit == '0') {
			throw new IOException("Leading zeros are not permitted.");
		}
	}

	public static final Charset UTF8 = Charset.forName("UTF-8");

	private Tools() {
		// hide constructor
	}
}
