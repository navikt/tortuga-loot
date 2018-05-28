package no.nav.opptjening.loot.entity;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;

public class PersonServiceTest {

    @Test(expected = PersonNotFoundException.class)
    public void when_Person_Does_Not_Exist_Then_Throw_Exception() {
        PersonService service = new PersonService(new PersonMockDao());
        service.finnPerson("1234");
    }

    @Test
    public void that_Person_Is_Returned_When_Fnr_Exist() {
        Person testPerson = Person.newBuilder()
                .withId(1)
                .withFnr("1234")
                .withDatoOpprettet(new Date(System.currentTimeMillis()))
                .withOpprettetAv("TORTUGA")
                .withDatoEndret(new Date(System.currentTimeMillis()))
                .withEndretAv("TORTUGA")
                .withVersjon(1)
                .build();

        PersonService service = new PersonService(new PersonMockDao(testPerson));

        Assert.assertEquals(testPerson, service.finnPerson("1234"));
    }

    private class PersonMockDao implements PersonDao {
        private final Person[] personer;

        public PersonMockDao() {
            this.personer = null;
        }

        public PersonMockDao(Person ...personer) {
            this.personer = personer;
        }

        @Override
        public Person finnPerson(String fnr) throws SQLException {
            if (personer == null) {
                return null;
            }
            for (Person person : personer) {
                if (fnr.equals(person.getFnr())) {
                    return person;
                }
            }
            return null;
        }
    }
}
