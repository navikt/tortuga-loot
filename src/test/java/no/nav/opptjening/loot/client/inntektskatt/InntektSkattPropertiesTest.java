package no.nav.opptjening.loot.client.inntektskatt;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static no.nav.opptjening.loot.client.inntektskatt.InntektSkattProperties.createFromEnvironment;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class InntektSkattPropertiesTest {

    private static Map<String, String> applicationEnvironment = new HashMap<>();

    @Test
    void createFromEnvironment_throwsMissingClientConfig_when_INNTEKT_SKATT_URL_isNull() throws Exception {
        applicationEnvironment.put("INNTEKT_SKATT_URL", null);
        assertThrows(MissingClientConfig.class, () -> createFromEnvironment(applicationEnvironment));
    }

    @Test
    void createFromEnvironment_throwsURISyntaxException_when_INNTEKT_SKATT_URL_containsIllegalCharacters() throws Exception {
        String malformedUrl = "https://www.malformedUrl .com";
        applicationEnvironment.put("INNTEKT_SKATT_URL", malformedUrl);
        assertThrows(URISyntaxException.class, () -> createFromEnvironment(applicationEnvironment));
    }
}