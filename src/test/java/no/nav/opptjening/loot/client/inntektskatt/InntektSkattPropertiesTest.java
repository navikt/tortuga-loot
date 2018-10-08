package no.nav.opptjening.loot.client.inntektskatt;

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class InntektSkattPropertiesTest {

    private static Map<String, String> applicationEnvironment = new HashMap<>();

    @Test(expected = MissingClientConfig.class)
    public void createFromEnvironment_throwsMissingClientConfig_when_INNTEKT_SKATT_URL_isNull() throws Exception {
        applicationEnvironment.put("INNTEKT_SKATT_URL", null);
        InntektSkattProperties.createFromEnvironment(applicationEnvironment);
    }

    @Test(expected = URISyntaxException.class)
    public void createFromEnvironment_throwsURISyntaxException_when_INNTEKT_SKATT_URL_containsIllegalCharacters() throws Exception {
        String malformedUrl = "https://www.malformedUrl .com";
        applicationEnvironment.put("INNTEKT_SKATT_URL", malformedUrl);
        InntektSkattProperties.createFromEnvironment(applicationEnvironment);
    }
}