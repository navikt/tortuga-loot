package no.nav.opptjening.loot.client;

import no.nav.opptjening.loot.sts.STSClientConfig;
import no.nav.opptjening.loot.sts.SrvLootStsProperties;
import no.nav.popp.tjenester.inntektskatt.v1.InntektSkattV1;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.util.Map;

public class InntektSkattClientConfiguration {
    
    private static final InntektSkattV1 client = makeClient();

    private static InntektSkattV1 makeClient() {
        Map<String, String> env = System.getenv();
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(env.getOrDefault("INNTEKT_SKATT_ENDPOINT_URL", "localhost:8080/"));
        factory.setServiceClass(InntektSkattV1.class);
        factory.getFeatures().add(new LoggingFeature());
        InntektSkattV1 client = (InntektSkattV1) factory.create();
        STSClientConfig.konfigurerKlientTilAaSendeStsUtstedtSaml(client, new SrvLootStsProperties());
        return client;
    }
}
