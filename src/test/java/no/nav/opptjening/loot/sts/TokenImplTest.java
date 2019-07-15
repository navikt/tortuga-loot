package no.nav.opptjening.loot.sts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenImplTest {

    @Test
    void validToken(){
        Token valid = new TokenImpl("token",3600L,"Bearer");
        assertFalse(valid.isExpired());
    }

    @Test
    void expiredToken() throws InterruptedException {
        Token invalid = new TokenImpl("token",0L,"Bearer");
        Thread.sleep(1000);
        assertTrue(invalid.isExpired());
    }
}