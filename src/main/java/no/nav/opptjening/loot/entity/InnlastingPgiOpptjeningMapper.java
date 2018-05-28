package no.nav.opptjening.loot.entity;

import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InnlastingPgiOpptjeningMapper {
    private static final Logger LOG = LoggerFactory.getLogger(InnlastingPgiOpptjeningMapper.class);

    private final PersonService personService;

    public InnlastingPgiOpptjeningMapper(PersonService personService) {
        this.personService = personService;
    }

    public InnlastingPgiOpptjening toInnlastningPGI(PensjonsgivendeInntekt pensjonsgivendeInntekt) {
        InnlastingPgiOpptjening.Builder builder = InnlastingPgiOpptjening.newBuilder();

        try {
            builder.withPersonId(personService.finnPerson(pensjonsgivendeInntekt.getPersonidentifikator()).getId());
        } catch (PersonNotFoundException e) {
            LOG.warn("Fant ikke person, setter personId = null", e);
            builder.withPersonId(null).withStatus("KLAR_TIL_FNR_KONTROLL");
        }

        // TODO: map fastlands- and svalbardsinntekt to PGI JSF/SVA/NARING
        builder.withPgiJsf("0")
                .withPgiLonn("0")
                .withPgiNaring("0");

        return builder.build();
    }
}
