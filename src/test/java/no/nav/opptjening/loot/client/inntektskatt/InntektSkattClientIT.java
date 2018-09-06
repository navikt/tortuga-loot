package no.nav.opptjening.loot.client.inntektskatt;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.MatchesXPathPattern;
import no.nav.opptjening.loot.client.EndpointSTSClientConfig;
import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class InntektSkattClientIT {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    InntektSkattClient inntektSkattClient;

    private static final Logger LOG = LoggerFactory.getLogger(InntektSkattClientIT.class);

    @Before
    public void setUp() throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("STS_URL", "http://localhost:" + wireMockRule.port() + "/SecurityTokenServiceProvider");
        env.put("STS_CLIENT_USERNAME", "testusername");
        env.put("STS_CLIENT_PASSWORD", "testpassword");
        env.put("INNTEKT_SKATT_URL", "http://localhost:" + wireMockRule.port() + "/popp-ws/InntektSkatt_v1");

        inntektSkattClient = InntektSkattClient.createFromEnvironment(env, EndpointSTSClientConfig.STS_SAML_POLICY_NO_TRANSPORT_BINDING);
    }

    @After
    public void tearDown() throws Exception {
    }

    private static String readTestResource(String file) throws Exception {
        URL url = InntektSkattClientIT.class.getResource(file);
        if (url == null) {
            throw new RuntimeException("Could not find file " + file);
        }
        Path resourcePath = Paths.get(url.toURI());
        return new String(Files.readAllBytes(resourcePath), StandardCharsets.UTF_8);
    }

    @Test
    public void lagreBeregnetSkatt() throws Exception {
        Map<String, String> ns3 = new HashMap<>();
        ns3.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns3.put("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

        Map<String, String> ns4 = new HashMap<>();
        ns4.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns4.put("wst", "http://docs.oasis-open.org/ws-sx/ws-trust/200512");

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/SecurityTokenServiceProvider"))
                .withHeader("SOAPAction", WireMock.equalTo("\"http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue\""))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/wsse:UsernameToken/wsse:Username/text()",
                        ns3, WireMock.equalTo("testusername")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/wsse:UsernameToken/wsse:Password/text()",
                        ns3, WireMock.equalTo("testpassword")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:SecondaryParameters/wst:SecondaryParameters/wst:TokenType/text()",
                        ns4, WireMock.equalTo("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:RequestType/text()",
                        ns4, WireMock.equalTo("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:SecondaryParameters/wst:TokenType/text()",
                        ns4, WireMock.containing("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:SecondaryParameters/wst:KeyType/text()",
                        ns4, WireMock.equalTo("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer")))
                .willReturn(WireMock.okXml(readTestResource("/sts-response.xml"))));

        Map<String, String> ns1 = new HashMap<>();
        ns1.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns1.put("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        ns1.put("saml2", "urn:oasis:names:tc:SAML:2.0:assertion");

        Map<String, String> ns2 = new HashMap<>();
        ns2.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns2.put("ns2", "http://nav.no/popp/tjenester/inntektskatt/v1");

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/popp-ws/InntektSkatt_v1"))
                .withHeader("SOAPAction", WireMock.equalTo("\"http://nav.no/popp/tjenester/inntektskatt/v1/inntektSkatt_v1/LagreBeregnetSkattRequest\""))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/saml2:Assertion/saml2:Issuer/text()",
                        ns1, WireMock.equalTo("theIssuer")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"DigestValue\"]/text()",
                        ns1, WireMock.equalTo("digestValue")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"SignatureValue\"]/text()",
                        ns1, WireMock.equalTo("signatureValue")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"X509Certificate\"]/text()",
                        ns1, WireMock.equalTo("certificateValue")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"X509IssuerName\"]/text()",
                        ns1, WireMock.equalTo("CN=B27 Issuing CA Intern, DC=preprod, DC=local")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/saml2:Assertion/saml2:Subject/saml2:NameID/text()",
                        ns1, WireMock.equalTo("testusername")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/saml2:Assertion/saml2:AttributeStatement/saml2:Attribute/saml2:AttributeValue/text()",
                        ns1, WireMock.equalTo("testusername")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/personIdent/text()",
                        ns2, WireMock.equalTo("01029804032")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektsaar/text()",
                        ns2, WireMock.equalTo("2017")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektSkatt/personinntektFiskeFangstFamilieBarnehage/text()",
                        ns2, WireMock.equalTo("1")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektSkatt/personinntektLoenn/text()",
                        ns2, WireMock.equalTo("2")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektSkatt/personinntektNaering/text()",
                        ns2, WireMock.equalTo("3")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektSkatt/personinntektBarePensjonsdel/text()",
                        ns2, WireMock.equalTo("4")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektSkatt/svalbardLoennLoennstrekkordningen/text()",
                        ns2, WireMock.equalTo("5")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektSkatt/svalbardPersoninntektNaering/text()",
                        ns2, WireMock.equalTo("6")))
                .willReturn(WireMock.okXml(readTestResource("/popp-response.xml"))));

        InntektSkatt inntektSkatt = new InntektSkatt();
        inntektSkatt.setPersoninntektFiskeFangstFamilieBarnehage(1L);
        inntektSkatt.setPersoninntektLoenn(2L);
        inntektSkatt.setPersoninntektNaering(3L);
        inntektSkatt.setPersoninntektBarePensjonsdel(4L);
        inntektSkatt.setSvalbardLoennLoennstrekkordningen(5L);
        inntektSkatt.setSvalbardPersoninntektNaering(6L);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent("01029804032");
        lagreBeregnetSkattRequest.setInntektsaar("2017");
        lagreBeregnetSkattRequest.setInntektSkatt(inntektSkatt);

        inntektSkattClient.lagreBeregnetSkatt(lagreBeregnetSkattRequest);
    }

    @Test
    public void lagreBeregnetSkattWithNullValue() throws Exception {
        Map<String, String> ns3 = new HashMap<>();
        ns3.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns3.put("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

        Map<String, String> ns4 = new HashMap<>();
        ns4.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns4.put("wst", "http://docs.oasis-open.org/ws-sx/ws-trust/200512");

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/SecurityTokenServiceProvider"))
                .withHeader("SOAPAction", WireMock.equalTo("\"http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue\""))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/wsse:UsernameToken/wsse:Username/text()",
                        ns3, WireMock.equalTo("testusername")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/wsse:UsernameToken/wsse:Password/text()",
                        ns3, WireMock.equalTo("testpassword")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:SecondaryParameters/wst:SecondaryParameters/wst:TokenType/text()",
                        ns4, WireMock.equalTo("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:RequestType/text()",
                        ns4, WireMock.equalTo("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:SecondaryParameters/wst:TokenType/text()",
                        ns4, WireMock.containing("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/wst:RequestSecurityToken/wst:SecondaryParameters/wst:KeyType/text()",
                        ns4, WireMock.equalTo("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer")))
                .willReturn(WireMock.okXml(readTestResource("/sts-response.xml"))));

        Map<String, String> ns1 = new HashMap<>();
        ns1.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns1.put("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        ns1.put("saml2", "urn:oasis:names:tc:SAML:2.0:assertion");

        Map<String, String> ns2 = new HashMap<>();
        ns2.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        ns2.put("ns2", "http://nav.no/popp/tjenester/inntektskatt/v1");

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/popp-ws/InntektSkatt_v1"))
                .withHeader("SOAPAction", WireMock.equalTo("\"http://nav.no/popp/tjenester/inntektskatt/v1/inntektSkatt_v1/LagreBeregnetSkattRequest\""))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/saml2:Assertion/saml2:Issuer/text()",
                        ns1, WireMock.equalTo("theIssuer")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"DigestValue\"]/text()",
                        ns1, WireMock.equalTo("digestValue")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"SignatureValue\"]/text()",
                        ns1, WireMock.equalTo("signatureValue")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"X509Certificate\"]/text()",
                        ns1, WireMock.equalTo("certificateValue")))
                .withRequestBody(new MatchesXPathPattern("//*[local-name()=\"X509IssuerName\"]/text()",
                        ns1, WireMock.equalTo("CN=B27 Issuing CA Intern, DC=preprod, DC=local")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/saml2:Assertion/saml2:Subject/saml2:NameID/text()",
                        ns1, WireMock.equalTo("testusername")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Header/wsse:Security/saml2:Assertion/saml2:AttributeStatement/saml2:Attribute/saml2:AttributeValue/text()",
                        ns1, WireMock.equalTo("testusername")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/personIdent/text()",
                        ns2, WireMock.equalTo("01029804032")))
                .withRequestBody(new MatchesXPathPattern("//soap:Envelope/soap:Body/ns2:lagreBeregnetSkatt/request/inntektsaar/text()",
                        ns2, WireMock.equalTo("2017")))
                .willReturn(WireMock.okXml(readTestResource("/popp-response.xml"))));

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();
        lagreBeregnetSkattRequest.setPersonIdent("01029804032");
        lagreBeregnetSkattRequest.setInntektsaar("2017");
        lagreBeregnetSkattRequest.setInntektSkatt(null);

        inntektSkattClient.lagreBeregnetSkatt(lagreBeregnetSkattRequest);
    }
}
