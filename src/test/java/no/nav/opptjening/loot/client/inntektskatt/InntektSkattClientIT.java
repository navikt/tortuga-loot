package no.nav.opptjening.loot.client.inntektskatt;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InntektSkattClientIT {

    private static final int WIREMOCK_SERVER_PORT = 8080;
    private static final String BEARER_TOKEN = "Bearer " + "eyJ4vaea3";
    private static final String FNR = "01029804032";
    private static final String INNTEKTSAR = "2017";
    private static WireMockServer wireMockServer = new WireMockServer(WIREMOCK_SERVER_PORT);
    private static final String STS_TOKEN_ENDPOINT = "/rest/v1/sts/token";
    private static final String INNTEKT_SKATT_ENDPOINT = "/popp-ws/api/inntekt/ske";

    private static InntektSkattClient inntektSkattClient;

    @BeforeEach
    void setUp() throws URISyntaxException {

        wireMockServer.start();
        Map<String, String> env = new HashMap<>();
        env.put("STS_URL", "http://localhost:" + wireMockServer.port());
        env.put("STS_CLIENT_USERNAME", "testusername");
        env.put("STS_CLIENT_PASSWORD", "testpassword");
        env.put("INNTEKT_SKATT_URL", "http://localhost:" + wireMockServer.port() + INNTEKT_SKATT_ENDPOINT);

        inntektSkattClient = new InntektSkattClient(env);
    }

    @AfterEach
    void teardown() {
        wireMockServer.stop();
    }

    @Test
    @Order(1)
    void shouldThrowExceptionOnHTTPResponse500() {

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(INNTEKT_SKATT_ENDPOINT))
                        .withHeader("Authorization", WireMock.matching(BEARER_TOKEN))
                        .withHeader("Nav-Call-Id", WireMock.notMatching("UUID"))
                        .withHeader("Nav-Consumer-Id", WireMock.matching("tortuga-loot"))
                        .withRequestBody(WireMock.matchingJsonPath("$.[?(@.personIdent == '" + FNR + "')]"))
                        .withRequestBody(WireMock.matchingJsonPath("$.[?(@.inntektsaar == '" + INNTEKTSAR + "')]"))
                //.willReturn(WireMock.serverError())
        );

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent(FNR);
        lagreBeregnetSkattRequest.setInntektsaar(INNTEKTSAR);
        lagreBeregnetSkattRequest.setInntektSKD(null);

        assertThrows(Exception.class, () -> inntektSkattClient.lagreInntektPopp(lagreBeregnetSkattRequest));
    }

    @Test
    @Order(2)
    void lagreBeregnetSkatt() {

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(STS_TOKEN_ENDPOINT))
                .withQueryParam("grant_type", WireMock.matching("client_credentials"))
                .withQueryParam("scope", WireMock.matching("openid"))
                .withHeader("Authorization", WireMock.matching("Basic " + Base64.getEncoder().encodeToString(("testusername" + ":" + "testpassword").getBytes())))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(INNTEKT_SKATT_ENDPOINT))
                .withHeader("Authorization", WireMock.matching("Bearer " + "eyJ4vaea3"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.personIdent == '01029804032')]"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.inntektsaar == '2017')]"))
                .withRequestBody(WireMock.matchingJsonPath("$.inntektSKD",
                        WireMock.equalToJson("{\"personinntektFiskeFangstFamilieBarnehage\":1,\"personinntektLoenn\":2,\"personinntektNaering\":3,"
                                + "\"personinntektBarePensjonsdel\":4,\"svalbardLoennLoennstrekkordningen\":5,\"svalbardPersoninntektNaering\":6}}")))
                .willReturn(WireMock.okJson("{\"LagreBeregnetSkattResponse\":\"{}\"}"))
        );

        InntektSKD inntektSKD = new InntektSKD();
        inntektSKD.setPersoninntektFiskeFangstFamilieBarnehage(1L);
        inntektSKD.setPersoninntektLoenn(2L);
        inntektSKD.setPersoninntektNaering(3L);
        inntektSKD.setPersoninntektBarePensjonsdel(4L);
        inntektSKD.setSvalbardLoennLoennstrekkordningen(5L);
        inntektSKD.setSvalbardPersoninntektNaering(6L);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent("01029804032");
        lagreBeregnetSkattRequest.setInntektsaar("2017");
        lagreBeregnetSkattRequest.setInntektSKD(inntektSKD);

        inntektSkattClient.lagreInntektPopp(lagreBeregnetSkattRequest);
    }

    @Test
    @Order(3)
    void lagreBeregnetSkattWithNullValue() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(INNTEKT_SKATT_ENDPOINT))
                .withHeader("Authorization", WireMock.matching("Bearer " + "eyJ4vaea3"))
                .withHeader("Nav-Call-Id", WireMock.notMatching("UUID"))
                .withHeader("Nav-Consumer-Id", WireMock.matching("tortuga-loot"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.personIdent == '01029804032')]"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.inntektsaar == '2017')]"))
                .willReturn(WireMock.okJson("{\"LagreBeregnetSkattResponse\":\"{}\"}"))
        );

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent("01029804032");
        lagreBeregnetSkattRequest.setInntektsaar("2017");
        lagreBeregnetSkattRequest.setInntektSKD(null);

        inntektSkattClient.lagreInntektPopp(lagreBeregnetSkattRequest);
    }
}
