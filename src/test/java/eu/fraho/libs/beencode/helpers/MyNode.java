package eu.fraho.libs.beencode.helpers;

import eu.fraho.libs.beencode.BNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class MyNode extends BNode<Object> {
    private static final long serialVersionUID = 1L;

    protected MyNode(Object value) {
        super(value);
    }

    @Override
    public void write(@NotNull OutputStream os) throws IOException {

    }
}
