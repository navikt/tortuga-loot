package no.nav.opptjening.loot.client.inntektskatt;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import io.prometheus.client.Counter;

import no.nav.opptjening.loot.RestClientProperties;
import no.nav.opptjening.loot.sts.Token;

public class InntektSkattClient {

    private static final Counter lagreBeregnetSkattRequestsSentTotalCounter = Counter.build()
            .name("lagre_beregnet_skatt_requests_sent_total")
            .help("Antall beregnet skatt requester sendt til popp.").register();
    private static final Counter lagreBeregnetSkattRequestsSentCounter = Counter.build()
            .name("lagre_beregnet_skatt_requests_sent")
            .labelNames("year")
            .help("Antall beregnet skatt requester sendt til popp.").register();

    private Client restClient;
    private InntektSkattProperties inntektSkattEndpointUrl;

    public InntektSkattClient(Map<String, String> env) throws URISyntaxException {
        this.inntektSkattEndpointUrl = InntektSkattProperties.createFromEnvironment(env);
        this.restClient = createRestClient(env);
    }

    public void lagreBeregnetSkatt(LagreBeregnetSkattRequest lagreBeregnetSkattRequest, Token accessToken) {
        restClient.target(inntektSkattEndpointUrl.getUrl())
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .post(Entity.json(lagreBeregnetSkattRequest));

        lagreBeregnetSkattRequestsSentTotalCounter.inc();
        lagreBeregnetSkattRequestsSentCounter.labels(lagreBeregnetSkattRequest.getInntektsaar()).inc();
    }

    private static Client createRestClient(Map<String, String> env) {
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        return ClientBuilder.newBuilder()
                .connectTimeout(Long.parseLong(restClientProperties.getConnectionTimeout()), TimeUnit.MILLISECONDS)
                .readTimeout(Long.parseLong(restClientProperties.getReadTimeout()), TimeUnit.MILLISECONDS)
                .build();
    }
}
