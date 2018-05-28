package no.nav.opptjening.loot.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface PersonRowMapper {
    Person toPerson(ResultSet rs) throws SQLException;

    class DefaultPersonRowMapper implements PersonRowMapper {

        public Person toPerson(ResultSet rs) throws SQLException {
            return Person.newBuilder()
                    .withId(rs.getLong("PERSON_ID"))
                    .withFnr(rs.getString("FNR_FK"))
                    .withDatoOpprettet(rs.getDate("DATO_OPPRETTET"))
                    .withOpprettetAv(rs.getString("OPPRETTET_AV"))
                    .withDatoEndret(rs.getDate("DATO_ENDRET"))
                    .withEndretAv(rs.getString("ENDRET_AV"))
                    .withVersjon(rs.getLong("VERSJON"))
                    .build();
        }
    }
}
