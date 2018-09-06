package no.nav.opptjening.loot;

import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.popp.tjenester.inntektskatt.v1.informasjon.InntektSkatt;

public class PensjonsgivendeInntektMapper {

    public InntektSkatt mapToInntektSkatt(PensjonsgivendeInntekt pensjonsgivendeInntekt) {
        if (pensjonsgivendeInntekt == null) {
            return null;
        }

        InntektSkatt inntektSkatt = new InntektSkatt();

        inntektSkatt.setPersoninntektFiskeFangstFamilieBarnehage(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektFiskeFangstFamiliebarnehage());
        inntektSkatt.setPersoninntektLoenn(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektLoenn());
        inntektSkatt.setPersoninntektNaering(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektNaering());
        inntektSkatt.setPersoninntektBarePensjonsdel(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektBarePensjonsdel());

        inntektSkatt.setSvalbardLoennLoennstrekkordningen(pensjonsgivendeInntekt.getSvalbardinntekt()
                .getSvalbardLoennLoennstrekkordningen());
        inntektSkatt.setSvalbardPersoninntektNaering(pensjonsgivendeInntekt.getSvalbardinntekt()
                .getSvalbardPersoninntektNaering());

        return inntektSkatt;
    }
}
