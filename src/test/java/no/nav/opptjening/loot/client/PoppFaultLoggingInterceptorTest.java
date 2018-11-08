package no.nav.opptjening.loot.client;

import org.apache.cxf.io.ReaderInputStream;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PoppFaultLoggingInterceptorTest {

    private Message message;

    PoppFaultLoggingInterceptor in = new PoppFaultLoggingInterceptor();


    @Before
    public void setUp(){
        message = new MessageImpl();
        message.setExchange(new ExchangeImpl());
        InputStream is = new BufferedInputStream( new ReaderInputStream(new StringReader("<message>This is the test-message</message>")));
        message.setContent(InputStream.class, is);

    }

    @Test
    public void makeSureInputStreamIsntConsumed() {
        in.handleMessage(message);

        InputStream is = message.getContent(InputStream.class);
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));

        assertEquals("<message>This is the test-message</message>", result);
    }

    @Test
    public void makeSureContentIsCached() {
        in.handleMessage(message);

        Object cachedResult = message.getExchange().get("no.nav.tortuga.cached-message");

        assertEquals("<message>This is the test-message</message>", cachedResult);
    }
}
