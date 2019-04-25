package no.nav.opptjening.loot.client.inntektskatt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

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

    private HttpClient httpClient;
    private InntektSkattProperties inntektSkattEndpointUrl;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public InntektSkattClient(Map<String, String> env) throws URISyntaxException {
        this.inntektSkattEndpointUrl = InntektSkattProperties.createFromEnvironment(env);
        this.httpClient = createHttpClient(env);
    }

    public void lagreBeregnetSkatt(LagreBeregnetSkattRequest lagreBeregnetSkattRequest, Token accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(inntektSkattEndpointUrl.getUrl())
                .header("Authorization", accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(lagreBeregnetSkattRequest)))
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Exception while invoking endpoint: " + inntektSkattEndpointUrl.getUrl().toString() + ": " + e.getMessage(), e);
        }

        lagreBeregnetSkattRequestsSentTotalCounter.inc();
        lagreBeregnetSkattRequestsSentCounter.labels(lagreBeregnetSkattRequest.getInntektsaar()).inc();
    }

    private HttpClient createHttpClient(@NotNull Map<String, String> env) {
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(Long.parseLong(restClientProperties.getConnectionTimeout())))
                .build();
    }
}
