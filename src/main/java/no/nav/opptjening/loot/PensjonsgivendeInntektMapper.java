package no.nav.opptjening.loot;

import no.nav.opptjening.loot.client.inntektskatt.InntektSKD;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;


public class PensjonsgivendeInntektMapper {

    public InntektSKD mapToInntektSkatt(PensjonsgivendeInntekt pensjonsgivendeInntekt) {
        if (pensjonsgivendeInntekt == null) {
            return null;
        }

        InntektSKD inntektSKD = new InntektSKD();

        inntektSKD.setPersoninntektFiskeFangstFamilieBarnehage(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektFiskeFangstFamiliebarnehage());
        inntektSKD.setPersoninntektLoenn(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektLoenn());
        inntektSKD.setPersoninntektNaering(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektNaering());
        inntektSKD.setPersoninntektBarePensjonsdel(pensjonsgivendeInntekt.getFastlandsinntekt()
                .getPersoninntektBarePensjonsdel());

        inntektSKD.setSvalbardLoennLoennstrekkordningen(pensjonsgivendeInntekt.getSvalbardinntekt()
                .getSvalbardLoennLoennstrekkordningen());
        inntektSKD.setSvalbardPersoninntektNaering(pensjonsgivendeInntekt.getSvalbardinntekt()
                .getSvalbardPersoninntektNaering());

        return inntektSKD;
    }
}
