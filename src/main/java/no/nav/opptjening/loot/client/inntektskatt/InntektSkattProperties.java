package no.nav.opptjening.loot.client.inntektskatt;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

public class InntektSkattProperties {

    @NotNull
    private final URI url;

    public InntektSkattProperties(@NotNull URI url) {
        this.url = url;
    }

    @NotNull
    public static InntektSkattProperties createFromEnvironment(@NotNull Map<String, String> env)
            throws URISyntaxException, MissingClientConfig {
        return new InntektSkattProperties(
                new URI(Optional.ofNullable(env.get("INNTEKT_SKATT_URL")).orElseThrow(() -> new MissingClientConfig("Missing required property INNTEKT_SKATT_URL")))
        );
    }

    @NotNull
    public URI getUrl() {
        return url;
    }
}
