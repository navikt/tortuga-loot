package no.nav.opptjening.loot;


import java.net.URI;

import no.nav.opptjening.loot.client.inntektskatt.InntektSKD;
import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PensjonsgivendeInntektMapperTest {

    private final PensjonsgivendeInntektMapper pensjonsgivendeInntektMapper = new PensjonsgivendeInntektMapper();

    @Test
    void uri() throws Exception {
        URI uri = new URI("http://localhost:8080/foobar");
        assertEquals("http://localhost:8080/foobar", uri.toString());
    }

    @Test
    void mapToInntektSkattOk() {
        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(1L, 2L, 3L, 4L);
        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(5L, 6L);
        PensjonsgivendeInntekt pensjonsgivendeInntekt = new PensjonsgivendeInntekt("12345678901", "2018", fastlandsinntekt, svalbardinntekt);
        InntektSKD inntektSKD = pensjonsgivendeInntektMapper.mapToInntektSkatt(pensjonsgivendeInntekt);

        assertEquals((Long) 1L, inntektSKD.getPersoninntektLoenn());
        assertEquals((Long) 2L, inntektSKD.getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals((Long) 3L, inntektSKD.getPersoninntektNaering());
        assertEquals((Long) 4L, inntektSKD.getPersoninntektBarePensjonsdel());
        assertEquals((Long) 5L, inntektSKD.getSvalbardLoennLoennstrekkordningen());
        assertEquals((Long) 6L, inntektSKD.getSvalbardPersoninntektNaering());
    }

    @Test
    void mapToInntektSkattWithNullValuesOk() {
        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(null, 2L, 3L, 4L);
        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(null, null);
        PensjonsgivendeInntekt pensjonsgivendeInntekt = new PensjonsgivendeInntekt("12345678901", "2018", fastlandsinntekt, svalbardinntekt);
        InntektSKD inntektSKD = pensjonsgivendeInntektMapper.mapToInntektSkatt(pensjonsgivendeInntekt);

        assertNull(inntektSKD.getPersoninntektLoenn());
        assertEquals((Long) 2L, inntektSKD.getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals((Long) 3L, inntektSKD.getPersoninntektNaering());
        assertEquals((Long) 4L, inntektSKD.getPersoninntektBarePensjonsdel());
        assertNull(inntektSKD.getSvalbardLoennLoennstrekkordningen());
        assertNull(inntektSKD.getSvalbardPersoninntektNaering());
    }

    @Test
    void mapToInntektSkattAsNullOk() {
        InntektSKD inntektSKD = pensjonsgivendeInntektMapper.mapToInntektSkatt(null);
        assertNull(inntektSKD);
    }
}
