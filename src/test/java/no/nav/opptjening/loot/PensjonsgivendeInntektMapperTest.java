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
        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(1L,2L,3L,4L);
        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(5L,6L);
        PensjonsgivendeInntekt pensjonsgivendeInntekt = new PensjonsgivendeInntekt("12345678901", "2018", fastlandsinntekt, svalbardinntekt);
        InntektSkatt inntektSkatt = PensjonsgivendeInntektMapper.mapToInntektSkatt(pensjonsgivendeInntekt);

        assertEquals((Long) 1L, inntektSkatt.getPersoninntektLoenn());
        assertEquals((Long) 2L, inntektSkatt.getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals((Long) 3L, inntektSkatt.getPersoninntektNaering());
        assertEquals((Long) 4L, inntektSkatt.getPersoninntektBarePensjonsdel());
        assertEquals((Long) 5L, inntektSkatt.getSvalbardLoennLoennstrekkordningen());
        assertEquals((Long) 6L, inntektSkatt.getSvalbardPersoninntektNaering());
    }

    @Test
    public void mapToInntektSkattWithNullValuesOk() {
        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(null,2L,3L,4L);
        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(null,null);
        PensjonsgivendeInntekt pensjonsgivendeInntekt = new PensjonsgivendeInntekt("12345678901", "2018", fastlandsinntekt, svalbardinntekt);
        InntektSkatt inntektSkatt = PensjonsgivendeInntektMapper.mapToInntektSkatt(pensjonsgivendeInntekt);

        assertNull(inntektSkatt.getPersoninntektLoenn());
        assertEquals((Long) 2L, inntektSkatt.getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals((Long) 3L, inntektSkatt.getPersoninntektNaering());
        assertEquals((Long) 4L, inntektSkatt.getPersoninntektBarePensjonsdel());
        assertNull(inntektSkatt.getSvalbardLoennLoennstrekkordningen());
        assertNull(inntektSkatt.getSvalbardPersoninntektNaering());
    }

    @Test
    public void mapToInntektSkattAsNullOk() {
        InntektSkatt inntektSkatt = PensjonsgivendeInntektMapper.mapToInntektSkatt(null);
        assertNull(inntektSkatt);
    }
}
