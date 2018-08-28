package no.nav.opptjening.loot.sts;

import java.util.Optional;

public class SrvLootStsProperties implements STSProperties {

    private static final String username;
    private static final String password;
    private static final String stsUrl;

    static {
        username = getStsPropertiesByEnvName("STS_CLIENT_USERNAME");
        password = getStsPropertiesByEnvName("STS_CLIENT_PASSWORD");
        stsUrl = getStsPropertiesByEnvName("STS_URL");
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

    private static String getStsPropertiesByEnvName(String envName){
        return Optional.ofNullable(System.getenv().get(envName))
                .orElseThrow(()->new MissingStsConfig("Missing STS property " + envName + "."));
    }
}
