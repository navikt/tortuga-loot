package no.nav.opptjening.loot.client.inntektskatt;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class InntektSkattProperties {

    private static final String DEFAULT_RESEND_MAX_BACH_SIZE = "50";
    private static final String DEFAULT_RESEND_INTERVAL = "60000";

    @NotNull
    private final URI url;
    private final Long maxResendBatchSize;
    private final Long resendInterval;

    public InntektSkattProperties(@NotNull URI url, @NotNull Long maxResendBatchSize, @NotNull Long resendInterval) {
        this.url = url;
        this.maxResendBatchSize = maxResendBatchSize;
        this.resendInterval = resendInterval;
    }

    @NotNull
    public static InntektSkattProperties createFromEnvironment(@NotNull Map<String, String> env) throws URISyntaxException, MissingClientConfig {
        return new InntektSkattProperties(
                new URI(Optional.ofNullable(env.get("INNTEKT_SKATT_URL")).orElseThrow(() -> new MissingClientConfig("Missing required property INNTEKT_SKATT_URL"))),
                Long.valueOf(env.getOrDefault("RESEND_MAX_BACH_SIZE", DEFAULT_RESEND_MAX_BACH_SIZE)),
                Long.valueOf(env.getOrDefault("RESEND_INTERVAL", DEFAULT_RESEND_INTERVAL))
        );
    }

    @NotNull
    public URI getUrl() {
        return url;
    }

    public String getImage() {
        String image = System.getenv("NAIS_APP_IMAGE");
        return (image == null || image.isBlank()) ? "debug" : image;
    }

    public Long getMaxResendBatchSize() {
        return maxResendBatchSize;
    }

    public Long getResendInterval() {
        return resendInterval;
    }
}
