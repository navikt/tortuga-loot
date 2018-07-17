package no.nav.opptjening.loot;

import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PensjonsgivendeInntektMapperTest {

    @Test
    public void mapToInntektSkattOk() {
        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(0L,1L,2L,3L);
        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(4L,5L);
        PensjonsgivendeInntekt pensjonsgivendeInntekt = new PensjonsgivendeInntekt("12345678901", "2018", fastlandsinntekt, svalbardinntekt);
        InntektSkatt inntektSkatt = PensjonsgivendeInntektMapper.mapToInntektSkatt(pensjonsgivendeInntekt);

        assertEquals(0L, (long) inntektSkatt.getPersoninntektLoenn());
        assertEquals(1L, (long) inntektSkatt.getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals(2L, (long) inntektSkatt.getPersoninntektNaering());
        assertEquals(3L, (long) inntektSkatt.getPersoninntektBarePensjonsdel());
        assertEquals(4L, (long) inntektSkatt.getSvalbardLoennLoennstrekkordningen());
        assertEquals(5L, (long) inntektSkatt.getSvalbardPersoninntektNaering());
    }

    @Test
    public void mapToInntektSkattWithNullOk() {
        InntektSkatt inntektSkatt = PensjonsgivendeInntektMapper.mapToInntektSkatt(null);
        assertNull(inntektSkatt);
    }
}
