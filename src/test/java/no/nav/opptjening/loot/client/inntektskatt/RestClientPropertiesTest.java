package no.nav.opptjening.loot.client.inntektskatt;

import java.util.HashMap;
import java.util.Map;

import no.nav.opptjening.loot.RestClientProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestClientPropertiesTest {

    private static Map<String, String> env = new HashMap<>();

    @BeforeEach
    void setUp(){
        env.clear();
    }

    @Test
    void createFromEnviromentWithDefaultValues() {
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        assertEquals(restClientProperties.getConnectionTimeout(), "2000");
        assertEquals(restClientProperties.getReadTimeout(), "5000");
    }

    @Test
    void createFromEnvironmetWithValuesFromEnv() {
        env.put("REST_CLIENT_CONNECTION_TIMEOUT", "9000");
        env.put("REST_CLIENT_READ_TIMEOUT", "9000");
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        assertEquals(restClientProperties.getConnectionTimeout(), "9000");
        assertEquals(restClientProperties.getReadTimeout(), "9000");
    }
}