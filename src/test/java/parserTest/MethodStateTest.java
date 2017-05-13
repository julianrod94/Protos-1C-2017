package parserTest;

import com.protos.app.exceptions.UnsupportedMethodException;
import com.protos.app.parser.Context;
import com.protos.app.parser.MethodState;
import com.protos.app.parser.State;
import com.protos.app.parser.URLState;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;

/**
 * Created by julian on 11/05/17.
 */
public class MethodStateTest {

    private Context context;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testNoMethod() {
        String method = " / HTTP/1.1";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new MethodState(), buffer, ByteBuffer.allocate(10));
        exception.expect(UnsupportedMethodException.class);
        context.getState().handle(context);
    }

    @Test
    public void testLowerCaseMethod() {
        String method = "get / HTTP/1.1";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new MethodState(), buffer, ByteBuffer.allocate(10));
        exception.expect(UnsupportedMethodException.class);
        context.getState().handle(context);
    }

    @Test
    public void testGETMethod() {
        String method = "GET / HTTP/1.1";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new MethodState(), buffer, ByteBuffer.allocate(10));
        State state = context.getState().handle(context);
        Assert.assertEquals(URLState.class, state.getClass());
        Assert.assertEquals(context.getMethod().toString(),"GET");
    }

    @Test
    public void testPOSTMethod() {
        String method = "POST / HTTP/1.1";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new MethodState(), buffer, ByteBuffer.allocate(10));
        State state = context.getState().handle(context);
        Assert.assertEquals(URLState.class, state.getClass());
        Assert.assertEquals(context.getMethod().toString(),"POST");
    }

    @Test
    public void testHEADMethod() {
        String method = "HEAD / HTTP/1.1";
        ByteBuffer buffer = ByteBuffer.allocate(method.length());
        buffer.put(method.getBytes());
        context = new Context(new MethodState(), buffer, ByteBuffer.allocate(10));
        State state = context.getState().handle(context);
        Assert.assertEquals(URLState.class, state.getClass());
        Assert.assertEquals(context.getMethod().toString(),"HEAD");
    }

    @Test
    public void testFragmentedInput() {
        ByteBuffer buffer = ByteBuffer.allocate(30);
        buffer.put("H".getBytes());
        context = new Context(new MethodState(), buffer, ByteBuffer.allocate(10));
        State state = context.getState().handle(context);
        state = context.getState().handle(context);
        context.getQueue().put("E".getBytes());
        state = context.getState().handle(context);
        context.getQueue().put("A".getBytes());
        state = context.getState().handle(context);
        context.getQueue().put("D".getBytes());
        state = context.getState().handle(context);
        context.getQueue().put(" ".getBytes());
        state = context.getState().handle(context);
        Assert.assertEquals(URLState.class, state.getClass());
        Assert.assertEquals(context.getMethod().toString(),"HEAD");
    }
}
