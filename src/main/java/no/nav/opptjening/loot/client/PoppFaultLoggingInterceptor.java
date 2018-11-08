package no.nav.opptjening.loot.client;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AttachmentInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


class PoppFaultLoggingInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(PoppFaultLoggingInterceptor.class);
    private static final String CACHED_CONTENT = "no.nav.tortuga.cached-message";

    public PoppFaultLoggingInterceptor() {
        super(Phase.RECEIVE);
        addBefore(AttachmentInInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Exchange exchange = message.getExchange();
        String content = fetchContentFromMessage(message);
        exchange.put(CACHED_CONTENT, content);
    }

    @Override
    public void handleFault(Message message) {
        LOG.warn("The message that faulted was: " + message.getExchange().get(CACHED_CONTENT) + "\nIt had the following " +
                "headers: " + message.get(Message.PROTOCOL_HEADERS) + "\nAnd response-code: " + message.get(Message.RESPONSE_CODE));
    }

    private String fetchContentFromMessage(Message message) {
        String soapMessage = null;
        try {
            InputStream is = message.getContent(InputStream.class);

            if(is!=null) {
                CachedOutputStream bos = new CachedOutputStream();
                IOUtils.copy(is, bos);
                soapMessage = new String(bos.getBytes());
                bos.flush();
                message.setContent(InputStream.class, is);
                is.close();
                InputStream inputStream = new ByteArrayInputStream(soapMessage.getBytes());
                message.setContent(InputStream.class, inputStream);
                bos.close();
            }
        } catch (IOException ioe) {
            LOG.error("Error when caching message. ", ioe);
        }
        return soapMessage;
    }
}