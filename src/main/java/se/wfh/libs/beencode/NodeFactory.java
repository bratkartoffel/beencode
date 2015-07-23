package se.wfh.libs.beencode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NodeFactory {
	@SuppressWarnings("unchecked")
	public static <T extends BNode<?>> T clone(T node) {
		Java6Helper.requireNonNull(node, "node may not be null");

		return (T) node.clone();
	}

	public static BNode<?> decode(byte[] data) throws BencodeException {
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(data);
			return decode(bis);
		} catch (IOException ioe) {
			throw new BencodeException("Internal error while parsing.", ioe);
		} finally {
			Java6Helper.close(bis);
		}
	}

	private static boolean canParse(Class<? extends BNode<?>> clazz, byte prefix)
			throws BencodeException {
		if (clazz.equals(BDict.class)) {
			return BDict.canParsePrefix(prefix);
		} else if (clazz.equals(BInteger.class)) {
			return BInteger.canParsePrefix(prefix);
		} else if (clazz.equals(BList.class)) {
			return BList.canParsePrefix(prefix);
		} else if (clazz.equals(BString.class)) {
			return BString.canParsePrefix(prefix);
		} else {
			throw new IllegalArgumentException("Unknown BNode-Type: " + clazz);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends BNode<?>> T decode(byte[] data, Class<T> expected)
			throws BencodeException {
		if (!canParse(expected, data[0])) {
			throw new BencodeException("The given data is not a valid "
					+ expected.getSimpleName());
		}
		return (T) decode(data);

	}

	public static BNode<?> decode(InputStream is) throws BencodeException {
		try {
			return decode(is, (byte) is.read());
		} catch (IOException e) {
			throw new BencodeException("Error decoding data.", e);
		}
	}

	public static BNode<?> decode(InputStream is, byte prefix)
			throws BencodeException {
		BNode<?> value;
		if (BDict.canParsePrefix(prefix)) {
			value = BDict.empty();
		} else if (BInteger.canParsePrefix(prefix)) {
			value = BInteger.empty();
		} else if (BString.canParsePrefix(prefix)) {
			value = BString.empty();
		} else if (BList.canParsePrefix(prefix)) {
			value = BList.empty();
		} else {
			throw new BencodeException("No parser found for prefix '" + prefix + "'");
		}

		try {
			value.readFrom(is, prefix);
		} catch (IOException ioe) {
			throw new BencodeException("Error decoding data.", ioe);
		}
		return value;
	}

	public static <T extends BNode<?>> void decode(InputStream is, T obj)
			throws BencodeException {
		try {
			obj.readFrom(is, (byte) is.read());
		} catch (IOException ioe) {
			throw new BencodeException("Error decoding data.", ioe);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends BNode<?>> T decode(InputStream is, Class<T> expected)
			throws BencodeException {
		byte prefix;
		try {
			prefix = (byte) is.read();
		} catch (IOException ioe) {
			throw new BencodeException("Error decoding data", ioe);
		}

		if (!canParse(expected, prefix)) {
			throw new BencodeException("The given data is not a valid "
					+ expected.getSimpleName());
		}
		return (T) decode(is, prefix);
	}

	public static byte[] encode(BNode<?> node) throws BencodeException {
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			node.writeTo(bos);
			return bos.toByteArray();
		} catch (IOException ioe) {
			throw new BencodeException("Error encoding data.", ioe);
		} finally {
			Java6Helper.close(bos);
		}
	}

	public static void encode(BNode<?> node, OutputStream os)
			throws BencodeException {
		try {
			node.writeTo(os);
		} catch (IOException ioe) {
			throw new BencodeException("Error encoding data.", ioe);
		}
	}
}
