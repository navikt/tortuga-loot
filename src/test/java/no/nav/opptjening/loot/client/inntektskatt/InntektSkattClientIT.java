package no.nav.opptjening.loot.client.inntektskatt;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.opptjening.loot.sts.TokenClient;

public class InntektSkattClientIT {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule();
    private static final String STSEndpoint = "/SecurityTokenServiceProvider";
    private static final String InntektSkattEndpoint = "/popp-ws/InntektSkatt_v1";

    private InntektSkattClient inntektSkattClient;
    private TokenClient tokenClient;

    private static final Logger LOG = LoggerFactory.getLogger(InntektSkattClientIT.class);

    @Before
    public void setUp() throws URISyntaxException {
        Map<String, String> env = new HashMap<>();
        env.put("STS_URL", "http://localhost:" + wireMockRule.port() + STSEndpoint);
        env.put("STS_CLIENT_USERNAME", "testusername");
        env.put("STS_CLIENT_PASSWORD", "testpassword");
        env.put("INNTEKT_SKATT_URL", "http://localhost:" + wireMockRule.port() + InntektSkattEndpoint);

        inntektSkattClient = new InntektSkattClient(env);
        tokenClient = new TokenClient(env);
    }

    @Test
    public void lagreBeregnetSkatt() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(STSEndpoint))
                .withQueryParam("grant_type", WireMock.matching("client_credentials"))
                .withQueryParam("scope", WireMock.matching("openid"))
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
    public void lagreBeregnetSkattWithNullValue() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(STSEndpoint))
                .withQueryParam("grant_type", WireMock.matching("client_credentials"))
                .withQueryParam("scope", WireMock.matching("openid"))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(InntektSkattEndpoint))
                .withHeader("Authorization", WireMock.matching("Bearer " + "eyJ4vaea3"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.personIdent == '01029804032')]"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.inntektsaar == '2017')]"))
                .withRequestBody(WireMock.matchingJsonPath("$.[?(@.inntektSKD == null)]"))
                .willReturn(WireMock.okJson("{\"LagreBeregnetSkattResponse\":\"{}\"}"))
        );

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent("01029804032");
        lagreBeregnetSkattRequest.setInntektsaar("2017");
        lagreBeregnetSkattRequest.setInntektSKD(null);

        inntektSkattClient.lagreBeregnetSkatt(lagreBeregnetSkattRequest, tokenClient.getAccessToken());
    }
}
