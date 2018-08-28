package no.nav.opptjening.loot.client;

import no.nav.opptjening.loot.sts.STSClientConfig;
import no.nav.opptjening.loot.sts.SrvLootStsProperties;
import no.nav.popp.tjenester.inntektskatt.v1.InntektSkattV1;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class InntektSkattClientConfiguration {

    private final static String INNTEKT_SKATT_URL_ENV_NAME = "INNTEKT_SKATT_URL";
    private Map<String, String> env;
    private Supplier<JaxWsProxyFactoryBean> factorySupplier;
    private SrvLootStsProperties srvLootStsProperties;
    private STSClientConfig stsClientConfig;

    public InntektSkattClientConfiguration(Supplier<JaxWsProxyFactoryBean> factorySupplier, Map<String, String> env,
                                           SrvLootStsProperties srvLootStsProperties, STSClientConfig stsClientConfig) {
        this.factorySupplier = factorySupplier;
        this.env = env;
        this.srvLootStsProperties = srvLootStsProperties;
        this.stsClientConfig = stsClientConfig;
    }

    public InntektSkattV1 configureAndGetClient() {
        JaxWsProxyFactoryBean factory = factorySupplier.get();

        String inntektSkattUrl = Optional.ofNullable(env.get(INNTEKT_SKATT_URL_ENV_NAME))
                .orElseThrow(() -> new MissingClientConfig("Missing client property " + INNTEKT_SKATT_URL_ENV_NAME + "."));

        factory.setAddress(inntektSkattUrl);
        factory.setServiceClass(InntektSkattV1.class);
        factory.getFeatures().add(new LoggingFeature());
        InntektSkattV1 client = (InntektSkattV1) factory.create();

        stsClientConfig.konfigurerKlientTilAaSendeStsUtstedtSaml(client, srvLootStsProperties);

        return client;
    }
}
