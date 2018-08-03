package no.nav.opptjening.loot;


import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;

public class PensjonsgivendeInntektRecordMapper {

    private final static int INNTEKTSAAR = 0;
    private final static int PERSONIDENTIFIKATOR = 1;

    public static LagreBeregnetSkattRequest mapToLagreBeregnetSkattRequest(String key, InntektSkatt inntektSkatt) {

        String[] inntektsAarAndPersonIdentifikator = key.split("-");
        LagreBeregnetSkattRequest lagreBeregnetSkattRequest = new LagreBeregnetSkattRequest();

        lagreBeregnetSkattRequest.setPersonIdent(inntektsAarAndPersonIdentifikator[PERSONIDENTIFIKATOR]);
        lagreBeregnetSkattRequest.setInntektsaar(inntektsAarAndPersonIdentifikator[INNTEKTSAAR]);
        lagreBeregnetSkattRequest.setInntektSkatt(inntektSkatt);

        return lagreBeregnetSkattRequest;
    }
}
