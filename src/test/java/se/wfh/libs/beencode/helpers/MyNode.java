package se.wfh.libs.beencode.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.wfh.libs.beencode.BNode;

public class MyNode extends BNode<Object> {
	private static final long serialVersionUID = 1L;

	protected MyNode(Object value) {
		super(value);
	}

	@Override
	protected BNode<Object> clone() {
		return null;
	}

	@Override
	protected void writeTo(OutputStream os) throws IOException {
	}

	@Override
	protected void readFrom(InputStream is, byte prefix) throws IOException {
	}
}
