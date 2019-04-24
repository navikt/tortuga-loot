package no.nav.opptjening.loot.sts;

public interface Token {
    String getAccessToken();

    Long getExpiresIn();

    String getTokenType();

    boolean isExpired();
}
