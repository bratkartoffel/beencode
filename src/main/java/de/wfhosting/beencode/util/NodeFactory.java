package de.wfhosting.beencode.util;

import java.io.IOException;
import java.io.InputStream;

import de.wfhosting.beencode.data.BDict;
import de.wfhosting.beencode.data.BInteger;
import de.wfhosting.beencode.data.BList;
import de.wfhosting.beencode.data.BNode;
import de.wfhosting.beencode.data.BString;
import de.wfhosting.common.R;

/**
 * Helper class to create new nodes.<br>
 * This class does not need to be instanced.
 * 
 * @since 0.1
 */
public final class NodeFactory {
	/**
	 * This method tries to create a new node based on the given prefix. The
	 * inputstream is passed directly to the nodes constructor.
	 * 
	 * @param prefix
	 *          The prefix identifying the node to create
	 * @param inp
	 *          The inputstream to read the node data from
	 * 
	 * @throws IOException
	 *           If anything goes wrong while reading from the stream or the read
	 *           values are not parseable.
	 * 
	 * @return The created node
	 */
	public static BNode<?> parseByPrefix(final int prefix, final InputStream inp)
			throws IOException {
		/* the value to return */
		BNode<?> value;

		/* decide which type has the element */
		switch (prefix) {
			case BDict.PREFIX:
				/* the value is also a dict */
				value = new BDict(inp, BDict.PREFIX);
				break;
			case BInteger.PREFIX:
				/* the value is an integer */
				value = new BInteger(inp, BInteger.PREFIX);
				break;
			case BList.PREFIX:
				/* the value is a list */
				value = new BList(inp, BList.PREFIX);
				break;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				/* the value is a string */
				value = new BString(inp, (byte) prefix);
				break;
			default:
				/* unknown prefix for value */
				throw new IOException(R.t(LanguageFields.ERROR_INVALID_PREFIX,
						BNode.class.getSimpleName(), prefix, "d,l,i,0-9"));
		}

		/* node successfully parsed, return it */
		return value;
	}

	private NodeFactory() {
		// hide constructor
	}
}
