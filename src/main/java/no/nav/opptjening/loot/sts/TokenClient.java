package no.nav.opptjening.loot.sts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.logstash.logback.encoder.org.apache.commons.lang.Validate;

import no.nav.opptjening.loot.RestClientProperties;

public class TokenClient {
    private static final Logger LOG = LoggerFactory.getLogger(TokenClient.class);

    private Token accessToken;
    private STSProperties stsProperties;
    private Client restClient;

    public TokenClient(@NotNull Map<String, String> env) throws URISyntaxException {
        this.stsProperties = STSProperties.createFromEnvironment(env);
        this.restClient = createRestClient(env);
    }

    public synchronized Token getAccessToken() {
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = getTokenFromProvider();
        }
        LOG.debug("Returning cached and valid access-token for user: {}", stsProperties.getUsername());
        return accessToken;
    }

    private Token getTokenFromProvider() {
        LOG.debug("Getting new access-token for user: {} from: {}", stsProperties.getUsername(), getTokenUrl());
        Response response = this.restClient.target(getTokenUrl())
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic" + Base64.getEncoder().encodeToString((stsProperties.getUsername() + ":" + stsProperties.getPassword()).getBytes()))
                .get();
        return extractToken(response);
    }

    private Token extractToken(Response response) {
        Validate.notNull(response, "Response from token service is null");
        Validate.isTrue(response.getStatus() == HttpStatus.OK_200);
        try {
            accessToken = new ObjectMapper().readValue(response.readEntity(String.class), TokenImpl.class);
            LOG.debug("Successfully retrieved access-token for user: {} from: {}", stsProperties.getUsername(), getTokenUrl());
            return accessToken;
        } catch (IOException e) {
            throw new RuntimeException("Exception while extracting token" + e.getMessage(), e);
        }
    }

    private Client createRestClient(@NotNull Map<String, String> env) {
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        return ClientBuilder.newBuilder()
                .connectTimeout(Long.parseLong(restClientProperties.getConnectionTimeout()), TimeUnit.MILLISECONDS)
                .readTimeout(Long.parseLong(restClientProperties.getReadTimeout()), TimeUnit.MILLISECONDS)
                .build();
    }

    private String getTokenUrl() {
        return stsProperties.getUrl() + "/rest/v1/sts/token";
    }
}
