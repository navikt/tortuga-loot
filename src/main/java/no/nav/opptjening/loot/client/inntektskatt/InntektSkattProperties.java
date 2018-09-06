package no.nav.opptjening.loot.client.inntektskatt;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class InntektSkattProperties {

    @NotNull
    private final URI url;

    public InntektSkattProperties(@NotNull URI url) {
        this.url = url;
    }

    @NotNull
    public static InntektSkattProperties createFromEnvironment(@NotNull Map<String, String> env)
            throws URISyntaxException {
        return new InntektSkattProperties(
                new URI(env.computeIfAbsent("INNTEKT_SKATT_URL", s -> {
                    throw new MissingClientConfig("Missing required property INNTEKT_SKATT_URL");
                }))
        );
    }

    @NotNull
    public URI getUrl() {
        return url;
    }
}
