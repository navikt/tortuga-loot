package no.nav.opptjening.loot;

import no.nav.opptjening.loot.client.inntektskatt.InntektSKD;
import no.nav.opptjening.loot.client.inntektskatt.LagreBeregnetSkattRequest;

public class PensjonsgivendeInntektRecordMapper {

    public LagreBeregnetSkattRequest mapToLagreBeregnetSkattRequest(String inntektsaar, String personident, InntektSKD inntektSKD) {
        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();

        lagreBeregnetSkattRequest.setPersonIdent(personident);
        lagreBeregnetSkattRequest.setInntektsaar(inntektsaar);
        lagreBeregnetSkattRequest.setInntektSKD(inntektSKD);

        return lagreBeregnetSkattRequest;
    }
}
