package no.nav.opptjening.loot.entity;

import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class InnlastingPgiOpptjeningMapperTest {
    @Test
    public void toPensjonsgivendeInntekt() throws Exception {
        PensjonsgivendeInntekt pensjonsgivendeInntekt = PensjonsgivendeInntekt.newBuilder()
                .setPersonidentifikator("1234")
                .setInntektsaar("2017")
                .setFastlandsinntekt(Fastlandsinntekt.newBuilder().setPersoninntektBarePensjonsdel(1)
                        .setPersoninntektFiskeFangstFamiliebarnehage(2)
                        .setPersoninntektLoenn(3)
                        .setPersoninntektNaering(4)
                        .build())
                .setSvalbardinntekt(Svalbardinntekt.newBuilder()
                        .setSvalbardLoennLoennstrekkordningen(5)
                        .setSvalbardPersoninntektNaering(6)
                        .build())
                .build();

        InnlastingPgiOpptjening innlastingPgiOpptjening = new InnlastingPgiOpptjeningMapper(new PersonService(new PersonMockDao())).toInnlastningPGI(pensjonsgivendeInntekt);

        Assert.assertEquals("0", innlastingPgiOpptjening.getPgiJsf());
        Assert.assertEquals("0", innlastingPgiOpptjening.getPgiLonn());
        Assert.assertEquals("0", innlastingPgiOpptjening.getPgiNaring());
        Assert.assertNull(innlastingPgiOpptjening.getPersonId());
    }

    private class PersonMockDao implements PersonDao {
        @Override
        public Person finnPerson(String fnr) throws SQLException {
            return null;
        }
    }
}
