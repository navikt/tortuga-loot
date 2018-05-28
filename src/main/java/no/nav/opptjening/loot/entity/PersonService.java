package no.nav.opptjening.loot.entity;

import io.prometheus.client.Counter;

import java.sql.SQLException;

public class PersonService {

    private static final Counter nonExistingPersonCounter = Counter.build()
            .name("person_not_found_counter")
            .help("Antall personer som ikke finnes i T_PERSON").register();

    private final PersonDao dao;

    public PersonService(PersonDao dao) {
        this.dao = dao;
    }

    public Person finnPerson(String fnr) {
        try {
            Person p = dao.finnPerson(fnr);

            if (p == null) {
                nonExistingPersonCounter.inc();
                throw new PersonNotFoundException("Fant ikke person med fnr=" + fnr);
            }

            return p;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
