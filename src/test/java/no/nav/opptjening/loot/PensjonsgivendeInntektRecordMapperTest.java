package no.nav.opptjening.loot;

import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PensjonsgivendeInntektRecordMapperTest {


    @Test
    public void mapToBeregnetSkattRequestWithInntektSkattOk() {
        InntektSkatt inntektSkatt = new InntektSkatt();
        inntektSkatt.setPersoninntektFiskeFangstFamilieBarnehage(9000L);
        inntektSkatt.setPersoninntektLoenn(8000L);
        inntektSkatt.setPersoninntektNaering(7000L);
        inntektSkatt.setPersoninntektBarePensjonsdel(6000L);
        inntektSkatt.setSvalbardLoennLoennstrekkordningen(5000L);
        inntektSkatt.setSvalbardPersoninntektNaering(4000L);

        String recordKey = "2017-12345678901";
        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = PensjonsgivendeInntektRecordMapper
                .mapToLagreBeregnetSkattRequest(recordKey, inntektSkatt);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678901", lagreBeregnetSkattRequest.getPersonIdent());
        assertEquals(9000L, (long) lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals(8000L, (long) lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektLoenn());
        assertEquals(7000L, (long) lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektNaering());
        assertEquals(6000L, (long) lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektBarePensjonsdel());
        assertEquals(5000L, (long) lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardLoennLoennstrekkordningen());
        assertEquals(4000L, (long) lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardPersoninntektNaering());
    }

    @Test
    public void mapToBeregnetSkattRequestWithInntektSkattNullOk() {
        InntektSkatt inntektSkatt = null;

        String key = "2018-12345678902";
        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = PensjonsgivendeInntektRecordMapper
                .mapToLagreBeregnetSkattRequest(key, inntektSkatt);

        assertEquals("2018", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678902", lagreBeregnetSkattRequest.getPersonIdent());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt());
    }
}
