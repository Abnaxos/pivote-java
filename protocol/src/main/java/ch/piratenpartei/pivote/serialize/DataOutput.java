package ch.piratenpartei.pivote.serialize;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DataOutput implements Closeable {

    private final OutputStream output;
    private final SerializationContext context;

    public DataOutput(OutputStream output, SerializationContext context) {
        this.output = output;
        this.context = context;
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
