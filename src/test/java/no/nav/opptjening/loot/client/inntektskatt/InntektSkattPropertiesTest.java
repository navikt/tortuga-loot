package no.nav.opptjening.loot.client.inntektskatt;


import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static no.nav.opptjening.loot.client.inntektskatt.InntektSkattProperties.createFromEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void createsWithEnvOrDefault() throws URISyntaxException {
        applicationEnvironment.put("INNTEKT_SKATT_URL", "http://localhost:9080/api/lol");
        applicationEnvironment.put("RESEND_MAX_BACH_SIZE", "200");
        applicationEnvironment.put("RESEND_INTERVAL", "2500");
        InntektSkattProperties properties = createFromEnvironment(applicationEnvironment);
        assertEquals(properties.getMaxResendBatchSize(), 200L);
        assertEquals(properties.getResendInterval(), 2500L);

        Map<String, String> envWithDefaults = new HashMap<>();
        envWithDefaults.put("INNTEKT_SKATT_URL", "http://localhost:9080/api/lol");
        InntektSkattProperties defaultProps = createFromEnvironment(envWithDefaults);
        assertEquals(defaultProps.getMaxResendBatchSize(), 50L);
        assertEquals(defaultProps.getResendInterval(), 60000L);
    }
}