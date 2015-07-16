package se.wfh.libs.beencode;

import java.io.IOException;

public class BencodeException extends IOException {
	private static final long serialVersionUID = 1L;

	public BencodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public BencodeException(String message) {
		super(message);
	}
}
