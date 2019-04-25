package no.nav.opptjening.loot.sts;

import java.time.LocalDateTime;

public class TokenImpl implements Token {

    private String access_token;
    private Long expires_in;
    private String token_type;

    private final LocalDateTime issuedAt = LocalDateTime.now();

    public TokenImpl() {
    }

    public TokenImpl(String accessToken, Long expiresIn, String tokenType) {
        this.access_token = accessToken;
        this.expires_in = expiresIn;
        this.token_type = tokenType;
    }

    @Override
    public String getAccessToken() {
        return access_token;
    }

    @Override
    public Long getExpiresIn() {
        return expires_in;
    }

    @Override
    public String getTokenType() {
        return token_type;
    }

    @Override
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(issuedAt.plusSeconds(expires_in));
    }
}
