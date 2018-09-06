package no.nav.opptjening.loot.sts;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

public class STSProperties {

    @NotNull
    private final URI url;

    @NotNull
    private final String username;

    @NotNull
    private final String password;

    public STSProperties(@NotNull URI url, @NotNull String username, @NotNull String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @NotNull
    public static STSProperties createFromEnvironment(@NotNull Map<String, String> env)
            throws URISyntaxException {
        return new STSProperties(
                new URI(Optional.of(env.get("STS_URL")).orElseThrow(() -> {
                    throw new MissingStsConfig("Missing required property STS_URL");
                })),
                Optional.of(env.get("STS_CLIENT_USERNAME")).orElseThrow(() -> {
                    throw new MissingStsConfig("Missing required property STS_CLIENT_USERNAME");
                }),
                Optional.of(env.get("STS_CLIENT_PASSWORD")).orElseThrow(() -> {
                    throw new MissingStsConfig("Missing required property STS_CLIENT_PASSWORD");
                })
        );
    }

    @NotNull
    public URI getUrl() {
        return url;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    @NotNull
    public String getPassword() {
        return password;
    }
}
