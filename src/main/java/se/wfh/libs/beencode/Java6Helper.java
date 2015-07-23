package se.wfh.libs.beencode;

import java.io.Closeable;
import java.io.IOException;

final class Java6Helper {
	/**
	 * Objects.requireNonNull(Object, String)
	 * 
	 * @param a
	 * @param msg
	 * @return
	 */
	static <T> T requireNonNull(T a, String msg) {
		if (a == null) {
			throw new NullPointerException(msg);
		}

		return a;
	}

	public static void close(Closeable stream) {
		if (stream == null) {
			return;
		}

		try {
			stream.close();
		} catch (IOException e) {
			// ignore as with java 7+ try-with-resources
		}
	}
}
