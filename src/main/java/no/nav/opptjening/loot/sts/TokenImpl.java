package no.nav.opptjening.loot.sts;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenImpl implements Token {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("token_type")
    private String tokenType;

    private final LocalDateTime issuedAt = LocalDateTime.now();

    public TokenImpl() {
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Long getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(issuedAt.plusSeconds(expiresIn));
    }
}
