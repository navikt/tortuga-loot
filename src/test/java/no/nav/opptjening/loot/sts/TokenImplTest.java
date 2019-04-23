package no.nav.opptjening.loot.sts;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class TokenImplTest {

    @Test
    public void validToken(){
        Token valid = new TokenImpl("token",3600L,"Bearer");
        assertThat(valid.isExpired(), is(false));
    }

    @Test
    public void expiredToken() throws InterruptedException {
        Token invalid = new TokenImpl("token",0L,"Bearer");
        Thread.sleep(1000);
        assertThat(invalid.isExpired(), is(true));
    }
}