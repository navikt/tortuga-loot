package no.nav.opptjening.loot;

import no.nav.opptjening.loot.client.inntektskatt.InntektSKD;
import no.nav.opptjening.loot.client.inntektskatt.LagreBeregnetSkattRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PensjonsgivendeInntektRecordMapperTest {

    private final PensjonsgivendeInntektRecordMapper pensjonsgivendeInntektRecordMapper = new PensjonsgivendeInntektRecordMapper();

    @Test
    void mapToBeregnetSkattRequestWithInntektSkattOk() {
        InntektSKD inntektSKD = new InntektSKD();
        inntektSKD.setPersoninntektFiskeFangstFamilieBarnehage(9000L);
        inntektSKD.setPersoninntektLoenn(8000L);
        inntektSKD.setPersoninntektNaering(7000L);
        inntektSKD.setPersoninntektBarePensjonsdel(6000L);
        inntektSKD.setSvalbardLoennLoennstrekkordningen(5000L);
        inntektSKD.setSvalbardPersoninntektNaering(4000L);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper
                .mapToLagreBeregnetSkattRequest("2017", "12345678901", inntektSKD);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678901", lagreBeregnetSkattRequest.getPersonIdent());
        assertEquals((Long) 9000L, lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals((Long) 8000L, lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektLoenn());
        assertEquals((Long) 7000L, lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektNaering());
        assertEquals((Long) 6000L, lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektBarePensjonsdel());
        assertEquals((Long) 5000L, lagreBeregnetSkattRequest.getInntektSKD().getSvalbardLoennLoennstrekkordningen());
        assertEquals((Long) 4000L, lagreBeregnetSkattRequest.getInntektSKD().getSvalbardPersoninntektNaering());
    }

    @Test
    void mapToBeregnetSkattRequestWithInntektSkattWithNullValuesOk() {
        InntektSKD inntektSKD = new InntektSKD();
        inntektSKD.setPersoninntektFiskeFangstFamilieBarnehage(9000L);
        inntektSKD.setPersoninntektLoenn(null);
        inntektSKD.setPersoninntektNaering(7000L);
        inntektSKD.setPersoninntektBarePensjonsdel(null);
        inntektSKD.setSvalbardLoennLoennstrekkordningen(null);
        inntektSKD.setSvalbardPersoninntektNaering(4000L);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper.
                mapToLagreBeregnetSkattRequest("2017", "12345678902", inntektSKD);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678902", lagreBeregnetSkattRequest.getPersonIdent());
        assertEquals((Long) 9000L, lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektFiskeFangstFamilieBarnehage());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektLoenn());
        assertEquals((Long) 7000L, lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektNaering());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektBarePensjonsdel());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getSvalbardLoennLoennstrekkordningen());
        assertEquals((Long) 4000L, lagreBeregnetSkattRequest.getInntektSKD().getSvalbardPersoninntektNaering());
    }

    @Test
    void mapToBeregnetSkattRequestWithInntektSkattWithOnlyNullValuesOk() {
        InntektSKD inntektSKD = new InntektSKD();
        inntektSKD.setPersoninntektFiskeFangstFamilieBarnehage(null);
        inntektSKD.setPersoninntektLoenn(null);
        inntektSKD.setPersoninntektNaering(null);
        inntektSKD.setPersoninntektBarePensjonsdel(null);
        inntektSKD.setSvalbardLoennLoennstrekkordningen(null);
        inntektSKD.setSvalbardPersoninntektNaering(null);

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper
                .mapToLagreBeregnetSkattRequest("2017", "12345678902", inntektSKD);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678902", lagreBeregnetSkattRequest.getPersonIdent());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektFiskeFangstFamilieBarnehage());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektLoenn());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektNaering());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getPersoninntektBarePensjonsdel());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getSvalbardLoennLoennstrekkordningen());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD().getSvalbardPersoninntektNaering());
    }

    @Test
    void mapToBeregnetSkattRequestWithInntektSkattNullOk() {
        InntektSKD inntektSKD = null;

        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = pensjonsgivendeInntektRecordMapper.
                mapToLagreBeregnetSkattRequest("2017", "12345678902", inntektSKD);

        assertEquals("2017", lagreBeregnetSkattRequest.getInntektsaar());
        assertEquals("12345678902", lagreBeregnetSkattRequest.getPersonIdent());
        assertNull(lagreBeregnetSkattRequest.getInntektSKD());
    }
}
