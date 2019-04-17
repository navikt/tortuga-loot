package no.nav.opptjening.loot;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class RestClientProperties {

    private static final String defaultConnectionTimeout = "2000";
    private static final String defaultReadTimeout = "5000";

    @NotNull
    private final String connectionTimeout;
    @NotNull
    private final String readTimeout;

    public RestClientProperties(@NotNull String connectionTimeout, @NotNull String readTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    @NotNull
    public static RestClientProperties createFromEnvironment(@NotNull Map<String, String> env) {
        return new RestClientProperties(
                env.getOrDefault("TOKEN_CLIENT_CONNECTION_TIMEOUT", defaultConnectionTimeout),
                env.getOrDefault("TOKEN_CLIENT_READ_TIMEOUT", defaultReadTimeout));
    }

    @NotNull
    public String getConnectionTimeout() {
        return connectionTimeout;
    }

    @NotNull
    public String getReadTimeout() {
        return readTimeout;
    }
}
