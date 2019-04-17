package no.nav.opptjening.loot.client.inntektskatt;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import no.nav.opptjening.loot.RestClientProperties;

public class RestClientPropertiesTest {

    private static Map<String, String> env = new HashMap<>();

    @Before
    public void before(){
        env.clear();
    }

    @Test
    public void createFromEnviromentWithDefaultValues() {
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        assertEquals(restClientProperties.getConnectionTimeout(), "2000");
        assertEquals(restClientProperties.getReadTimeout(), "5000");
    }

    @Test
    public void createFromEnvironmetWithValuesFromEnv() {
        env.put("REST_CLIENT_CONNECTION_TIMEOUT", "9000");
        env.put("REST_CLIENT_READ_TIMEOUT", "9000");
        RestClientProperties restClientProperties = RestClientProperties.createFromEnvironment(env);
        assertEquals(restClientProperties.getConnectionTimeout(), "9000");
        assertEquals(restClientProperties.getReadTimeout(), "9000");
    }
}