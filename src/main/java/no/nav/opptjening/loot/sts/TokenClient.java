package no.nav.opptjening.loot.sts;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.logstash.logback.encoder.org.apache.commons.lang.Validate;

import no.nav.opptjening.loot.RestClientProperties;

public class TokenClient {
    private static final Logger LOG = LoggerFactory.getLogger(TokenClient.class);

    private Token accessToken;
    private STSProperties stsProperties;
    private HttpClient httpClient;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TokenClient(@NotNull Map<String, String> env) throws URISyntaxException {
        this.stsProperties = STSProperties.createFromEnvironment(env);
        this.httpClient = createHttpClient(env);
    }

    public synchronized Token getAccessToken() {
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = getTokenFromProvider();
        }
        return accessToken;
    }

    private Token getTokenFromProvider() {
        LOG.info("Getting new access-token for user: {} from: {}", stsProperties.getUsername(), getTokenUrl());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getTokenUrl())
                .header("Authorization", "Basic" + " " + Base64.getEncoder().encodeToString((stsProperties.getUsername() + ":" + stsProperties.getPassword()).getBytes()))
                .GET()
                .build();

        HttpResponse response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Exception while getting token from provider: " + e.getMessage(), e);
        }

        return extractToken(response);
    }

    private Token extractToken(HttpResponse response) {
        Validate.notNull(response, "Response from token service is null");
        Validate.isTrue(response.statusCode() == 200);
        LOG.info("Successfully retrieved access-token for user: {} from: {}", stsProperties.getUsername(), getTokenUrl());
        return gson.fromJson((String) response.body(), TokenImpl.class);
    }

    private HttpClient createHttpClient(@NotNull Map<String, String> env) {
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(Long.parseLong(restClientProperties.getConnectionTimeout())))
                .build();
    }

    private URI getTokenUrl() {
        return URI.create(stsProperties.getUrl() + "/rest/v1/sts/token?grant_type=client_credentials&scope=openid");
    }
}
