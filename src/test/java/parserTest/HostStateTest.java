package parserTest;

import com.protos.app.exceptions.UnsupportedMethodException;
import com.protos.app.parser.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * Created by alexismoragues on 5/13/17.
 */
public class HostStateTest {
    private Context context;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testHostParser() {
        String method = "www.google.com";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new HostState(), buffer, ByteBuffer.allocate(10));
        State state = context.getState().handle(context);
        Assert.assertEquals(HostState.class, state.getClass());
    }

    @Test
    public void testHostURIParser() {
        String method = "www.google.com/revenge";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new HostState(), buffer, ByteBuffer.allocate(10));
        State state = context.getState().handle(context);
        Assert.assertEquals(URIState.class, state.getClass());
    }
}
