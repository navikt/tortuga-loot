package no.nav.opptjening.loot.client.inntektskatt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Counter;

import no.nav.opptjening.loot.RestClientProperties;
import no.nav.opptjening.loot.sts.Token;
import no.nav.opptjening.loot.sts.TokenClient;

public class InntektSkattClient {

    private static final Logger LOG = LoggerFactory.getLogger(InntektSkattClient.class);
    private static final Counter lagreBeregnetSkattRequestsSentTotalCounter = Counter.build()
            .name("lagre_beregnet_skatt_requests_sent_total")
            .help("Antall beregnet skatt requester sendt til popp.").register();
    private static final Counter lagreBeregnetSkattRequestsSentCounter = Counter.build()
            .name("lagre_beregnet_skatt_requests_sent")
            .labelNames("year")
            .help("Antall beregnet skatt requester sendt til popp.").register();

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private HttpClient httpClient;
    private InntektSkattProperties inntektSkattProperties;
    private TokenClient tokenClient;
    private ArrayDeque<LagreBeregnetSkattRequest> inMemBackoutQueue = new ArrayDeque<>();

    public InntektSkattClient(Map<String, String> env) throws URISyntaxException {
        this.inntektSkattProperties = InntektSkattProperties.createFromEnvironment(env);
        this.httpClient = createHttpClient(env);
        this.tokenClient = new TokenClient(env);
    }

    InntektSkattClient() {
    }

    public void lagreInntektPopp(LagreBeregnetSkattRequest lagreBeregnetSkattRequest) throws Exception {
        handleResponse(invokePopp(lagreBeregnetSkattRequest), lagreBeregnetSkattRequest);

        if (!inMemBackoutQueue.isEmpty()) {
            LagreBeregnetSkattRequest toRetry = inMemBackoutQueue.pop();
            LOG.debug("Resending record for person:{}, year:{} from in-mem backout queue", toRetry.getPersonIdent(), toRetry.getInntektsaar());
            handleResponse(invokePopp(toRetry), toRetry);
        }
    }

    private HttpResponse invokePopp(LagreBeregnetSkattRequest lagreBeregnetSkattRequest) {
        LOG.debug("Saving record for person:{}, year:{}", lagreBeregnetSkattRequest.getPersonIdent(), lagreBeregnetSkattRequest.getInntektsaar());
        try {
            return httpClient.send(createRequest(lagreBeregnetSkattRequest), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            LOG.error("Exception while invoking endpoint:{}, message:{}", inntektSkattProperties.getUrl().toString(), e.getMessage());
            throw new RuntimeException("Exception while invoking endpoint: " + inntektSkattProperties.getUrl().toString() + ": " + e.getMessage(), e);
        }
    }

    private void handleResponse(HttpResponse response, LagreBeregnetSkattRequest lagreBeregnetSkattRequest) throws Exception {
        if (response.statusCode() == 200) {
            incrementCounters(lagreBeregnetSkattRequest);
        } else if (response.statusCode() >= 400 && response.statusCode() < 500) {
            //todo
        } else {
            throw new Exception(
                    "Request to POPP failed with status: " + response.statusCode() + ", message:" + response.body() + ", for person:" + lagreBeregnetSkattRequest.getPersonIdent()
                            + " , year: " + lagreBeregnetSkattRequest.getInntektsaar());
        }
    }

    private void incrementCounters(LagreBeregnetSkattRequest lagreBeregnetSkattRequest) {
        lagreBeregnetSkattRequestsSentTotalCounter.inc();
        lagreBeregnetSkattRequestsSentCounter.labels(lagreBeregnetSkattRequest.getInntektsaar()).inc();
    }

    private HttpRequest createRequest(LagreBeregnetSkattRequest lagreBeregnetSkattRequest) {
        Token accessToken = tokenClient.getAccessToken();
        return HttpRequest.newBuilder()
                .uri(inntektSkattProperties.getUrl())
                .header("Authorization", accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .header("Content-Type", "application/json")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
                .header("Nav-Consumer-Id", "tortuga-loot")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(lagreBeregnetSkattRequest)))
                .build();
    }

    private HttpClient createHttpClient(@NotNull Map<String, String> env) {
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(Long.parseLong(restClientProperties.getConnectionTimeout())))
                .build();
    }
}
