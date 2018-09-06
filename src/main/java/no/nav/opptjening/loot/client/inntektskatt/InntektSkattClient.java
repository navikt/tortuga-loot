package no.nav.opptjening.loot.client.inntektskatt;

import io.prometheus.client.Counter;
import no.nav.opptjening.loot.client.EndpointSTSClientConfig;
import no.nav.opptjening.loot.client.WsClientBuilder;
import no.nav.opptjening.loot.sts.STSClientBuilder;
import no.nav.opptjening.loot.sts.STSProperties;
import no.nav.popp.tjenester.inntektskatt.v1.InntektSkattV1;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattSikkerhetsbegrensning;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattUgyldigInput;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.trust.STSClient;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.Map;

public class InntektSkattClient {

    private static final Counter lagreBeregnetSkattRequestsSentTotalCounter = Counter.build()
            .name("lagre_beregnet_skatt_requests_sent_total")
            .help("Antall beregnet skatt requester sendt til popp.").register();
    private static final Counter lagreBeregnetSkattRequestsSentCounter = Counter.build()
            .name("lagre_beregnet_skatt_requests_sent")
            .labelNames("year")
            .help("Antall beregnet skatt requester sendt til popp.").register();

    private static InntektSkattV1 port;

    public InntektSkattClient(InntektSkattV1 port) {
        this.port = port;
    }

    @NotNull
    public static InntektSkattClient createFromEnvironment(Map<String, String> env) throws URISyntaxException {
        return createFromEnvironment(env, EndpointSTSClientConfig.STS_SAML_POLICY);
    }

    @NotNull
    public static InntektSkattClient createFromEnvironment(Map<String, String> env, String policyUri) throws URISyntaxException {
        WsClientBuilder wsClientBuilder = new WsClientBuilder(JaxWsProxyFactoryBean::new);
        STSClientBuilder stsClientBuilder = new STSClientBuilder();

        InntektSkattProperties inntektSkattProperties = InntektSkattProperties.createFromEnvironment(env);
        InntektSkattV1 inntektSkattV1 = wsClientBuilder.createPort(inntektSkattProperties.getUrl().toString(), InntektSkattV1.class);

        STSProperties stsProperties = STSProperties.createFromEnvironment(env);
        STSClient stsClient = stsClientBuilder.build(stsProperties);

        EndpointSTSClientConfig endpointSTSClientConfig = new EndpointSTSClientConfig(stsClient);

        inntektSkattV1 = endpointSTSClientConfig.configureRequestSamlToken(inntektSkattV1, policyUri);

        return new InntektSkattClient(inntektSkattV1);
    }

    public void lagreBeregnetSkatt(LagreBeregnetSkattRequest lagreBeregnetSkattRequest)
            throws LagreBeregnetSkattSikkerhetsbegrensning,
                   LagreBeregnetSkattUgyldigInput {
        port.lagreBeregnetSkatt(lagreBeregnetSkattRequest);
        lagreBeregnetSkattRequestsSentTotalCounter.inc();
        lagreBeregnetSkattRequestsSentCounter.labels(lagreBeregnetSkattRequest.getInntektsaar()).inc();
    }
}
