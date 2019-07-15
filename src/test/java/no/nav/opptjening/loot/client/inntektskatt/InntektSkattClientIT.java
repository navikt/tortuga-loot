package no.nav.opptjening.loot.client.inntektskatt;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.opptjening.loot.sts.TokenClient;

class InntektSkattClientIT {

    private static final int WIREMOCK_SERVER_PORT = 8080;
    private static WireMockServer wireMockServer = new WireMockServer(WIREMOCK_SERVER_PORT);
    private static final String STSTokenEndpoint = "/rest/v1/sts/token";
    private static final String InntektSkattEndpoint = "/popp-ws/api/lagre-inntekt-skd";

    private static InntektSkattClient inntektSkattClient;
    private static TokenClient tokenClient;

    @BeforeAll
    static void setUp() throws URISyntaxException {
        wireMockServer.start();
        Map<String, String> env = new HashMap<>();
        env.put("STS_URL", "http://localhost:" + wireMockServer.port());
        env.put("STS_CLIENT_USERNAME", "testusername");
        env.put("STS_CLIENT_PASSWORD", "testpassword");
        env.put("INNTEKT_SKATT_URL", "http://localhost:" + wireMockServer.port() + InntektSkattEndpoint);

        inntektSkattClient = new InntektSkattClient(env);
        tokenClient = new TokenClient(env);
    }

    @Test
    void lagreBeregnetSkatt() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(STSTokenEndpoint))
                .withQueryParam("grant_type", WireMock.matching("client_credentials"))
                .withQueryParam("scope", WireMock.matching("openid"))
                .withHeader("Authorization", WireMock.matching("Basic " + Base64.getEncoder().encodeToString(("testusername" + ":" + "testpassword").getBytes())))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(InntektSkattEndpoint))
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

        inntektSkattClient.lagreBeregnetSkatt(lagreBeregnetSkattRequest, tokenClient.getAccessToken());
    }

    @Test
    void lagreBeregnetSkattWithNullValue() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(STSTokenEndpoint))
                .withQueryParam("grant_type", WireMock.matching("client_credentials"))
                .withQueryParam("scope", WireMock.matching("openid"))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(InntektSkattEndpoint))
                .withHeader("Authorization", WireMock.matching("Bearer " + "eyJ4vaea3"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.personIdent == '01029804032')]"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.inntektsaar == '2017')]"))
                .willReturn(WireMock.okJson("{\"LagreBeregnetSkattResponse\":\"{}\"}"))
        );

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent("01029804032");
        lagreBeregnetSkattRequest.setInntektsaar("2017");
        lagreBeregnetSkattRequest.setInntektSKD(null);

        inntektSkattClient.lagreBeregnetSkatt(lagreBeregnetSkattRequest, tokenClient.getAccessToken());
    }
}
