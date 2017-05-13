package parserTest;

import com.protos.app.exceptions.UnsupportedProtocolException;
import com.protos.app.parser.Context;
import com.protos.app.parser.HostState;
import com.protos.app.parser.ProtocolState;
import com.protos.app.parser.State;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;

/**
 * Created by alexismoragues on 5/13/17.
 */
public class ProtocolStatetTest {
    private Context context;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testHttpProtocol() {
        String method = "http:// ";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new ProtocolState(), buffer, ByteBuffer.allocate(10));
        State state = context.getState().handle(context);
        Assert.assertEquals(HostState.class, state.getClass());
    }

    @Test
    public void testWrongProtocol() {
        String method = "ftp:// ";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new ProtocolState(), buffer, ByteBuffer.allocate(10));
        exception.expect(UnsupportedProtocolException.class);
        State state = context.getState().handle(context);
    }
}
