package no.nav.opptjening.loot;

import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PensjonsgivendeInntektRecordMapperTest {

    private final PensjonsgivendeInntektRecordMapper pensjonsgivendeInntektRecordMapper = new PensjonsgivendeInntektRecordMapper();

    @Test
    public void mapToBeregnetSkattRequestWithInntektSkattOk() {
        InntektSkatt inntektSkatt = new InntektSkatt();
        inntektSkatt.setPersoninntektFiskeFangstFamilieBarnehage(9000L);
        inntektSkatt.setPersoninntektLoenn(8000L);
        inntektSkatt.setPersoninntektNaering(7000L);
        inntektSkatt.setPersoninntektBarePensjonsdel(6000L);
        inntektSkatt.setSvalbardLoennLoennstrekkordningen(5000L);
        inntektSkatt.setSvalbardPersoninntektNaering(4000L);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper
                .mapToLagreBeregnetSkattRequest("2017", "12345678901", inntektSkatt);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678901", lagreBeregnetSkattRequest.getPersonIdent());
        assertEquals((Long) 9000L, lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals((Long) 8000L, lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektLoenn());
        assertEquals((Long) 7000L, lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektNaering());
        assertEquals((Long) 6000L, lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektBarePensjonsdel());
        assertEquals((Long) 5000L, lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardLoennLoennstrekkordningen());
        assertEquals((Long) 4000L, lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardPersoninntektNaering());
    }

    @Test
    public void mapToBeregnetSkattRequestWithInntektSkattWithNullValuesOk() {
        InntektSkatt inntektSkatt = new InntektSkatt();
        inntektSkatt.setPersoninntektFiskeFangstFamilieBarnehage(9000L);
        inntektSkatt.setPersoninntektLoenn(null);
        inntektSkatt.setPersoninntektNaering(7000L);
        inntektSkatt.setPersoninntektBarePensjonsdel(null);
        inntektSkatt.setSvalbardLoennLoennstrekkordningen(null);
        inntektSkatt.setSvalbardPersoninntektNaering(4000L);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper.
                mapToLagreBeregnetSkattRequest("2017", "12345678902", inntektSkatt);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678902", lagreBeregnetSkattRequest.getPersonIdent());
        assertEquals((Long) 9000L, lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektFiskeFangstFamilieBarnehage());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektLoenn());
        assertEquals((Long) 7000L, lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektNaering());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektBarePensjonsdel());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardLoennLoennstrekkordningen());
        assertEquals((Long) 4000L, lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardPersoninntektNaering());
    }

    @Test
    public void mapToBeregnetSkattRequestWithInntektSkattWithOnlyNullValuesOk() {
        InntektSkatt inntektSkatt = new InntektSkatt();
        inntektSkatt.setPersoninntektFiskeFangstFamilieBarnehage(null);
        inntektSkatt.setPersoninntektLoenn(null);
        inntektSkatt.setPersoninntektNaering(null);
        inntektSkatt.setPersoninntektBarePensjonsdel(null);
        inntektSkatt.setSvalbardLoennLoennstrekkordningen(null);
        inntektSkatt.setSvalbardPersoninntektNaering(null);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper
                .mapToLagreBeregnetSkattRequest("2017", "12345678902", inntektSkatt);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678902", lagreBeregnetSkattRequest.getPersonIdent());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektFiskeFangstFamilieBarnehage());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektLoenn());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektNaering());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getPersoninntektBarePensjonsdel());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardLoennLoennstrekkordningen());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt().getSvalbardPersoninntektNaering());
    }

    @Test
    public void mapToBeregnetSkattRequestWithInntektSkattNullOk() {
        InntektSkatt inntektSkatt = null;

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper.
                mapToLagreBeregnetSkattRequest("2017", "12345678902", inntektSkatt);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678902", lagreBeregnetSkattRequest.getPersonIdent());
        assertNull(lagreBeregnetSkattRequest.getInntektSkatt());
    }
}
