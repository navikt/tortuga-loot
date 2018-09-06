package no.nav.opptjening.loot;


import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;

public class PensjonsgivendeInntektRecordMapper {

    public LagreBeregnetSkattRequest mapToLagreBeregnetSkattRequest(String inntektsaar, String personident, InntektSkatt inntektSkatt) {
        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();

        lagreBeregnetSkattRequest.setPersonIdent(personident);
        lagreBeregnetSkattRequest.setInntektsaar(inntektsaar);
        lagreBeregnetSkattRequest.setInntektSkatt(inntektSkatt);

        return lagreBeregnetSkattRequest;
    }
}
