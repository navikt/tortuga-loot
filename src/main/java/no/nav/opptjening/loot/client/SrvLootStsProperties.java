package no.nav.opptjening.loot.client;

import java.util.Map;

public class SrvLootStsProperties implements STSProperties {

    private static final String username;
    private static final String password;
    private static final String stsUrl;

    static {
        Map<String, String> env = System.getenv();
        username = env.getOrDefault("STS_CLIENT_USERNAME", "srvtortuga-loot");
        password = env.getOrDefault("STS_CLIENT_USERNAME", "srvtortuga-loot");
        stsUrl = env.getOrDefault("STS_URL", "srvtortuga-loot");
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getStsUrl() {
        return stsUrl;
    }
}
